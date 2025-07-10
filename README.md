# [🐶 반려견 돌봄 서비스](http://www.petscarebook.com/pets-care/main)

반려견 돌봄을 위한 펫시터 서비스입니다. 고객은 원하는 펫시터를 선택해서 예약하고, 돌봄 기록을 확인하며, 리뷰를 남길 수 있습니다.  
펫시터는 자신의 자격을 인증하고, 예약을 관리하며, 돌봄 기록을 작성할 수 있습니다.
실제로 집에서 현재 반려견을 키우고 있기도 하고, 프로젝트 주제를 선정을 하는 과정에서 저의 실제 일상생활 상황에 대입해서 개발을 하면 재미와 흥미를 갖고 할 수 있을 것 같아 반려견 돌봄 서비스로 주제를 선정하게 되었습니다.  
이 서비스는 반려견의 케어 기록 관리, 돌봄 일정 예약 기능 등을 제공하며, 사용자와 돌봄사와 상호작용할 수 있는 플랫폼을 목표로 하고 있습니다. 

테스트 계정  
고객 -> id: user1 / pw: aaw131aaw131  
펫시터 -> id: gktjd12 / pw: aaw131aaw131  

--- 

✨ 주요 기능  

<br>

👥 회원 & 인증

<ol>회원 관리: 일반 회원과 펫시터 등록</ol>

<ol>시큐리티 로그인: 자체 회원가입과 로그인 Spring Security 기능 제공</ol>

<ol>소셜 로그인: 구글, 네이버, 카카오 지원(OAuth 2.0)</ol>

<ol>JWT 기반 인증: Access & Refresh Token 활용</ol>  

<ol>고객과 돌봄사간의 실시간 채팅 기능(WebSocket + STOMP)(+개발 완료)</ol>  

<ol>회원 신고 기능: 부적절한 회원 신고 기능(+개발 예정)</ol>    

<br>

🐾 반려견 관리

<ol>반려견 프로필 등록: 이름, 나이, 품종, 건강 상태 및 특이사항 등의 정보 저장</ol>

<ol>반려견 정보 수정 및 삭제</ol>  

<br>

🏅 펫시터 관리

<ol>자격증 등록: 펫시터의 돌봄 자격 인증</ol>

<ol>예약 가능 일정 등록: 고객이 예약할 수 있는 날짜 설정</ol>  

<br>

📅 돌봄 예약 시스템

<ol>고객 예약: 원하는 펫시터의 예약 가능 날짜 선택 후 예약 신청(멀티스레드 환경을 고려한 동시성(비관적 락) 처리)</ol>

<ol>펫시터 일정 관리: 예약 승인 및 취소 기능 제공</ol>  

<br>

📖 돌봄 케어 기록(로그)

<ol>돌봄 기록 작성: 펫시터가 돌봄 진행 상황을 기록</ol>

<ol>고객 확인: 작성된 돌봄 기록 확인 가능</ol>  

<br>

⭐ 리뷰 & 평점

<ol>리뷰 작성: 고객이 펫시터에 대한 리뷰 작성 가능</ol>

<ol>평점 부여: 0.5점 단위의 별점 시스템 제공</ol>  

<br>

🔧 관리자 페이지

<ol>회원 관리: 등록, 탈퇴 및 신고 회원 관리</ol>

<ol>돌봄 예약 삭제: 관리자가 돌봄 예약 정보를 삭제 가능</ol>

<ol>돌봄 리뷰 삭제: 부적절한 리뷰 삭제 기능</ol>

<ol>돌봄 기록 삭제: 관리자가 돌봄 기록을 삭제 가능</ol>

<ol>포인트 사용 내역 관리: 사용자의 포인트 적립 및 사용 내역 확인 및 관리</ol>  

<br>

+ Redis(Lettuce) 기반 웹 서비스 이용 가이드 챗봇(+개발 완료 -> 배포 완료)  
+ 전국 동물 병원 정보 조회 및 캐싱(네이버 맵에 병원 정보 표시, +개발 완료 -> 배포 완료)  

---

🛠 기술 스택  

<br>

🔹 BE

Spring Boot / JAVA

JPA & Spring Data JPA - ORM 및 데이터 관리

QueryDSL & JPQL - 동적 쿼리 처리

