package com.kh.lifeFit.service.groupBuyService;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
import com.kh.lifeFit.domain.supply.Supply;
import com.kh.lifeFit.domain.supply.SupplyStatus;
import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.domain.user.UserType;
import com.kh.lifeFit.repository.groupBuyRepository.GroupBuyInfoRepository;
import com.kh.lifeFit.repository.groupBuyRepository.GroupBuyRepository;
import com.kh.lifeFit.repository.supplyRepository.SupplyRepository;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@EnableRetry
@ActiveProfiles("test")
@SpringBootTest
class GroupBuyConcurrencyTest {

    @Autowired
    private GroupBuyService groupBuyService;

    @Autowired
    private GroupBuyInfoRepository groupBuyInfoRepository;

    @Autowired
    private GroupBuyRepository groupBuyRepository;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private UserRepository userRepository;

    private Long groupBuyInfoId;
    private List<Long> userIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // ===============================
        // 1️⃣ 테스트용 User 20명 생성
        // ===============================
        for (int i = 1; i <= 20; i++) {
            User user = new User();
            user.setEmail("test" + i + "@test.com");
            user.setPassword("password");
            user.setName("테스트유저" + i);
            user.setGender(Gender.MALE);
            user.setAge(20 + i);
            user.setType(UserType.EMPLOYEE);

            User savedUser = userRepository.save(user);
            userIds.add(savedUser.getId()); // ⭐ 핵심
        }

        // ===============================
        // 2️⃣ 테스트용 Supply 생성
        // ===============================
        Supply supply = Supply.create(
                "테스트 영양제",
                30000L,
                100L,
                "TestBrand",
                60L,
                LocalDate.now().plusYears(1),
                "테스트 상세",
                "img.png",
                SupplyStatus.GROUP
        );
        supplyRepository.save(supply);

        // ===============================
        // 3️⃣ 공동구매 정보 생성 (재고 10)
        // ===============================
        GroupBuyInfo info = GroupBuyInfo.create(
                supply,
                10L,                 // totalStock
                20L,                 // discount
                LocalDate.now().plusDays(7)
        );
        groupBuyInfoRepository.save(info);
        groupBuyInfoId = info.getId();
    }

    @Test
    void 공동구매_동시성_테스트() throws InterruptedException {

        int threadCount = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // ===============================
        // 20명이 동시에 공동구매 참여
        // ===============================
        for (Long userId : userIds) {
            executorService.submit(() -> {
                try {
                    groupBuyService.participate(groupBuyInfoId, userId);
                } catch (Exception e) {
                    // 재고 부족 / OptimisticLock 예외 → 정상
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // ===============================
        // 결과 검증
        // ===============================
        GroupBuyInfo result = groupBuyInfoRepository.findById(groupBuyInfoId)
                .orElseThrow();

        long buyCount =
                groupBuyRepository.countByGroupBuyInfoIdAndStatus(
                        groupBuyInfoId,
                        GroupBuyStatus.BUY
                );

        System.out.println("구매 성공 수 = " + buyCount);
        System.out.println("남은 재고 = " + result.getLimitStock());

        assertThat(buyCount).isEqualTo(10);
        assertThat(result.getLimitStock()).isEqualTo(0);
    }
}
