package com.PetSitter.service.Reservation.CustomerReservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.dto.CareAvailableDate.request.UpdateCareAvailableDateRequest;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.request.AddCustomerReservationRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Reservation.CustomerReservation.CustomerReservationRepository;
import com.PetSitter.service.Admin.Reservation.AdminReservationService;
import com.PetSitter.service.CareAvailableDate.CareAvailableDateService;
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

import java.time.LocalDate;
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

    @Autowired
    private AdminReservationService adminReservationService;

    @Autowired
    private CareAvailableDateService careAvailableDateService;

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

        int threadCount = 2000;
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

        es.shutdown();

        // then
//        assertThat(successCount).isEqualTo(1);  // 성공한 예약이 1개여야 정상.
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1999);

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findById(careAvailableId)
                .orElseThrow(() -> new IllegalArgumentException("날짜를 찾을 수 없습니다."));

        assertThat(careAvailableDate.getStatus()).isEqualTo(CareAvailableDateStatus.IMPOSSIBILITY);
    }

    @DisplayName("예약 취소_동시성")
    @Test
    void 예약취소_동시성() throws InterruptedException {
        // given
        int threadCount = 1000;
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

        es.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(999);

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

        // 돌봄사 정보 수정 작업
        Future<?> updateFuture = es.submit(() -> {
            try {
                memberService.update(sitterId, updateMemberRequest, null);
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
        } finally {
            es.shutdown();
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

    @DisplayName("고객_돌봄사_동시예약취소")
    @Test
    void 고객_돌봄사_동시예약취소() throws ExecutionException, InterruptedException {
        // given
        long customerId = 1;
        long sitterId = 3;
        long customerReservationId = 1;
        long sitterScheduleId = 1;

        ExecutorService es = Executors.newFixedThreadPool(2);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        // 고객이 취소
        Future<?> customerResult = es.submit(() -> {
            try {
                customerReservationService.delete(customerId, customerReservationId);
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("고객 예약 취소 오류: " + e.getMessage());
                failCount.incrementAndGet();
            }
        });
//        customerResult.get();

        // 돌봄사가 취소
        Future<?> sitterResult = es.submit(() -> {
            try {
                sitterScheduleService.delete(sitterId, sitterScheduleId);
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("돌봄사 예약 취소 오류: " + e.getMessage());
                failCount.incrementAndGet();
            }
        });

        try {
            customerResult.get();
            sitterResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }

    @DisplayName("회원_관리자_동시예약취소")
    @Test
    void 회원_관리자_동시예약취소() throws ExecutionException, InterruptedException {
        // given
        long customerId = 1;
        long sitterId = 3;
        long customerReservationId = 1;
        long sitterScheduleId = 1;

        Member admin = memberRepository.findByLoginId("super").get();

        ExecutorService es = Executors.newFixedThreadPool(3);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        // 고객이 취소
        Future<?> customerResult = es.submit(() -> {
            try {
                customerReservationService.delete(customerId, customerReservationId);
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("고객 예약 취소 실패: " + e.getMessage());
                failCount.incrementAndGet();
            }
        });

        // 관리자가 취소
        Future<?> adminResult = es.submit(() -> {
            try {
                adminReservationService.deleteForAdmin(customerReservationId, admin);
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("관리자 예약 취소 실패: " + e.getMessage());
                failCount.incrementAndGet();
            }
        });

        // 돌봄사가 취소
        Future<?> sitterResult = es.submit(() -> {
            try {
                sitterScheduleService.delete(sitterId, sitterScheduleId);
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println("돌봄사 예약 취소 실패: " + e.getMessage());
                failCount.incrementAndGet();
            }
        });

        try {
            customerResult.get();
            sitterResult.get();
            adminResult.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            es.shutdown();
        }

        // then
        assertThat(successCount.get()).isEqualTo(1);    // 돌봄사가 취소하는 로직이 DB 쓰는 비용이 덜 하는 듯. 거의 보통 돌봄사가 먼저 락 얻고 처리하게 됨.
        assertThat(failCount.get()).isEqualTo(2);
    }

    @DisplayName("돌봄날짜정보수정_예약발생")
    @Test
    void 돌봄날짜정보수정_예약발생() throws InterruptedException {
        // given
        long customerId = 1;
        long sitterId = 3;
        long careAvailableId = 1;

        AddCustomerReservationRequest reservationRequest = new AddCustomerReservationRequest(
                customerId,
                sitterId,
                careAvailableId,
                List.of(1L, 2L),
                "돌봄날짜수정",
                0
        );

        UpdateCareAvailableDateRequest dateRequest = new UpdateCareAvailableDateRequest(
                LocalDate.parse("2025-11-15"),
                50000,
                CareAvailableDateStatus.POSSIBILITY);


        int threadCount = 200;
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        // 돌봄 날짜 정보 수정
        es.submit(() -> {
            try {
                careAvailableDateService.update(sitterId, careAvailableId, dateRequest);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
                System.out.println("돌봄사 돌봄 날짜 정보 수정 실패: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        // 예약 발생
        IntStream.range(0, threadCount - 1).forEach(i -> {
            es.submit(() -> {
                try {
                    customerReservationService.save(reservationRequest);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("고객 예약 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();
        es.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(199);
    }

    @DisplayName("돌봄날짜삭제_예약발생")
    void 돌봄날짜삭제_예약발생() {
        // given


        // when


        // then
    }
}