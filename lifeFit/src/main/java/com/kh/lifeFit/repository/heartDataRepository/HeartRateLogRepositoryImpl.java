package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.kh.lifeFit.domain.heartData.QHeartRateLog.heartRateLog;

@RequiredArgsConstructor
public class HeartRateLogRepositoryImpl implements HeartRateLogRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HeartRateLog> findLogsWithFilter(Long userId, ProcessStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable){

        // 리스트 조회
        List<HeartRateLog> content = queryFactory
                .selectFrom(heartRateLog)
                .where(
                        userIdEq(userId),
                        statusEq(status),
                        createdAtBetween(start, end)
                )
                .offset(pageable.getOffset())   // 페이지 시작 번호
                .limit(pageable.getPageSize())  // 페이지당 개수
                .orderBy(heartRateLog.createdAt.desc()) // 최신순 정렬
                .fetch();

        // 전체 건수 조회 (Page 객체 생성용)
        Long total = queryFactory
                .select(heartRateLog.count())
                .from(heartRateLog)
                .where(
                        userIdEq(userId),
                        statusEq(status),
                        createdAtBetween(start, end)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // 조건 메소드
    private BooleanExpression userIdEq(Long userId) {
        // 값이 null이면 조건절에서 아예 무시됨
        return userId != null ? heartRateLog.userId.eq(userId) : null;
    }

    private BooleanExpression statusEq(ProcessStatus status) {
        return status != null ? heartRateLog.processStatus.eq(status) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        // 둘 중 하나라도 없으면 날짜 필터링 제외
        if (start == null || end == null) return null;
        return heartRateLog.createdAt.between(start, end);
    }
}
