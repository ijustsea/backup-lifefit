package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertListDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertStatsDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataChartDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataListDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.kh.lifeFit.domain.heartData.QHeartRateData.heartRateData;

@RequiredArgsConstructor
public class HeartRateDataRepositoryImpl implements HeartRateDataRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    //================================ 실시간 심박수 ==============================================
    @Override
    public HeartDataStatsDto findStatsByHeartRate(Long userId) {

        // 심박수 통계 '오늘'로 기간 설정 -> 오늘 00:00:00
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        // 현재 심박수 조회
        Integer currentHeartRate = queryFactory
                .select(heartRateData.heartRate)
                .from(heartRateData)
                .where(heartRateData.userId.eq(userId))
                .orderBy(heartRateData.measuredAt.desc())
                .fetchFirst(); // 최신순 1건

        // 심박수 데이터가 없는 경우
        if (currentHeartRate == null) {
            return new HeartDataStatsDto(0, 0, 0, null);
        }

        // 평균 & 최대 심박수 조회
        // 한 행에 두 컬럼이 나옴 => Tuple 사용
        Tuple stats = queryFactory
                .select(
                        heartRateData.heartRate.avg(),
                        heartRateData.heartRate.max()
                )
                .from(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.measuredAt.goe(startOfToday)
                )
                .fetchOne();

        // 현재 심박수 + 평균 & 최대 심박수 결합
        Integer avgHeartRate = 0;
        Integer maxHeartRate = 0;
        // Double -> Integer 변환 로직 & stats가 null일 경우
        if(stats != null){
            Double averageHeartRate = stats.get(heartRateData.heartRate.avg()); // 타입 에러 방지를 위해 Double로 꺼내기
            if (averageHeartRate != null) {
                avgHeartRate = (int) Math.round(averageHeartRate); // 반올림으로 정수값 받기
            }
            maxHeartRate = stats.get(heartRateData.heartRate.max());
        }

        // 결합된 1개의 객체
        return new HeartDataStatsDto(
                currentHeartRate,
                avgHeartRate,
                maxHeartRate != null ? maxHeartRate : 0,
                null
        );
    }

    // 상단 '최근 30분간 심박수 추이'
    @Override
    public List<HeartDataChartDto> findChartData(Long userId, LocalDateTime startTime){

        // 'HH:mm' 문자열로 변환
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                heartRateData.measuredAt,
                ConstantImpl.create("%H:%i")
        );

        return queryFactory
                .select(Projections.constructor(HeartDataChartDto.class,
                        formattedDate,  // time, 1분 단위로 잘린 시간 (String)
                        heartRateData.heartRate.avg().round().castToNum(Integer.class) // value, 해당 1분간의 평균 심박수 (Double)-> (Integer)형변환
                        ))
                .from(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.measuredAt.goe(startTime)) // startTime 이후 데이터만
                .groupBy(formattedDate)                          // 분단위 문자열로 그룹
                .orderBy(formattedDate.asc())         // chart는 왼쪽(과거)->오른쪽(최신)이라 오름차순
                .fetch();
    }

    // [추가] 실시간 대시보드용 1분 단위 집계 리스트 조회
    @Override
    public List<HeartDataListDto> findRecentDataList(Long userId, Pageable pageable) {

        // 1분 단위로 그룹화하기 위한 포맷 (HH:mm)
        StringTemplate formattedMinute = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                heartRateData.measuredAt,
                ConstantImpl.create("%Y-%m-%d %H:%i")
        );

        return queryFactory
                .select(Projections.constructor(HeartDataListDto.class,
                        heartRateData.measuredAt.max(), // 1. measuredAt (해당 분의 마지막 시간)
                        heartRateData.heartRate.avg().round().castToNum(Integer.class), // 2. heartRate (평균)
                        Expressions.asNumber(0).as("variation"), // 3. variation (집계 시 0으로 처리)
                        Expressions.asString("").as("timeAge"),  // 4. timeAge (서비스에서 계산)
                        heartRateData.status.max() // 5. status (해당 분의 최악 상태)
                ))
                .from(heartRateData)
                .where(heartRateData.userId.eq(userId))
                .groupBy(formattedMinute)
                .orderBy(formattedMinute.desc()) // 최신 분이 위로 오도록
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }


    //================================ 심박수 알림 내역 ==============================================

    //리스트 조회
    @Override
    public Page<HeartAlertListDto> findAlertList(Long userId, HeartAlertSearchRequest request, Pageable pageable){

        // 리스트 조회
        List<HeartAlertListDto> list = queryFactory
                .select(Projections.constructor(HeartAlertListDto.class,
                        heartRateData.measuredAt,
                        heartRateData.heartRate,
                        heartRateData.status
                ))
                .from(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.status.ne(HeartRateStatus.NORMAL),
                        statusEq(request.getStatus()),
                        dateFilter(request.getStartDate(), request.getEndDate())
                )
                .orderBy(heartRateData.measuredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 데이터 전체 개수 조회 (페이징 계산용)
        Long tatal = queryFactory
                .select(heartRateData.count())
                .from(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.status.ne(HeartRateStatus.NORMAL),
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
                        heartRateData.count().as("totalCount"),
                        heartRateData.status.when(HeartRateStatus.CAUTION).then(1).otherwise(0).sum().as("cautionCount"),
                        heartRateData.status.when(HeartRateStatus.DANGER).then(1).otherwise(0).sum().as("dangerCount")
                ))
                .from(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.status.ne(HeartRateStatus.NORMAL),
                        statusEq(request.getStatus()),
                        dateFilter(request.getStartDate(), request.getEndDate())
                )
                .fetchOne();
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

    @Override
    public LocalDate getLatestDate(Long userId){
        LocalDateTime maxTime = queryFactory
                .select(heartRateData.measuredAt.max())
                .from(heartRateData)
                .where(heartRateData.userId.eq(userId))
                .fetchOne();

        // 데이터가 없으면 오늘 날짜 반환(에러 방지) + 데이터 있으면 가장 최근 날짜 반환
        return (maxTime != null) ? maxTime.toLocalDate() : LocalDate.now();
    }

}
