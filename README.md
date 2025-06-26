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

+Redis(Lettuce) 기반 웹 서비스 이용 가이드 챗봇(+개발 완료)  
+전국 동물 병원 정보 확인(네이버 맵에 병원 정보 표시, +개발 예정)  

---

🛠 기술 스택  

<br>

🔹 BE

Spring Boot / JAVA

JPA & Spring Data JPA - ORM 및 데이터 관리

QueryDSL & JPQL - 동적 쿼리 처리

Spring Security  

Redis(Lettuce)

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

<img width="1299" alt="erd-temp" src="https://github.com/user-attachments/assets/05d441ed-8bfe-486d-83e3-1bccf243ce15" />    

---

# [📌 API 명세](http://ec2-3-36-128-60.ap-northeast-2.compute.amazonaws.com:9090/swagger-ui/index.html)

API 명세서는 Swagger 문서에서 확인할 수 있습니다.  

---

# 메인 페이지(1)
<img width="1609" alt="image" src="https://github.com/user-attachments/assets/aef60f5d-73ea-4391-982a-528db36784a0" />        
<br><br><br>

# 메인 페이지(2)
<img width="1630" alt="image" src="https://github.com/user-attachments/assets/e478fd2f-7ffb-42d6-b2a4-010fa50480ad" />      
(돌봄 리뷰는 가장 최근에 작성된 리뷰 6개만 조회(+페이징))
<br><br>

# WebSocket + STOMP 실시간 통신  
<img width="1704" alt="image" src="https://github.com/user-attachments/assets/6bdf162e-5a3a-4f46-9f7a-13f274370ddd" /><br>  
<img width="1686" alt="image" src="https://github.com/user-attachments/assets/380b16ae-d7ee-4eda-a7a9-0f1dac6b0fcd" />    
(접속 중이지 않은 다른 채팅방에게서 온 메시지는 알림 형식으로 표시)<br>  
<img width="1223" alt="image" src="https://github.com/user-attachments/assets/aa36eb4f-53af-4bfd-9512-fd02bedf6c4f" />    
<br>  
<img width="1243" alt="image" src="https://github.com/user-attachments/assets/7369ceed-2059-442d-87a3-3af5c3aced1e" />  
사용자가 아직 읽지 않은 메시지는 별도의 알림 탭에서 확인 가능  

<br><br><br>

# 회원 정보 페이지 - 고객 시점(1)
<img width="606" alt="image" src="https://github.com/user-attachments/assets/70ff23ea-c493-42a9-841f-f192d6b18d8b" />        
<img width="603" alt="image" src="https://github.com/user-attachments/assets/6fd4b718-bfa7-48c8-ad61-f923385e8a56" />    
<img width="626" alt="image" src="https://github.com/user-attachments/assets/3e1b2dc2-728d-47ae-8157-cb76a1afc5cd" /> 

(한 번에 여러 마리의 반려견 정보 수정, 등록 가능)<br><br>

<img width="1562" alt="image" src="https://github.com/user-attachments/assets/c2996738-a52d-432a-9193-1b74394b3109" />    
(자신이 등록한 모든 돌봄 리뷰 목록 조회 페이지(+페이징), 클릭 시 리뷰 작성한 해당 예약 페이지로 이동)<br><br><br>

# 회원 정보 페이지 - 돌봄사 시점(2)
<img width="647" alt="image" src="https://github.com/user-attachments/assets/166fbe4c-c437-4365-879b-64ceb14c9d6a" />    
<img width="1507" alt="image" src="https://github.com/user-attachments/assets/12ca5767-c45f-44c5-a926-6238f6c171e3" />      
(자신에게 등록된 모든 돌봄 리뷰 목록 조회 페이지((+페이징), 돌봄사도 한 번에 여러 개의 자격증 정보 수정, 등록 가능), 클릭 시 해당 예약 페이지로 이동)<br><br>
<img width="1459" alt="image" src="https://github.com/user-attachments/assets/49b2cb76-0a3e-4f72-82be-58a7e651506f" />    
(자신이 등록한 모든 돌봄 케어 로그 목록 조회 페이지(+페이징), 클릭 시 케어 로그 작성한 해당 예약 페이지로 이동)<br><br><br>

