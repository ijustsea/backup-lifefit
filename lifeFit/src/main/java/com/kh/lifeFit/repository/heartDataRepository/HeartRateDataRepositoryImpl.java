package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataChartDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.kh.lifeFit.domain.heartData.QHeartRateData.heartRateData;

@RequiredArgsConstructor
public class HeartRateDataRepositoryImpl implements HeartRateDataRepositoryCustom{

    private final JPAQueryFactory queryFactory;

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
        return queryFactory
                .select(Projections.constructor(HeartDataChartDto.class,
                        heartRateData.measuredAt,  // time
                        heartRateData.heartRate))  // value
                .from(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.measuredAt.goe(startTime)) // startTime 이후 데이터만
                .orderBy(heartRateData.measuredAt.asc())         // chart는 왼쪽(과거)->오른쪽(최신)이라 오름차순
                .fetch();
    }


    @Override
    public List<HeartRateData> findRecentData(Long userId, Pageable pageable) {
        return queryFactory
                .selectFrom(heartRateData)
                .where(heartRateData.userId.eq(userId))
                .orderBy(heartRateData.measuredAt.desc()) // 최신순
                .offset(pageable.getOffset())   // 페이지 번호 계산 (0부터 시작)
                .limit(pageable.getPageSize())  // 가져올 개수 (limit)
                .fetch();
    }
}