Spring Security  

Redis(Lettuce) + 중복 조회 데이터 Cache Aside(Look Aside) 읽기 전략으로 캐싱

WebSocket + STOMP

<br>

🔹 FE

Thymeleaf - 서버 사이드 렌더링(SSR)

HTML, CSS, JavaScript

Bootstrap

<br>

🔹 개발 환경

IntelliJ IDEA

MySQL  

<br>

🔹 배포 환경  

AWS EC2 프리티어

MySQL  

---

# ERD 상세 설명  

<br>

<img width="1154" alt="erd-temp" src="https://github.com/user-attachments/assets/65023b22-85d3-424c-8580-490c3af75ff6" />      

---

# [📌 API 명세](http://petscarebook.com/swagger-ui/index.html)

API 명세서는 Swagger 문서에서 확인할 수 있습니다.  

---

# 메인 페이지(1)
<img width="1547" alt="image" src="https://github.com/user-attachments/assets/390a008d-3522-4f23-82fd-97292b0fecb9" />  
<br><br><br>

# 메인 페이지(2)
<img width="1398" alt="image" src="https://github.com/user-attachments/assets/275dd0ac-ab2f-454a-a4a1-d2457ed8ff53" />  
(돌봄 리뷰는 가장 최근에 작성된 리뷰 6개만 조회(15분 마다 캐싱))  
<br><br>

# 메인 페이지(3)
<img width="1328" alt="image" src="https://github.com/user-attachments/assets/2a5f640e-2916-4eaa-bb31-bca2993cfab9" />    
(각 조건 별 검색 결과와 카운트 쿼리 모두 Cache Aside 읽기 패턴으로 12시간 마다 캐싱)  

-> 캐싱 하기 전과 후를 라이트하우스 지수로 비교하면,  
  FCP (First Contentful Paint): 최초 DOM 콘텐츠가 렌더링되기까지의 시간  
  SI (Speed Index): 콘텐츠가 시각적으로 표시되는 속도  
  LCP (Largest Contentful Paint): 페이지 내 가장 큰 콘텐츠 요소가 렌더링되기까지의 시간  

| 지표                                 | 캐싱 전          | 캐싱 후         | 개선율          |
| ---------------------------------- | ------------- | ------------ | ------------ |
| **FCP** (First Contentful Paint)   | 695 \~ 1677ms | 576 \~ 745ms | **44.3% 개선** |
| **SI** (Speed Index)               | 695 \~ 1677ms | 576 \~ 745ms | **44.3% 개선** |
| **LCP** (Largest Contentful Paint) | 696 \~ 1677ms | 576 \~ 932ms | **36.5% 개선** |

   
<br><br>