# 돌봄 예약 가능한 돌봄사 목록  
<img width="680" alt="image" src="https://github.com/user-attachments/assets/f82cea2d-46cd-4e97-84f8-32149dc77a6c" />        

(돌봄 예약 가능 날짜를 등록한 펫시터 중 현재 예약이 가능한 펫시터만 조회(+페이징))<br><br><br>

# 돌봄 예약 가능한 돌봄사 목록 중 돌봄사를 택했을 때  
<img width="609" alt="image" src="https://github.com/user-attachments/assets/48a3b3ac-a23e-4dc7-bf26-e5f697ebe21a" />        
<img width="722" alt="image" src="https://github.com/user-attachments/assets/15fb1850-8d5b-47be-99a6-9777e701754b" />          

(선택한 돌봄사의 정보와 스펙들을 보여주고, 해당 돌봄사에게 작성된 모든 돌봄 리뷰를 조회(+리뷰는 최상위 5개  
조회 후, 이후 비동기로 5개씩 페이징))<br><br><br>

# 고객 시점: 돌봄 예약 페이지(1)
<img width="1446" alt="image" src="https://github.com/user-attachments/assets/d19dc408-13a0-4227-a318-57ac7d63731c" />  
동시성 처리: 공유 자원인 돌봄사의 돌봄 가능 날짜에 대해 비관적 락(X-Lock)을 이용해서 동시성 처리를 해결해서 데이터 정합성을 지킬 수 있게 됨.    
<br>
(해당 돌봄사가 등록한 돌봄 예약 가능한 날짜를 예약 가능한 날짜에 반환, 맡길 반려견을 선택)  
<br>
(선택한 날짜의 돌봄 가격과 회원이 보유중인 적립금을 조회하여 적립금 사용 입력란에 숫자를 입력하면 비동기로 최종 결제 금액이 자동으로 산정)  
<br>
<br><br><br>

# 고객 시점: 돌봄 예약 내역 조회 페이지(돌봄사도 동일)
<img width="1424" alt="image" src="https://github.com/user-attachments/assets/5195c2f6-893b-41ed-8767-2a11cffbcc4e" />  
(돌봄 예약 내역 전체 조회(+페이징), 클릭 시 해당 예약 페이지로 이동)<br><br><br>

# 고객 시점: 특정 돌봄 예약 상세 조회 페이지(1)(돌봄사도 동일)
<img width="638" alt="image" src="https://github.com/user-attachments/assets/73951d2e-9418-4229-9816-b4934cd7817b" />                  
<br><br><br>

# 고객 시점: 특정 돌봄 예약 상세 조회 페이지(2)(돌봄사도 동일)
<img width="886" alt="image" src="https://github.com/user-attachments/assets/6713b04f-5d4e-43d1-8a8c-4758c1996e83" />    

(별점 리뷰는 0.5 단위로도 가능(취소된 예약에는 리뷰 작성 불가능))<br><br><br>

# 펫시터 시점: 돌봄 예약 가능한 날짜 목록 페이지
<img width="1456" alt="image" src="https://github.com/user-attachments/assets/f450e974-cbee-4054-8c07-e6e266d36c0b" />  
(돌봄사가 등록한 돌봄 예약 가능 날짜만 예약이 가능(+페이징))<br><br><br>

# 펫시터 시점: 등록한 돌봄 예약 가능한 날짜 수정
<img width="1050" alt="image" src="https://github.com/user-attachments/assets/d030d3f0-9b2a-46e4-8068-727dd42c56d3" />  
<img width="1048" alt="image" src="https://github.com/user-attachments/assets/9b369566-3195-41a5-8cda-b0c962f886e4" />  
<br><br><br>

# 펫시터 시점: 특정 돌봄 예약 상세 조회 페이지 - 돌봄 케어 로그 작성 부분
<img width="916" alt="image" src="https://github.com/user-attachments/assets/d3f2e4e7-84d5-4255-9a73-89096bd03e64" />   

