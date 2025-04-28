package com.PetSitter.service.Reservation.CustomerReservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.request.AddCustomerReservationRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Reservation.CustomerReservation.CustomerReservationRepository;
import com.PetSitter.service.Member.MemberService;
import com.PetSitter.service.Reservation.SitterSchedule.SitterScheduleService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@ActiveProfiles("test")
@Transactional
@Rollback
@SpringBootTest
class CustomerReservationServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CustomerReservationService customerReservationService;

    @Autowired
    private CareAvailableDateRepository careAvailableDateRepository;

    @Autowired
    private SitterScheduleService sitterScheduleService;

    @DisplayName("예약 발생_동시성")
    @Test
    void 예약_동시처리() throws InterruptedException {
        // given
        long customerId = 1;
        long sitterId = 3;
        long careAvailableId = 15;

        AddCustomerReservationRequest request = new AddCustomerReservationRequest(
                customerId,
                sitterId,
                careAvailableId,
                List.of(1L, 2L),
                "1번",
                0
        );

        int threadCount = 20;
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

//        List<Future<Boolean>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        // Future 활용 ver
//        IntStream.range(0, threadCount).forEach(i -> {
//            futures.add(es.submit(() -> {
//                try {
//                    customerReservationService.save(request);
//                    return true;    // 예약 성공
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false; // 예약 실패
//                } finally {
//                    latch.countDown();
//                }
//            }));
//        });

        // AtomicInteger 활용 ver
        IntStream.range(0, threadCount).forEach(i -> {
            es.submit(() -> {
                try {
                    customerReservationService.save(request);
                    successCount.incrementAndGet();    // 예약 성공
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.incrementAndGet(); // 예약 실패
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();  // 모든 스레드가 작업을 완료하기 전까지 main 스레드 대기.

//        long successCount = futures.stream()
//                .filter(future -> {
//                    try {
//                        return future.get();
//                    } catch (ExecutionException | InterruptedException e) {
//                        return false;
//                    }
//                })
//                .count();

        // then
//        assertThat(successCount).isEqualTo(1);  // 성공한 예약이 1개여야 정상.
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(19);

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findById(careAvailableId)
                .orElseThrow(() -> new IllegalArgumentException("날짜를 찾을 수 없습니다."));

        assertThat(careAvailableDate.getStatus()).isEqualTo(CareAvailableDateStatus.IMPOSSIBILITY);
    }

    @DisplayName("예약 취소_동시성")
    @Test
    void 예약취소_동시성() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

//        List<Future<Boolean>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        // future 활용 ver
//        IntStream.range(0, threadCount).forEach(i -> {
//            futures.add(es.submit(() -> {
//                try {
//                    customerReservationService.delete(1, 1);
////                    sitterScheduleService.delete(3, 1);
//                    return true;
//                } catch (Exception e) {
//                    System.out.println("e.getMessage() = " + e.getMessage());
//                    return false; // 예약 실패
//                } finally {
//                    latch.countDown();
//                }
//            }));
//        });

        // AtomicInteger 활용 ver
        IntStream.range(0, threadCount).forEach(i -> {
            es.submit(() -> {
                try {
                    customerReservationService.delete(1, 1);
//                    sitterScheduleService.delete(3, 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();

//        long successCount = futures.stream()
//                .filter(future -> {
//                    try {
//                        return future.get();
//                    } catch (ExecutionException | InterruptedException e) {
//                        return false;
//                    }
//                })
//                .count();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(99);

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findById(16L)
                .orElseThrow(() -> new IllegalArgumentException("날짜를 찾을 수 없습니다."));

        assertThat(careAvailableDate.getStatus()).isEqualTo(CareAvailableDateStatus.POSSIBILITY);
    }

    @DisplayName("회원정보변경_예약 동시성 발생")
    @Test
    void 회원정보변경_예약_동시성() throws InterruptedException {
        // given
        long customerId = 1;
        long sitterId = 3;
        long careAvailableId = 10;

        // 예약 발생
        AddCustomerReservationRequest request = new AddCustomerReservationRequest(
                customerId,
                sitterId,
                careAvailableId,
                List.of(1L, 2L),
                "회원정보 변경 테스트",
                0
        );

        // 돌봄사 회원 정보 수정
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest("awa131", "테스트", "test1", "test@gmail.com", "010-1234-1234", "11111", "테스트 주소", "테스트 소개", 33, "PET_SITTER");

        int threadCount = 2;
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        // 예약 생성 작업
        Future<?> reservationFuture = es.submit(() -> {
            try {
                customerReservationService.save(request);
            } catch (Exception e) {
                System.out.println("예약 실패: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        Thread.sleep(100);

        // 돌봄사 정보 수정 작업
        Future<?> updateFuture = es.submit(() -> {
            try {
                memberService.update(sitterId, updateMemberRequest, null);  // 회원정보 수정 메서드에서는 sitter를 가장 먼저 락 걸고, 예약 생성 메서드에서는 customer 다음으로 sitter에 락 걸어서 보통 웬만하면 스레드들이 이 락부터 걸게 되는 듯.
            } catch (Exception e) {
                System.out.println("회원 정보 수정 실패: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        em.flush();
        em.clear();

        // 보통 @Test가 붙으면 큰 트랜잭션 안에서 작은 트랜잭션들이 세부적으로 존재하는데, 현재의 경우 각 메서드마다 개별 트랜잭션을 갖는 것이 아닌 @Test 트랜잭션 내부에 존재해서
        // 예약을 먼저 발생시키고 돌봄사 정보를 수정해도 1차 캐시에 업데이트된 돌봄사의 정보의 스냅샷을 가져오기 때문에 항상 최신화된 돌봄사의 정보가 가져와짐.
        CustomerReservationResponse.GetDetail reservation = customerReservationService.findById(customerId, 2);

        // 두 작업이 정상적으로 완료되었는지 확인
        try {
            updateFuture.get();
            reservationFuture.get();
        } catch (Exception e) {
            System.out.println("작업 중 예외 발생: " + e.getMessage());
        }

        // then
        Member updatedMember = memberRepository.findById(sitterId)
                .orElseThrow(() -> new IllegalArgumentException("돌봄사 정보가 없습니다."));

        assertThat(updatedMember.getName()).isEqualTo("테스트");
        assertThat(updatedMember.getZipcode()).isEqualTo("11111");
        assertThat(updatedMember.getAddress()).isEqualTo("테스트 주소");

        assertThat(reservation.getZipcode()).isEqualTo(updatedMember.getZipcode());
        assertThat(reservation.getAddress()).isEqualTo(updatedMember.getAddress());
    }
}