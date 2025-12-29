package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataChartDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataListDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
    public List<HeartRateData> findPollingData(Long userId, Long lastId){

        return queryFactory
                .selectFrom(heartRateData)
                .where(
                        heartRateData.userId.eq(userId),
                        heartRateData.id.gt(lastId)             // ID 기반 커서 방식
                )
                .orderBy(heartRateData.measuredAt.asc())
                .fetch();
    }
}