# WebSocket + STOMP 실시간 통신  
![image](https://github.com/user-attachments/assets/392be397-13df-4a7b-b8dc-ca9bc21b547c)  
<br>  
![image](https://github.com/user-attachments/assets/6b794eb8-4361-4269-b191-27fb6dfb2b25)      
(접속 중이지 않은 다른 채팅방에게서 온 메시지는 알림 형식으로 표시)<br>  
![image](https://github.com/user-attachments/assets/b8bb4d5c-7247-42cb-aca4-72316f8ac23d)      
<br>  
![image](https://github.com/user-attachments/assets/c2719647-50af-4593-9285-3e3e328b4aaa)    
사용자가 아직 읽지 않은 메시지는 별도의 알림 탭에서 확인 가능  

<br><br><br>

# 회원 정보 페이지 - 고객 시점(1)
<img width="943" alt="image" src="https://github.com/user-attachments/assets/5e99d5c0-3e55-45b2-8a74-441a6fd542c5" />          
<img width="614" alt="image" src="https://github.com/user-attachments/assets/33c83a95-da36-458e-8e92-79e6e14e855b" />        
<img width="616" alt="image" src="https://github.com/user-attachments/assets/253c7478-e640-4e4c-a38c-6fb2bddd6f7a" />  

  
(한 번에 여러 마리의 반려견 정보 수정, 등록 가능)<br><br>

<img width="1358" alt="image" src="https://github.com/user-attachments/assets/2ad852fb-17bc-4ee0-bc54-e2941d26c9b4" />    
(자신이 등록한 모든 돌봄 리뷰 목록 조회 페이지, 클릭 시 리뷰 작성한 해당 예약 페이지로 이동)<br><br><br>

# 회원 정보 페이지 - 돌봄사 시점(2)
<img width="616" alt="image" src="https://github.com/user-attachments/assets/65ce6d74-3c92-4dd4-b37d-9eafd5dd0e35" />      
<img width="1530" alt="image" src="https://github.com/user-attachments/assets/2d5f75dd-94e0-4613-84a5-2054747ec966" />      
(자신에게 등록된 모든 돌봄 리뷰 목록 조회 페이지(돌봄사도 한 번에 여러 개의 자격증 정보 수정, 등록 가능), 클릭 시 해당 예약 페이지로 이동)<br><br>
<img width="1587" alt="image" src="https://github.com/user-attachments/assets/35310d28-32fb-4f79-97c5-a0bee0d5d2af" />      
(자신이 작성한 모든 돌봄 케어 로그 목록 조회 페이지(클릭 시 케어 로그 작성한 해당 예약 페이지로 이동)<br><br><br>

# 돌봄 예약 가능한 돌봄사 목록  
<img width="653" alt="image" src="https://github.com/user-attachments/assets/698c3c4f-9f32-4d18-a442-5800cff38a14" />    


(현재 예약이 가능한 펫시터만 조회)<br><br><br>

# 돌봄 예약 가능한 돌봄사 목록 중 돌봄사를 택했을 때  
<img width="620" alt="image" src="https://github.com/user-attachments/assets/accd5597-5ee4-4dbc-b48b-dbd7083b9b21" />        
<img width="696" alt="image" src="https://github.com/user-attachments/assets/1e688ea0-7f23-4d8e-abae-69cd61d6129d" />     


(선택한 돌봄사의 정보와 스펙들을 보여주고, 해당 돌봄사에게 작성된 모든 돌봄 리뷰를 조회(+리뷰는 최상위 5개  
조회 후, 이후 비동기로 5개씩 페이징))<br><br><br>

# 고객 시점: 돌봄 예약 페이지(1)
<img width="641" alt="image" src="https://github.com/user-attachments/assets/503b52ac-2a60-4038-b2fc-adf5859b9547" />    


동시성 처리: 공유 자원인 돌봄사의 돌봄 가능 날짜에 대해 비관적 락(X-Lock)을 이용해서 동시성 처리를 해결해서 데이터 정합성을 지킬 수 있게 됨.    
<br>
(해당 돌봄사가 등록한 돌봄 예약 가능한 날짜를 예약 가능한 날짜에 반환, 맡길 반려견을 선택)  
<br>
(선택한 날짜의 돌봄 가격과 회원이 보유중인 적립금을 조회하여 적립금 사용 입력란에 숫자를 입력하면 비동기로 최종 결제 금액이 자동으로 산정)  
<br>
<br><br><br>

# 고객 시점: 돌봄 예약 내역 조회 페이지(돌봄사도 동일)
<img width="1465" alt="image" src="https://github.com/user-attachments/assets/1a855999-904f-4fdb-8486-c749542d215a" />      
(돌봄 예약 내역 전체 조회, 클릭 시 해당 예약 페이지로 이동)<br><br><br>

# 고객 시점: 특정 돌봄 예약 상세 조회 페이지(1)(돌봄사도 동일)
<img width="643" alt="image" src="https://github.com/user-attachments/assets/ef8bafe7-9765-4e44-8a4f-673aee4f48ac" />                    
<br><br><br>

# 고객 시점: 특정 돌봄 예약 상세 조회 페이지(2)(돌봄사도 동일)
<img width="991" alt="image" src="https://github.com/user-attachments/assets/18fb6a55-57a5-4ae2-a070-9743901f6827" />  
(별점 리뷰는 0.5 단위로도 가능(취소된 예약에는 리뷰 작성 불가능))<br><br><br>

# 펫시터 시점: 돌봄 예약 가능한 날짜 목록 페이지
<img width="1465" alt="image" src="https://github.com/user-attachments/assets/49bb29f6-5e8b-4e1e-ba69-85dd6cae7026" />    
(돌봄사가 등록한 돌봄 예약 가능 날짜만 예약이 가능)<br><br><br>

# 펫시터 시점: 등록한 돌봄 예약 가능한 날짜 수정
<img width="1103" alt="image" src="https://github.com/user-attachments/assets/76160059-a562-4a0f-be71-9011439052c3" />    
<img width="1108" alt="image" src="https://github.com/user-attachments/assets/847fd6f9-d717-41b9-be2a-fe9ce3462177" />    
<br><br><br>

# 펫시터 시점: 특정 돌봄 예약 상세 조회 페이지 - 돌봄 케어 로그 작성 부분
<img width="1053" alt="image" src="https://github.com/user-attachments/assets/c75e5323-3b04-43cf-a779-c63a454a9e7d" />  
(취소된 예약에는 돌봄 케어 로그 작성 불가능)<br><br><br>

# 돌봄 이용 후기 페이지
<img width="1468" alt="image" src="https://github.com/user-attachments/assets/87435349-d7a0-4a0d-81b2-b21970a8ca23" />    
<img width="1463" alt="image" src="https://github.com/user-attachments/assets/015fc92f-4da6-43ad-88d5-9c5eab1c14dc" />    
(돌봄 이용 후기 페이지에서 돌봄 리뷰가 존재하는 돌봄사만 검색 리스트에 반환해서 원하는 돌봄사의 리뷰만 조건 검색 가능(+15분 단위로 캐싱))<br><br><br>

# 관리자 페이지 - 회원 관리
<img width="1440" alt="image" src="https://github.com/user-attachments/assets/215e19aa-81d7-4210-8277-1ffce98fb2f5" />    
<img width="632" alt="image" src="https://github.com/user-attachments/assets/e2e2d5d6-1d1b-4214-8c68-d310c82a4e40" />            
<img width="602" alt="image" src="https://github.com/user-attachments/assets/2954e19d-4c40-4ad2-bcb9-f10dd76d0768" />          
<img width="1080" alt="image" src="https://github.com/user-attachments/assets/a7883a81-9e61-462f-88e5-9c3999ae7d3d" />  
(회원의 이름을 조건으로 검색해서 원하는 회원만 검색 가능, 관리자 권한으로 부적절한 회원 탈퇴 처리 가능)<br><br><br>

# 관리자 페이지 - 돌봄 예약 관리(+돌봄 케어 로그, 리뷰 관리)
<img width="1518" alt="image" src="https://github.com/user-attachments/assets/1f13f5a0-8c25-423b-ab8b-c1685ff6db31" />      
<img width="615" alt="image" src="https://github.com/user-attachments/assets/ab219688-0cf7-4bf2-9c60-aa320fe8d0c4" />            
<img width="586" alt="image" src="https://github.com/user-attachments/assets/0ceb6fcb-bdcf-4616-a33b-d288b5967314" />          

(고객의 이름을 조건으로 검색해서 원하는 돌봄 예약만 검색 가능, 관리자 권한으로 돌봄 예약 및 부적절한 돌봄 케어 로그 및 부적절한 리뷰 삭제 가능)<br><br><br>

# 관리자 페이지 - 회원 포인트 내역 관리(적립, 사용)
<img width="946" alt="image" src="https://github.com/user-attachments/assets/4a4e54a4-989f-4429-b7ec-a8b0fc610982" />     
<img width="977" alt="image" src="https://github.com/user-attachments/assets/e2025867-666a-4dac-ab03-090b0dec3e30" />    


(고객의 이름을 조건으로 검색해서 원하는 회원만 내역 검색 가능)<br><br><br>

--- 
💭 프로젝트를 진행하며 느낀 점  

<br>

1) 조회 성능 개선: 이번 프로젝트를 통해 많은 것을 배우는 경험을 할 수 있었습니다. 처음에는 데이터베이스 조회 성능 최적화를 고려하지 않고 구현을 진행했지만, 불필요한 데이터와 엔티티까지 쿼리문이 발생하는 것을 보고 실제 운영 환경에서는 대규모 데이터 조회 시 성능 문제가 발생할 수 있음을 깨닫게 되었습니다. 이를 해결하기 위해 쿼리 최적화, N+1 문제 해결 등을 적용하며 성능을 개선한 경험을 쌓을 수 있었습니다.  
  중복 조회 데이터 캐싱(Cache Aside 읽기 전략), 트랜잭션 readOnly 및 전파 속성 수정 후 라이트하우스 테스트를 직접 해보면서 유의미한 성능 개선 결과를 확인.(+jmeter로 추가 부하 테스트도 예정)                  <br>  <br>
이 과정에서 도메인 주도 설계(DDD)를 배우고 적용하며 유지보수성을 고려한 데이터 모델링을 해보고, 단순한 테이블 설계를 넘어서서 비즈니스 로직과 연계된 도메인 중심 모델링을 고민하며 성능적인 부분과 추후의 확장 가능성과 유지보수성을 고려해서 정규화와 비정규화의 균형을 맞춘 설계를 해보게 됐습니다.    
추가로, 도메인의 핵심 비즈니스 로직의 일부가 되는 검증 로직을 별도의 도메인 서비스(상호작용 관련 규칙 검증 서비스)에서 핵심 비즈니스 규칙을 캡슐화하고 검증하면 애플리케이션 서비스는 사용자의 특정 유스케이스만을 집중할 수 있게 되는 새로운 설계도 배워 적용을 고려하고 있습니다.


4) 트랜잭션 및 동시성 처리: 공유 자원인 돌봄사의 돌봄 가능 날짜에 대해 여러 고객이 예약 요청을 발생시킬 때 레이스 컨디션이 발생하여 데이터가 불일치 하게된 경우가 있었습니다.  멀티스레드 환경에서 공유 자원에 대해 동시성 처리를 하여 데이터 정합성을 지키기 위해 어떻게 트랜잭션의 생명주기를 관리해 줘야 하는가에 대한 고민도 하고 자바의 스레드, 낙관적 락과 비관적 락에 대해 배워 실제 테스트 코드로 ExecutorService를 이용해 스레드 풀에 스레드를 2000개 정도 설정한 후, 각 스레드가 동시성 문제가 생기는 비즈니스 로직(임계 영역)을 수행하게 해서 데이터 정합성이 보장되는지 직접 테스트하고 비관적 락(X-Lock)적용하여 동시성 문제를 해결하는 경험을 했습니다.  
이 프로젝트에서는 synchronized + ConcurrentHashMap와 낙관적 락을 사용하지 않은 이유는, 일단 첫번째 synchronized + ConcurrentHashMap로도 현재 분산 서버 환경이 아니기 때문에 충분히 해결할 수 있지만 분산 서버 환경이라 가정하고 해결을 하고 싶어서이고, 두번째 낙관적 락을 사용하지 않은 이유는 낙관적 락은 다른 트랜잭션에서 읽기는 허용하고 쓰기를 막기 때문에 성능이 비관적 락보다 좋을 수 있지만 사용자가 많이 몰리는 상황이라 가정하고 했을 때, 예약은 중요한 비즈니스 로직이기 때문에 데이터 정합성을 확실히 지키고, 낙관적 락을 사용했을 경우 트랜잭션을 개발자가 관리할 수 없다는 단점이 있어서 일일이 롤백 해줘야 하는 비용도 무시할 수 없기에 비관적 락을 택했습니다.   

5) Redis(Lettuce 기반)를 이용하여 비로그인(게스트), 로그인(회원) 신분의 유저가 챗봇을 이용하여 대화를 진행할 경우, 신분에 따라 대화 기록 저장 시간(TTL)을 유동적으로 설정도 해 보았고, 로그인 시 비로그인 신분으로 이전에 진행한 대화 기록이 있다면 이전 대화 기록을 마이그레이션도 해 보았습니다.(중요하지 않은 대화 기록이기에 휘발시킬 수 있는 속성을 가진 Redis를 채택.)  
Redis 서버 인메모리에 저장 시 ObjectMapper를 이용하여 DTO 객체를 JSON 데이터 형식으로 직렬화하여 저장하고, 반대로 대화 기록을 불러올 때에는 JSON 데이터를 자바 객체로 역직렬화하여 조회하였고 Redis를 이용해서 DB 한 곳으로의 트래픽을 분산시켜 부하를 줄일 수 있다는 것이 어떤 구조로 그럴 수 있는건 지 직접 알 수 있던 경험이었습니다. 
그리고 이 개발 경험으로 Redis에 캐시 읽기 전략에 일반적으로 사용하는 Look Aside(Cache Aside) 패턴, Read Through 패턴 등과 같은 여러 방법이 있다는 것도 알게 되어 상황에 맞는 전략을 이용해야 효율적이라는 것을 알게 됐습니다.  


6) 실시간 웹소켓 채팅 기능 구현: 고객과 돌봄사 간의 1:1 관계의 웹소켓 + STOMP(pub/sub) 기반 실시간 채팅을 구현하여 고객이 예약하기 전 돌봄사와 대화하여 궁금한 점을 묻거나, 자유로운 대화를 할 수 있도록 사용자 편의성을 위해 개발을 해봤습니다. mysql 네이티브 쿼리의 윈도우 함수를 사용해서 진행 중인 대화방 목록을 조회할 때 카카오톡과 같이 상대방의 정보와 마지막으로 진행된 메시지와 내가 아직 읽지 않은 메시지의 개수, 날짜 및 시간이 보이도록 적용하고, 현재 접속 중이지 않은 다른 채팅방에서 메시지를 수신할 경우 알림을 표시해주고 별도의 알림탭을 만들어 읽지 않은 메시지만 모아볼 수 있게 개발을 해봤습니다.  
해당 기능의 개발을 해보면서 JPA와 QueryDSL으로는 아직 한계가 있다는 것을 느끼고 상황에 따라 복잡한 쿼리의 경우 네이티브 쿼리는 선택이 아닌 필수라고 느끼게 되었습니다.  
WebSocket + STOMP 기반 실시간 웹 소켓 채팅을 구현해 보면서 pub/sub의 구조를 새로 배우게 될 수 있었고, 웹소켓의 경우 HTTP 통신할 때와 다른 인증 세션을 사용한다는 것을 알게 되어 Authentication을 별도로 웹소켓 인증 서버에 명시적으로 설정해 줘야 한다는 것도 알게 되었습니다.  


7) 인증 및 보안: 토큰을 구현하면서 액세스 토큰과 리프레시 토큰을 로컬 스토리지, 세션 스토리지, 브라우저(쿠키) 중 어느 곳에 저장하느냐에 따라 장단점과 특징이 있다는 걸 이해하게 되었고, JWT 토큰 관리 부분에 있어서는 사용자 입장에서 토큰의 짧은 생명 주기 때문에 로그인 상태가 풀리는 것이 불편할 것 같다 생각이 들어, 사용자의 동작을 감지해서 리프레시 토큰이 유효할 경우 새로운 액세스 토큰을 발급해서 교체하여 쾌적한 이용을 할 수 있게 했습니다.  
처음에는 액세스 토큰과 리프레시 토큰을 SSR 방식에서 개발했다 보니 로컬 스토리지와 세션 스토리지를 저장하는 경우가 아닌 브라우저(쿠키)에 저장하는 경우라면 서버에서 저장해야 하는 줄 알았지만, CSR 방식에서는 서버에서 화면을 렌더링 하는 것이 아니라, 서버가 응답한 데이터를 가지고 화면을 그리기 때문에 클라이언트에서 토큰을 받아 저장하는 방법을 사용한다는 것을 알게 되었습니다.   
추가로 액세스 토큰과 리프레시 토큰을 로컬 스토리지, 세션 스토리지, 브라우저(쿠키) 중 어느 곳에 저장하느냐에 따라 보안과 관련해서 장단점과 특징이 있다는 걸 정확히 이해하게 되었고,  
페이징 시에 서버에서 정해진 개수의 데이터를 클라이언트에게 반환하는 방식만이 페이징의 전부라고 생각했지만, 프론트엔드에서 디바이스 크기에 따라 페이징 크기를 조정하는 방식도 있다는 걸 배우게 됐습니다.  
그리고 민감한 보안 문제에 있어서도 스프링 시큐리티의 내부에서 동작하는 구조들에 대해 확실히 파악을 할 수 있던 경험도 하게 되었습니다.


1인 개발 프로젝트이지만 협업하는 상황을 스스로 가정하며 개발을 진행했고, 프론트엔드가 유연하게 데이터를 활용할 수 있도록 API 응답 방식을 고민하는 과정에서 프론트 개발자가 받기 좋은 커스텀 응답 객체의 중요성을 깊이 깨닫게 되었습니다.  

