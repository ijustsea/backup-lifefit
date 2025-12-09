package com.kh.lifeFit.repository.SleepRepository;


/*
 * 참고: QueryDSL 사용은
 * select 조회
 * 조건 검색
 * 통계 조회
 * join, groupBy
 * 동적 쿼리
 * 저장(insert) 기능을 위한 도구 아님
 * */

import com.kh.lifeFit.domain.sleepManager.SleepRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SleepRecordRepositoryImpl implements SleepRecordRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public SleepRecord saveSleepRecord(SleepRecord sleepRecord) {
        em.persist(sleepRecord);
        return sleepRecord;
    }
}
