package com.kh.lifeFit.repository.healthDataRepository;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.healthData.QHealthData;
import com.kh.lifeFit.dto.healthData.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
public class HealthDataQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QHealthData h = QHealthData.healthData;

    public HealthDataQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

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
        long totalUserCount = getTotalUserCount(filter);
        long highRiskCount = getHighRiskCount(filter);
        long cautionCount = getCautionCount(filter);
        long normalCount = getNormalCount(filter);

        return new HealthDataSummaryResponse(totalUserCount, highRiskCount, cautionCount, normalCount);
    }

    private long getTotalUserCount(HealthDataSummaryRequest filter) {
        Long count = jpaQueryFactory
                .select(h.userId.countDistinct())
                .from(h)
                .where(
                        baseCondition(filter)
                                .and(isLatest(filter))
                )
                .fetchOne();
        return count == null ? 0 : count;
    }

    private long getHighRiskCount(HealthDataSummaryRequest filter) {
        Long count = jpaQueryFactory
                .select(h.userId.countDistinct())
                .from(h)
                .where(
                        baseCondition(filter)
                                .and(isLatest(filter))
                                .and(highRiskPredicate())
                )
                .fetchOne();
        return count == null ? 0 : count;
    }

    private long getCautionCount(HealthDataSummaryRequest filter) {
        Long count = jpaQueryFactory
                .select(h.userId.countDistinct())
                .from(h)
                .where(
                        baseCondition(filter)
                                .and(isLatest(filter))
                                .and(normalPredicate().not())
                                .and(highRiskPredicate().not())
                )
                .fetchOne();
        return count == null ? 0 : count;
    }

    private long getNormalCount(HealthDataSummaryRequest filter) {
        Long count = jpaQueryFactory
                .select(h.userId.countDistinct())
                .from(h)
                .where(
                        baseCondition(filter)
                                .and(isLatest(filter))
                                .and(normalPredicate())
                )
                .fetchOne();
        return count == null ? 0 : count;
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

    private BooleanBuilder baseCondition(HealthDataSummaryRequest filter) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(deptLike(filter.getDept(), h));
        builder.and(recordedDateFrom(filter.getStartDate(), h));
        builder.and(recordedDateTo(filter.getEndDate(), h));
        builder.and(checkGender(filter.getGender(), h));
        builder.and(checkBmiRange(filter.getBmi(), h));
        builder.and(checkBloodSugarRange(filter.getBloodSugar(), h));
        builder.and(checkBloodPressureRange(filter.getBloodPressure(), h));
        builder.and(checkupDateRange(filter.getCheckupDate(), h));
        builder.and(h.checkupDate.isNotNull());

        return builder;

    }

    private BooleanBuilder baseCondition(QHealthData h2, HealthDataSummaryRequest filter) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(deptLike(filter.getDept(), h2));
        builder.and(recordedDateFrom(filter.getStartDate(), h2));
        builder.and(recordedDateTo(filter.getEndDate(), h2));
        builder.and(checkGender(filter.getGender(), h2));
        builder.and(checkBmiRange(filter.getBmi(), h2));
        builder.and(checkBloodSugarRange(filter.getBloodSugar(), h2));
        builder.and(checkBloodPressureRange(filter.getBloodPressure(), h2));
        builder.and(checkupDateRange(filter.getCheckupDate(), h2));
        builder.and(h2.checkupDate.isNotNull());

        return builder;

    }

    private BooleanExpression nameLike(String name, QHealthData h2) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return h2.userName.containsIgnoreCase(name);
    }

    private BooleanExpression deptLike(String dept, QHealthData h) {
        if (!StringUtils.hasText(dept)) {
            return null;
        }
        return h.userDepartment.containsIgnoreCase(dept);
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

    private BooleanExpression normalPredicate() {
        return h.bmi.between(18.5, 22.9)
                .and(h.bloodSugar.between(70, 99))
                .and(h.systolic.lt(120).and(h.diastolic.lt(80)));
    }

    private BooleanExpression highRiskPredicate() {
        return h.bmi.goe(25.0)
                .or(h.bloodSugar.goe(126))
                .or(h.systolic.goe(140).or(h.diastolic.goe(90)));
    }

    private BooleanExpression isLatest(HealthDataSummaryRequest filter) {
        QHealthData h2 = new QHealthData("h2");

        return h.checkupDate.eq(
                JPAExpressions
                        .select(h2.checkupDate.max())
                        .from(h2)
                        .where(
                                h2.userId.eq(h.userId)
                                        .and(baseCondition(h2, filter))
                        )
        );
    }

}