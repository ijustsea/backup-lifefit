package com.kh.lifeFit.repository.healthDataRepository;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.healthData.HealthData;
import com.kh.lifeFit.domain.healthData.QHealthData;

import com.kh.lifeFit.dto.healthData.HealthDataFilterRequest;
import com.kh.lifeFit.dto.healthData.HealthDataResponse;
import com.kh.lifeFit.dto.healthData.QHealthDataResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
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
                                checkGender(filter.getGender())

                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(h.recordedDate.desc())
                        .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(h.count())
                .from(h);

        return PageableExecutionUtils.getPage(list, pageable, () -> countQuery.fetchOne());
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
        return h.recordedDate.after(startDate.atStartOfDay());
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

        switch (bmi) {
            case "underweight":
                return h.bmi.lt(18.5);
            case "normal":
                return h.bmi.between(18.5, 22.9);
            case "overweight":
                return h.bmi.between(23, 24.9);
            case "obese":
                return h.bmi.goe(25.0);
            default:
                return null;
        }
    }

}
