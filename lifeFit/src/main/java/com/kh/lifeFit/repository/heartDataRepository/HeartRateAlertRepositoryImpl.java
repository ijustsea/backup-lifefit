package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertListDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertStatsDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.kh.lifeFit.domain.heartData.QHeartRateAlert.heartRateAlert;
import static com.kh.lifeFit.domain.heartData.QHeartRateData.heartRateData;

@RequiredArgsConstructor
public class HeartRateAlertRepositoryImpl implements HeartRateAlertRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //================================ 심박수 알림 내역 ==============================================

    //리스트 조회 (HeartRateAlert 기준 조회 + HeartRateData 조인)
    @Override
    public Page<HeartAlertListDto> findAlertList(Long userId, HeartAlertSearchRequest request, Pageable pageable){

        // 리스트 조회
        List<HeartAlertListDto> list = queryFactory
                .select(Projections.constructor(HeartAlertListDto.class,
                        heartRateData.measuredAt,
                        heartRateData.heartRate,
                        heartRateData.status
                ))
                .from(heartRateAlert)
                .join(heartRateAlert.heartRateData, heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        statusEq(request.getStatus()),
                        dateFilter(request.getStartDate(), request.getEndDate())
                )
                .orderBy(heartRateData.measuredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 데이터 전체 개수 조회 (페이징 계산용)
        Long tatal = queryFactory
                .select(heartRateAlert.count())
                .from(heartRateAlert)
                .join(heartRateAlert.heartRateData, heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        statusEq(request.getStatus()),
                        dateFilter(request.getStartDate(), request.getEndDate())
                )
                .fetchOne();
        // PageImpl 객체로 감싸서 반환 (리스트, 페이징 정보, 전체 개수)
        return new PageImpl<>(list, pageable, tatal != null ? tatal : 0L);
    }

    // 심박수 알림 통계
    @Override
    public HeartAlertStatsDto findAlertStats(Long userId, HeartAlertSearchRequest request){
        // 기간 설정 (오늘 00:00:00부터 현재)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfday = LocalDateTime.now().with(LocalDateTime.MAX);

        // 알림 통계 (총 알림 건수 | 경고 건수 | 위험 건수) ->
        return queryFactory
                .select(Projections.constructor(HeartAlertStatsDto.class,
                        heartRateAlert.count().as("totalCount"),
                        heartRateData.status.when(HeartRateStatus.CAUTION).then(1).otherwise(0).sum().as("cautionCount"),
                        heartRateData.status.when(HeartRateStatus.DANGER).then(1).otherwise(0).sum().as("dangerCount")
                ))
                .from(heartRateAlert)
                .join(heartRateAlert.heartRateData, heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        dateFilter(request.getStartDate(), request.getEndDate())
                )
                .fetchOne();
    }

    // 알림 발생 날짜 조회
    @Override
    public LocalDate getLatestAlertDate(Long userId) {
        LocalDateTime maxTime = queryFactory
                .select(heartRateData.measuredAt.max())
                .from(heartRateAlert)
                .join(heartRateAlert.heartRateData, heartRateData)
                .where(heartRateData.userId.eq(userId))
                .fetchOne();

        // 데이터가 없으면 오늘 날짜 반환(에러 방지) + 데이터 있으면 가장 최근 날짜 반환
        return (maxTime != null) ? maxTime.toLocalDate() : LocalDate.now();
    }

    // == 공통 조건 ==
    // 상태 필터 로직
    private BooleanExpression statusEq(HeartRateStatus status) {
        // '전체' 선택시 status가 null로 들어오면 조건을 null로 반환
        return status != null ? heartRateData.status.eq(status) : null;
    }

    // 날짜 범위 로직
    private BooleanExpression dateFilter(LocalDate startDate, LocalDate endDate) {
        // 기간 조회
        // 첫 진입 or 날짜 선택 안 했을 경우 기간 제한 없이 전체 조회
        if(startDate == null || endDate == null){
            return  null;
        }
        // 기간 조회 있는 경우
        return heartRateData.measuredAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

}