(취소된 예약에는 돌봄 케어 로그 작성 불가능)<br><br><br>

# 돌봄 이용 후기 페이지
<img width="1489" alt="image" src="https://github.com/user-attachments/assets/7b348fe2-17f0-4c69-94f2-3660948fe418" />  
<img width="1536" alt="image" src="https://github.com/user-attachments/assets/160f8eab-e251-4f6f-b028-731ddd6f943e" />    
(돌봄 이용 후기 페이지에서 돌봄 리뷰가 존재하는 돌봄사만 검색 리스트에 반환해서 원하는 돌봄사의 리뷰만 조건 검색 가능(+페이징))<br><br><br>

# 관리자 페이지 - 회원 관리
<img width="1091" alt="image" src="https://github.com/user-attachments/assets/2d8ff36c-82b8-4c5b-9140-aa12f0204842" />  
<img width="614" alt="image" src="https://github.com/user-attachments/assets/a068bc82-b48e-4ad1-b9f3-8304da2f99b8" />          
<img width="631" alt="image" src="https://github.com/user-attachments/assets/e1fa8bb1-bf2f-4d60-947d-485963b12e73" />        
<img width="1325" alt="image" src="https://github.com/user-attachments/assets/f92c09b3-528e-4d5c-94a2-564f4006a4be" />    

(회원의 이름을 조건으로 검색해서 원하는 회원만 검색 가능(+페이징), 관리자 권한으로 부적절한 회원 탈퇴 처리 가능)<br><br><br>

# 관리자 페이지 - 돌봄 예약 관리(+돌봄 케어 로그, 리뷰 관리)
<img width="1037" alt="image" src="https://github.com/user-attachments/assets/10d3c641-387e-42af-ba8c-1f1d8b283d16" />  
<img width="611" alt="image" src="https://github.com/user-attachments/assets/dd3636b3-38e2-4a5a-8c90-bc783cdd3856" />          
<img width="636" alt="image" src="https://github.com/user-attachments/assets/2ba9d9f8-2e89-4b6b-8dba-a8b4a2926b3e" />        

(고객의 이름을 조건으로 검색해서 원하는 돌봄 예약만 검색 가능(+페이징), 관리자 권한으로 돌봄 예약 및 부적절한 돌봄 케어 로그 및 부적절한 리뷰 삭제 가능)<br><br><br>

# 관리자 페이지 - 회원 포인트 내역 관리(적립, 사용)
<img width="1086" alt="image" src="https://github.com/user-attachments/assets/eb11f503-41a8-4333-8e76-df9e7ab0034f" />   
<img width="1055" alt="image" src="https://github.com/user-attachments/assets/f1de75e2-5978-4bdd-8a4f-e7f9e84fa5f4" />    
(고객의 이름을 조건으로 검색해서 원하는 회원만 내역 검색 가능(+페이징))<br><br><br>

--- 
💭 프로젝트를 진행하며 느낀 점  

<br>

1) 조회 성능 개선: 이번 프로젝트를 통해 많은 것을 배우는 경험을 할 수 있었습니다. 처음에는 데이터베이스 조회 성능 최적화를 고려하지 않고 구현을 진행했지만, 불필요한 데이터와 엔티티까지 쿼리문이 발생하는 것을 보고 실제 운영 환경에서는 대규모 데이터 조회 시 성능 문제가 발생할 수 있음을 깨닫게 되었습니다. 이를 해결하기 위해 쿼리 최적화, N+1 문제 해결 등을 적용하며 성능을 개선한 경험을 쌓을 수 있었습니다.(중복 조회 데이터 캐싱(Cache Aside 읽기 전략), 트랜잭션 readOnly 및 전파 속성 수정 후 라이트하우스 테스트 예정, +jmeter로 부하 테스트도 예정)<br>  <br>이 과정에서 도메인 주도 설계(DDD)를 배우고 적용하며 유지보수성을 고려한 데이터 모델링을 해보고, 단순한 테이블 설계를 넘어서서 비즈니스 로직과 연계된 도메인 중심 모델링을 고민하며 성능적인 부분과 추후의 확장 가능성과 유지보수성을 고려해서 정규화와 비정규화의 균형을 맞춘 설계를 해보게 됐습니다.    
추가로, 도메인의 핵심 비즈니스 로직의 일부가 되는 검증 로직을 별도의 도메인 서비스(상호작용 관련 규칙 검증 서비스)에서 핵심 비즈니스 규칙을 캡슐화하고 검증하면 애플리케이션 서비스는 사용자의 특정 유스케이스만을 집중할 수 있게 되는 새로운 설계도 배워 적용을 고려하고 있습니다.


