package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.kh.lifeFit.domain.heartData.QHeartRateData.heartRateData;

@RequiredArgsConstructor
public class HeartRateDataRepositoryImpl implements HeartRateDataRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public HeartDataStatsDto findStatsByHeartRate(Long userId) {

        // 현재 심박수 조회
        Integer currentHeartRate = queryFactory
                .select(heartRateData.heartRate)
                .from(heartRateData)
                .where(heartRateData.userId.eq(userId))
                .orderBy(heartRateData.measuredAt.desc())
                .fetchFirst(); // 최신순 1건

        // 심박수 데이터가 없는 경우
        if (currentHeartRate == null) {
            return new HeartDataStatsDto(0, 0.0, 0);
        }

        // 평균 & 최대 심박수 조회
        // 한 행에 두 컬럼이 나옴 => Tuple 사용
        Tuple stats = queryFactory
                .select(
                        heartRateData.heartRate.avg(),
                        heartRateData.heartRate.max()
                )
                .from(heartRateData)
                .where(heartRateData.userId.eq(userId))
                .fetchOne();

        // 현재 심박수 + 평균 & 최대 심박수 결합
        // stats가 null일 경우
        Double avgHeartRate = (stats != null) ? stats.get(heartRateData.heartRate.avg()) : 0.0;
        Integer maxHeartRate = (stats != null) ? stats.get(heartRateData.heartRate.max()) : 0;

        // 결합된 1개의 객체
        return new HeartDataStatsDto(
                currentHeartRate,
                avgHeartRate != null ? avgHeartRate : 0.0,
                maxHeartRate != null ? maxHeartRate : 0
        );
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
