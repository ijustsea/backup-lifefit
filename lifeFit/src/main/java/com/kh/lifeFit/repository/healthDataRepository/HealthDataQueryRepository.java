package com.kh.lifeFit.repository.healthDataRepository;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.healthData.QHealthData;

import com.kh.lifeFit.dto.healthData.HealthDataFilterRequest;
import com.kh.lifeFit.dto.healthData.HealthDataResponse;
import com.kh.lifeFit.dto.healthData.QHealthDataResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HealthDataQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QHealthData h = QHealthData.healthData;

    public Page<HealthDataResponse> selectHealthDataList (HealthDataFilterRequest filter, Pageable pageable) {

        if(filter == null){
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
                                nameLike(filter.getName()),
                                deptLike(filter.getDept()),
                                startDate(filter.getStartDate()),
                                endDate(filter.getEndDate()),
                                checkGender(filter.getGender()),
                                checkBmiRange(filter.getBmi()),
                                checkBloodSugarRange(filter.getBloodSugar()),
                                checkBloodPressureRange(filter.getBloodPressure()),
                                checkupDateRange(filter.getCheckupDate())
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(h.recordedDate.desc())
                        .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(h.count())
                .from(h)
                .where(
                        nameLike(filter.getName()),
                        deptLike(filter.getDept()),
                        startDate(filter.getStartDate()),
                        endDate(filter.getEndDate()),
                        checkGender(filter.getGender()),
                        checkBmiRange(filter.getBmi()),
                        checkBloodSugarRange(filter.getBloodSugar()),
                        checkBloodPressureRange(filter.getBloodPressure()),
                        checkupDateRange(filter.getCheckupDate())
                );

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    private BooleanExpression nameLike(String name){
        if(!StringUtils.hasText(name)){
            return null;
        }
        return h.userName.containsIgnoreCase(name);
    }

    private BooleanExpression deptLike(String dept){
        if(!StringUtils.hasText(dept)){
            return null;
        }
        return h.userDepartment.containsIgnoreCase(dept);
    }

    private BooleanExpression startDate(LocalDate startDate){
        if(startDate == null){
            return null;
        }
        return h.recordedDate.goe(startDate.atStartOfDay());
    }
    private BooleanExpression endDate(LocalDate endDate){
        if(endDate == null){
            return null;
        }
        endDate = endDate.plusDays(1);
        return h.recordedDate.before(endDate.atStartOfDay());
    }

    private BooleanExpression checkGender(Gender gender){
        if(gender == null){
            return null;
        }
        return h.userGender.eq(gender);
    }

    private BooleanExpression checkBmiRange(String bmi) {
        if (!StringUtils.hasText(bmi)) {
            return null;
        }

        return switch (bmi) {
            case "underweight" -> h.bmi.lt(18.5);
            case "normal" -> h.bmi.between(18.5, 22.9);
            case "overweight" -> h.bmi.between(23, 24.9);
            case "obese" -> h.bmi.goe(25.0);
            default -> null;
        };

    }

    private BooleanExpression checkBloodSugarRange(String bloodSugar) {
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

    private BooleanExpression checkBloodPressureRange(String bloodPressure) {
        if (!StringUtils.hasText(bloodPressure)) {
            return null;
        }
        return switch (bloodPressure) {
            case "normal" -> h.systolic.lt(120).and(h.diastolic.lt(80));
            case "pre_hypertension" -> h.systolic.between(120, 139).and(h.diastolic.between(80, 89));
            case "hypertension" -> h.systolic.goe(140).or(h.diastolic.goe(90));
            default -> null;
        };
    }

    private BooleanExpression checkupDateRange(String checkupDate) {

        QHealthData h2 =  new QHealthData("h2");

        if (!StringUtils.hasText(checkupDate)) {
            return null;
        }
        return switch (checkupDate) {
            case "over_3m" ->
                    h.checkupDate.eq(
                        JPAExpressions
                            .select(h2.checkupDate.max())
                            .from(h2)
                            .where(h2.userId.eq(h.userId))
                    ).and(
                            h.checkupDate.lt(LocalDate.now().minusMonths(3))
                    );
            case "over_6m" ->
                    h.checkupDate.eq(
                            JPAExpressions
                                .select(h2.checkupDate.max())
                                .from(h2)
                                .where(h2.userId.eq(h.userId))
                    ).and(
                            h.checkupDate.lt(LocalDate.now().minusMonths(6))
                    );
            case "over_1y" ->
                    h.checkupDate.eq(
                            JPAExpressions
                                .select(h2.checkupDate.max())
                                .from(h2)
                                .where(h2.userId.eq(h.userId))
                    ).and(
                            h.checkupDate.lt(LocalDate.now().minusYears(1))
                    );
            default -> null;
        };

    }

}