2) 트랜잭션 및 동시성 처리: 공유 자원인 돌봄사의 돌봄 가능 날짜에 대해 여러 고객이 예약 요청을 발생시킬 때 레이스 컨디션이 발생하여 데이터가 불일치 하게된 경우가 있었습니다.  멀티스레드 환경에서 공유 자원에 대해 동시성 처리를 하여 데이터 정합성을 지키기 위해 어떻게 트랜잭션의 생명주기를 관리해 줘야 하는가에 대한 고민도 하고 자바의 스레드, 낙관적 락과 비관적 락에 대해 배워 실제 테스트 코드로 ExecutorService를 이용해 스레드 풀에 스레드를 2000개 정도 설정한 후, 각 스레드가 동시성 문제가 생기는 비즈니스 로직(임계 영역)을 수행하게 해서 데이터 정합성이 보장되는지 직접 테스트하고 비관적 락(X-Lock)적용하여 동시성 문제를 해결하는 경험을 했습니다.  
이 프로젝트에서는 synchronized + ConcurrentHashMap와 낙관적 락을 사용하지 않은 이유는, 일단 첫번째 synchronized + ConcurrentHashMap로도 현재 분산 서버 환경이 아니기 때문에 충분히 해결할 수 있지만 분산 서버 환경이라 가정하고 해결을 하고 싶어서이고, 두번째 낙관적 락을 사용하지 않은 이유는 낙관적 락은 다른 트랜잭션에서 읽기는 허용하고 쓰기를 막기 때문에 성능이 비관적 락보다 좋을 수 있지만 사용자가 많이 몰리는 상황이라 가정하고 했을 때, 예약은 중요한 비즈니스 로직이기 때문에 데이터 정합성을 확실히 지키고, 낙관적 락을 사용했을 경우 트랜잭션을 개발자가 관리할 수 없다는 단점이 있어서 일일이 롤백 해줘야 하는 비용도 무시할 수 없기에 비관적 락을 택했습니다.   

3) Redis(Lettuce 기반)를 이용하여 비로그인(게스트), 로그인(회원) 신분의 유저가 챗봇을 이용하여 대화를 진행할 경우, 신분에 따라 대화 기록 저장 시간(TTL)을 유동적으로 설정도 해 보았고, 로그인 시 비로그인 신분으로 이전에 진행한 대화 기록이 있다면 이전 대화 기록을 마이그레이션도 해 보았습니다.(중요하지 않은 대화 기록이기에 휘발시킬 수 있는 속성을 가진 Redis를 채택.)  
Redis 서버 인메모리에 저장 시 ObjectMapper를 이용하여 DTO 객체를 JSON 데이터 형식으로 직렬화하여 저장하고, 반대로 대화 기록을 불러올 때에는 JSON 데이터를 자바 객체로 역직렬화하여 조회하였고 Redis를 이용해서 DB 한 곳으로의 트래픽을 분산시켜 부하를 줄일 수 있다는 것이 어떤 구조로 그럴 수 있는건 지 직접 알 수 있던 경험이었습니다. 
그리고 이 개발 경험으로 Redis에 캐시 읽기 전략에 일반적으로 사용하는 Look Aside(Cache Aside) 패턴, Read Through 패턴 등과 같은 여러 방법이 있다는 것도 알게 되어 상황에 맞는 전략을 이용해야 효율적이라는 것을 알게 됐습니다.  


