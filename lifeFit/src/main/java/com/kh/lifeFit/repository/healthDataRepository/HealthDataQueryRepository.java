package com.kh.lifeFit.repository.healthDataRepository;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.healthData.QHealthData;
import com.kh.lifeFit.dto.healthData.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HealthDataQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QHealthData h = QHealthData.healthData;

    private final EntityManager em;

    public Page<HealthDataResponse> selectHealthDataList(HealthDataFilterRequest filter, Pageable pageable) {

        if (filter == null) {
            filter = new HealthDataFilterRequest();
        }

        List<HealthDataResponse> list =
                jpaQueryFactory
                        .select(new QHealthDataResponse(
                                h.id,
                                h.userName,
                                h.userDepartment,
                                h.recordedDate,
                                h.userGender,
                                h.bmi,
                                h.bloodSugar,
                                h.systolic,
                                h.diastolic,
                                h.checkupDate))
                        .from(h)
                        .where(
                                baseCondition(filter)
                                        .and(nameLike(filter.getName(), h))
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(h.recordedDate.desc())
                        .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(h.count())
                .from(h)
                .where(
                        baseCondition(filter)
                                .and(nameLike(filter.getName(), h))
                );

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    public HealthDataSummaryResponse getHealthDataSummary(HealthDataSummaryRequest filter) {

        if (filter == null) {
            filter = new HealthDataSummaryRequest();
        }
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        // 1. SELECT 절: 한번에 모든 통계 집계 (case when 활용) - total, caution, highRisk, normal
        sql.append("select ");
        sql.append(" count(h.user_id) as total_count, "); // totalCount
        sql.append(" coalesce(sum(case when (h.bmi >= 25.0 or h.blood_sugar >= 126 or h.systolic >= 140 or h.diastolic >= 90) then 1 else 0 end), 0) as high_risk, ");
        sql.append(" coalesce(sum(case when (h.bmi between 18.5 and 22.9 and h.blood_sugar between 70 and 99 and h.systolic < 120 and h.diastolic <80) then 1 else 0 end), 0) as normal ");

        sql.append(" from health_data h ");
        sql.append(" inner join ( "); // 서브쿼리를 테이블 취급하여 조인함

        // 인덱스 태워서 id만 빠르게 뽑는 구간 (커버링 인덱스)
        sql.append(" select t.health_data_id ");
        sql.append(" from ( ");
        sql.append(" select sub.health_data_id, "); // pk만 조회하여 속도 향상
        sql.append(" row_number() over(partition by sub.user_id order by sub.checkup_date desc) as rn ");
        sql.append(" from health_data sub ");
        sql.append(" where 1 = 1 ");

        if (StringUtils.hasText(filter.getDept())) {
            sql.append(" and sub.user_department like ?").append(paramIndex++);
            params.add(filter.getDept() + "%");
        }

        if (filter.getStartDate() != null) {
            sql.append(" and sub.recorded_date >= ?").append(paramIndex++);
            params.add(filter.getStartDate().atStartOfDay());
        }

        if (filter.getEndDate() != null) {
            sql.append(" and sub.recorded_date < ?").append(paramIndex++);
            params.add(filter.getEndDate().plusDays(1).atStartOfDay());
        }

        if (filter.getGender() != null) {
            sql.append(" and sub.user_gender = ?").append(paramIndex++);
            params.add(filter.getGender().name());
        }

        sql.append(" and sub.checkup_date is not null ");

        sql.append(" ) t ");
        sql.append(" where t.rn = 1 ");

        // 찾아낸 id(target)와 원본 테이블(h)을 조인하여 필요한 데이터만 가져옴
        sql.append(") target on h.health_data_id = target.health_data_id ");

        // where 절 - 통계 필터 (h 테이블 기준)
        sql.append("where 1=1 ");

        if (StringUtils.hasText(filter.getBmi())) {
            switch (filter.getBmi()) {
                case "underweight" : sql.append(" and h.bmi < 18.5 "); break;
                case "normal" : sql.append(" and h.bmi between 18.5 and 22.9 "); break;
                case "overweight" : sql.append(" and h.bmi between 23.0 and 24.9 "); break;
                case "obese" : sql.append(" and h.bmi >= 25.0 "); break;
                default: break;
            }
        }

        if (StringUtils.hasText(filter.getBloodSugar())) {
            switch (filter.getBloodSugar()) {
                case "normal" : sql.append(" and h.blood_sugar <= 99 "); break;
                case "pre_diabetes" : sql.append(" and h.blood_sugar between 100 and 125"); break;
                case "diabetes" : sql.append(" and h.blood_sugar >= 126 "); break;
                default: break;
            }
        }

        if (StringUtils.hasText(filter.getBloodPressure())) {
            switch (filter.getBloodPressure()) {
                case "normal" : sql.append(" and h.systolic < 120 and h.diastolic < 80"); break;
                case "pre_hypertension" : sql.append("  and ((h.systolic between 120 and 139) or (h.diastolic between 80 and 89)) ");  break;
                case "hypertension" : sql.append(" and (h.systolic >= 140 or h.diastolic >= 90) "); break;
                default: break;
            }
        }

        // 쿼리 실행
        Query nativeQuery = em.createNativeQuery(sql.toString());

        // 파라미터 바인딩
        for (int i = 0; i < params.size(); i++) {
            nativeQuery.setParameter(i + 1, params.get(i));
        }

        try {
            // 결과 매핑
            Object resultObj = nativeQuery.getSingleResult(); // 변수 타입: 객체 -> Object[] {a, b, c} 형태

            Object[] result = (Object[]) resultObj; // 변수 타입: 객체 배열 -> Object[] {a, b, c} 형태
            // resultObj와 result는 같은 객체 배열을 참조함
            // 객체배열로 캐스팅 해주는 이유는 같은 객체를 가리키더라도 변수의 컴파일 타입이 다르면 사용할 수 있는 메서드가 다르기 때문!

            long total = ((Number) result[0]).longValue();
            long highRisk = ((Number) result[1]).longValue();
            long normal = ((Number) result[2]).longValue();
            long caution = total - highRisk - normal;

            return new HealthDataSummaryResponse(total, highRisk, caution, normal);
        } catch (NoResultException e) {
            return new HealthDataSummaryResponse(0, 0, 0, 0);
        }

    }

    private BooleanBuilder baseCondition(HealthDataFilterRequest filter) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(deptLike(filter.getDept(), h));
        builder.and(recordedDateFrom(filter.getStartDate(), h));
        builder.and(recordedDateTo(filter.getEndDate(), h));
        builder.and(checkGender(filter.getGender(), h));
        builder.and(checkBmiRange(filter.getBmi(), h));
        builder.and(checkBloodSugarRange(filter.getBloodSugar(), h));
        builder.and(checkBloodPressureRange(filter.getBloodPressure(), h));
        builder.and(checkupDateRange(filter.getCheckupDate(), h));

        return builder;

    }

    private BooleanExpression nameLike(String name, QHealthData h2) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return h2.userName.startsWith(name);
    }

    private BooleanExpression deptLike(String dept, QHealthData h) {
        if (!StringUtils.hasText(dept)) {
            return null;
        }
        return h.userDepartment.startsWith(dept);
    }

    private BooleanExpression recordedDateFrom(LocalDate startDate, QHealthData h) {
        if (startDate == null) {
            return null;
        }
        return h.recordedDate.goe(startDate.atStartOfDay());
    }

    private BooleanExpression recordedDateTo(LocalDate endDate, QHealthData h) {
        if (endDate == null) {
            return null;
        }
        endDate = endDate.plusDays(1);
        return h.recordedDate.before(endDate.atStartOfDay());
    }

    private BooleanExpression checkGender(Gender gender, QHealthData h) {
        if (gender == null) {
            return null;
        }
        return h.userGender.eq(gender);
    }

    private BooleanExpression checkBmiRange(String bmi, QHealthData h) {
        if (!StringUtils.hasText(bmi)) {
            return null;
        }

        return switch (bmi) {
            case "underweight" -> h.bmi.lt(18.5);
            case "normal" -> h.bmi.between(18.5, 22.9);
            case "overweight" -> h.bmi.between(23.0, 24.9);
            case "obese" -> h.bmi.goe(25.0);
            default -> null;
        };

    }

    private BooleanExpression checkBloodSugarRange(String bloodSugar, QHealthData h) {
        if (!StringUtils.hasText(bloodSugar)) {
            return null;
        }
        return switch (bloodSugar) {
            case "normal" -> h.bloodSugar.loe(99);
            case "pre_diabetes" -> h.bloodSugar.between(100, 125);
            case "diabetes" -> h.bloodSugar.goe(126);
            default -> null;
        };
    }

    private BooleanExpression checkBloodPressureRange(String bloodPressure, QHealthData h) {
        if (!StringUtils.hasText(bloodPressure)) {
            return null;
        }
        return switch (bloodPressure) {
            case "normal" -> h.systolic.lt(120).and(h.diastolic.lt(80));
            case "pre_hypertension" -> h.systolic.between(120, 139).or(h.diastolic.between(80, 89));
            case "hypertension" -> h.systolic.goe(140).or(h.diastolic.goe(90));
            default -> null;
        };
    }

    private BooleanExpression checkupDateRange(String checkupDate, QHealthData h) {

        QHealthData sub = new QHealthData("sub");

        if (!StringUtils.hasText(checkupDate)) {
            return null;
        }
        return switch (checkupDate) {
            case "over_3m" -> h.checkupDate.eq(
                    JPAExpressions
                            .select(sub.checkupDate.max())
                            .from(sub)
                            .where(sub.userId.eq(h.userId))
            ).and(
                    h.checkupDate.lt(LocalDate.now().minusMonths(3))
            );
            case "over_6m" -> h.checkupDate.eq(
                    JPAExpressions
                            .select(sub.checkupDate.max())
                            .from(sub)
                            .where(sub.userId.eq(h.userId))
            ).and(
                    h.checkupDate.lt(LocalDate.now().minusMonths(6))
            );
            case "over_1y" -> h.checkupDate.eq(
                    JPAExpressions
                            .select(sub.checkupDate.max())
                            .from(sub)
                            .where(sub.userId.eq(h.userId))
            ).and(
                    h.checkupDate.lt(LocalDate.now().minusYears(1))
            );
            default -> null;
        };

    }

}