4) 실시간 웹소켓 채팅 기능 구현: 고객과 돌봄사 간의 1:1 관계의 웹소켓 + STOMP(pub/sub) 기반 실시간 채팅을 구현하여 고객이 예약하기 전 돌봄사와 대화하여 궁금한 점을 묻거나, 자유로운 대화를 할 수 있도록 사용자 편의성을 위해 개발을 해봤습니다. mysql 네이티브 쿼리의 윈도우 함수를 사용해서 진행 중인 대화방 목록을 조회할 때 카카오톡과 같이 상대방의 정보와 마지막으로 진행된 메시지와 내가 아직 읽지 않은 메시지의 개수, 날짜 및 시간이 보이도록 적용하고, 현재 접속 중이지 않은 다른 채팅방에서 메시지를 수신할 경우 알림을 표시해주고 별도의 알림탭을 만들어 읽지 않은 메시지만 모아볼 수 있게 개발을 해봤습니다.  
해당 기능의 개발을 해보면서 JPA와 QueryDSL으로는 아직 한계가 있다는 것을 느끼고 상황에 따라 복잡한 쿼리의 경우 네이티브 쿼리는 선택이 아닌 필수라고 느끼게 되었습니다.  
WebSocket + STOMP 기반 실시간 웹 소켓 채팅을 구현해 보면서 pub/sub의 구조를 새로 배우게 될 수 있었고, 웹소켓의 경우 HTTP 통신할 때와 다른 인증 세션을 사용한다는 것을 알게 되어 Authentication을 별도로 웹소켓 인증 서버에 명시적으로 설정해 줘야 한다는 것도 알게 되었습니다.  


5) 인증 및 보안: 토큰을 구현하면서 액세스 토큰과 리프레시 토큰을 로컬 스토리지, 세션 스토리지, 브라우저(쿠키) 중 어느 곳에 저장하느냐에 따라 장단점과 특징이 있다는 걸 이해하게 되었고, JWT 토큰 관리 부분에 있어서는 사용자 입장에서 토큰의 짧은 생명 주기 때문에 로그인 상태가 풀리는 것이 불편할 것 같다 생각이 들어, 사용자의 동작을 감지해서 리프레시 토큰이 유효할 경우 새로운 액세스 토큰을 발급해서 교체하여 쾌적한 이용을 할 수 있게 했습니다.  
처음에는 액세스 토큰과 리프레시 토큰을 SSR 방식에서 개발했다 보니 로컬 스토리지와 세션 스토리지를 저장하는 경우가 아닌 브라우저(쿠키)에 저장하는 경우라면 서버에서 저장해야 하는 줄 알았지만, CSR 방식에서는 서버에서 화면을 렌더링 하는 것이 아니라, 서버가 응답한 데이터를 가지고 화면을 그리기 때문에 클라이언트에서 토큰을 받아 저장하는 방법을 사용한다는 것을 알게 되었습니다.   
추가로 액세스 토큰과 리프레시 토큰을 로컬 스토리지, 세션 스토리지, 브라우저(쿠키) 중 어느 곳에 저장하느냐에 따라 보안과 관련해서 장단점과 특징이 있다는 걸 정확히 이해하게 되었고,  
페이징 시에 서버에서 정해진 개수의 데이터를 클라이언트에게 반환하는 방식만이 페이징의 전부라고 생각했지만, 프론트엔드에서 디바이스 크기에 따라 페이징 크기를 조정하는 방식도 있다는 걸 배우게 됐습니다.  
그리고 민감한 보안 문제에 있어서도 스프링 시큐리티의 내부에서 동작하는 구조들에 대해 확실히 파악을 할 수 있던 경험도 하게 되었습니다.


1인 개발 프로젝트이지만 협업하는 상황을 스스로 가정하며 개발을 진행했고, 프론트엔드가 유연하게 데이터를 활용할 수 있도록 API 응답 방식을 고민하는 과정에서 프론트 개발자가 받기 좋은 커스텀 응답 객체의 중요성을 깊이 깨닫게 되었습니다.  

