# [🐶 반려견 돌봄 서비스](http://ec2-3-36-128-60.ap-northeast-2.compute.amazonaws.com:9090/pets-care/main)

반려견 돌봄을 위한 펫시터 서비스입니다. 고객은 원하는 펫시터를 선택해서 예약하고, 돌봄 기록을 확인하며, 리뷰를 남길 수 있습니다.  
펫시터는 자신의 자격을 인증하고, 예약을 관리하며, 돌봄 기록을 작성할 수 있습니다.
실제로 집에서 현재 반려견을 키우고 있기도 하고, 프로젝트 주제를 선정을 하는 과정에서 저의 실제 일상생활 상황에 대입해서 개발을 하면 재미와 흥미를 갖고 할 수 있을 것 같아 반려견 돌봄 서비스로 주제를 선정하게 되었습니다.  
이 서비스는 반려견의 케어 기록 관리, 돌봄 일정 예약 기능 등을 제공하며, 사용자와 돌봄사와 상호작용할 수 있는 플랫폼을 목표로 하고 있습니다. 

--- 

✨ 주요 기능  

<br>

👥 회원 & 인증

<ol>회원 관리: 일반 회원과 펫시터 등록</ol>

<ol>시큐리티 로그인: 자체 회원가입과 로그인 Spring Security 기능 제공</ol>

<ol>소셜 로그인: 구글, 네이버, 카카오 지원(OAuth 2.0)</ol>

<ol>JWT 기반 인증: Access & Refresh Token 활용</ol>  

<ol>회원 신고 기능: 부적절한 회원 신고 기능(+개발 예정)</ol>  

<ol>고객과 돌봄사간의 채팅 기능(+개발 예정)</ol>  

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

<ol>고객 예약: 원하는 펫시터의 예약 가능 날짜 선택 후 예약 신청</ol>

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

+웹 서비스 이용 가이드 AI 챗봇(+개발 예정)  

---

🛠 기술 스택  

<br>

🔹 BE

Spring Boot / JAVA

JPA & Spring Data JPA - ORM 및 데이터 관리

QueryDSL & JPQL - 동적 쿼리 처리

Spring Security

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

<img width="1236" alt="image" src="https://github.com/user-attachments/assets/d9de55a2-0c08-47c4-8ece-c181dd0367b7" />  

---

# [📌 API 명세](http://ec2-3-36-128-60.ap-northeast-2.compute.amazonaws.com:9090/swagger-ui/index.html)

API 명세서는 Swagger 문서에서 확인할 수 있습니다.  

---

# 메인 페이지(1)
<img width="1096" alt="image" src="https://github.com/user-attachments/assets/7ea3ccf6-b0af-4d7e-9cda-4df118d372d0" />  
<br><br><br>

# 메인 페이지(2)
<img width="998" alt="image" src="https://github.com/user-attachments/assets/cabc3260-a45f-4d26-9c43-fcb6968e9c80" />  
(돌봄 리뷰는 가장 최근에 작성된 리뷰 6개만 조회(+페이징))

<br><br><br>

# 회원 정보 페이지 - 고객 시점(1)
<img width="1291" alt="image" src="https://github.com/user-attachments/assets/86a70705-5690-494b-a6d0-3af2f2efc403" />
<img width="934" alt="image" src="https://github.com/user-attachments/assets/4959f461-1413-4672-be81-47f6d7214f9c" />  
<img width="959" alt="image" src="https://github.com/user-attachments/assets/5b2f0ece-22ee-41c2-808a-67cbdaff9625" />  

(한 번에 여러 마리의 반려견 정보 수정, 등록 가능)<br><br>
<img width="1176" alt="image" src="https://github.com/user-attachments/assets/d8de057e-b7b6-4f6b-93cb-bdd61992f249" />  
(자신이 등록한 모든 돌봄 리뷰 목록 조회 페이지(+페이징), 클릭 시 리뷰 작성한 해당 예약 페이지로 이동)<br><br><br>

# 회원 정보 페이지 - 돌봄사 시점(2)
<img width="1184" alt="image" src="https://github.com/user-attachments/assets/f6b0c6ed-6c72-4d3e-b9bd-b006c8c01912" />  
<img width="1165" alt="image" src="https://github.com/user-attachments/assets/0bfab71d-e282-436e-9ba5-b7c53c9ae2f3" />    
(자신에게 등록된 모든 돌봄 리뷰 목록 조회 페이지((+페이징), 돌봄사도 한 번에 여러 개의 자격증 정보 수정, 등록 가능), 클릭 시 해당 예약 페이지로 이동)<br><br>
<img width="1061" alt="image" src="https://github.com/user-attachments/assets/fa273643-7902-464f-8296-17b20df250a5" />  
(자신이 등록한 모든 돌봄 케어 로그 목록 조회 페이지(+페이징), 클릭 시 케어 로그 작성한 해당 예약 페이지로 이동)<br><br><br>

# 돌봄 예약 가능한 돌봄사 목록  
<img width="1189" alt="image" src="https://github.com/user-attachments/assets/238016e4-4890-49bf-aac6-607abc5ac23e" />      
(돌봄 예약 가능 날짜를 등록한 펫시터 중 현재 예약이 가능한 펫시터만 조회(+페이징))<br><br><br>

# 돌봄 예약 가능한 돌봄사 목록 중 돌봄사를 택했을 때  
<img width="741" alt="image" src="https://github.com/user-attachments/assets/f3ad6be6-681e-42e2-b63a-ff709f9d501b" />    
<img width="760" alt="image" src="https://github.com/user-attachments/assets/a1dd792b-b0a7-4da6-90fc-9f2e5aacfe73" />  
<img width="766" alt="image" src="https://github.com/user-attachments/assets/4c02e044-d648-49dc-a966-299014af3999" />  

(선택한 돌봄사의 정보와 스펙들을 보여주고, 해당 돌봄사에게 작성된 모든 돌봄 리뷰를 조회(+리뷰는 최상위 5개  
조회 후, 이후 비동기로 5개씩 페이징))<br><br><br>

# 고객 시점: 돌봄 예약 페이지(1)
<img width="1242" alt="image" src="https://github.com/user-attachments/assets/7787b71b-2259-4316-828b-162c5f248943" />  
(해당 돌봄사가 등록한 돌봄 예약 가능한 날짜를 예약 가능한 날짜에 반환, 맡길 반려견을 선택)<br><br><br>

# 고객 시점: 돌봄 예약 페이지(2)
<img width="1209" alt="image" src="https://github.com/user-attachments/assets/befd81c8-dedb-4dc8-b209-4b01178b50ab" />  
(선택한 날짜의 돌봄 가격과 회원이 보유중인 적립금을 조회하여 적립금 사용 입력란에 숫자를 입력하면 비동기로 최종 결제 금액이 자동으로 산정)<br><br><br>

# 고객 시점: 돌봄 예약 내역 조회 페이지(돌봄사도 동일)
<img width="1216" alt="image" src="https://github.com/user-attachments/assets/7d8893d9-c123-47c4-a1e1-1c62979a2513" />
(돌봄 예약 내역 전체 조회(+페이징), 클릭 시 해당 예약 페이지로 이동)<br><br><br>

# 고객 시점: 특정 돌봄 예약 상세 조회 페이지(1)(돌봄사도 동일)
<img width="857" alt="image" src="https://github.com/user-attachments/assets/27471f7d-7d70-4749-9bcc-8cb73449af35" />        
<br><br><br>

# 고객 시점: 특정 돌봄 예약 상세 조회 페이지(2)(돌봄사도 동일)
<img width="584" alt="image" src="https://github.com/user-attachments/assets/8ff1213b-0c3b-4676-b846-6c1c6db11599" />  

(별점 리뷰는 0.5 단위로도 가능(취소된 예약에는 리뷰 작성 불가능))<br><br><br>

# 펫시터 시점: 돌봄 예약 가능한 날짜 목록 페이지
<img width="1245" alt="image" src="https://github.com/user-attachments/assets/1c08059f-228f-4c38-9e46-77e09863eecc" />
(돌봄사가 등록한 돌봄 예약 가능 날짜만 예약이 가능(+페이징))<br><br><br>

# 펫시터 시점: 등록한 돌봄 예약 가능한 날짜 수정
<img width="1251" alt="image" src="https://github.com/user-attachments/assets/e6930642-cbb1-487a-99c2-8efd559123db" />
<img width="1281" alt="image" src="https://github.com/user-attachments/assets/b1ed7b42-976d-437d-8737-9a673d5834ec" />
<br><br><br>

# 펫시터 시점: 특정 돌봄 예약 상세 조회 페이지 - 돌봄 케어 로그 작성 부분
<img width="1057" alt="image" src="https://github.com/user-attachments/assets/31cec61d-1eb9-46d3-876c-486e7cc90620" />  
(취소된 예약에는 돌봄 케어 로그 작성 불가능)<br><br><br>

# 돌봄 이용 후기 페이지
<img width="1163" alt="image" src="https://github.com/user-attachments/assets/23f9cd3e-4d0c-4911-804a-3e50e2daa7c3" />
<img width="1158" alt="image" src="https://github.com/user-attachments/assets/8a557488-4c5b-4f6b-9127-077063ceac57" />  
(돌봄 이용 후기 페이지에서 돌봄 리뷰가 존재하는 돌봄사만 검색 리스트에 반환해서 원하는 돌봄사의 리뷰만 조건 검색 가능(+페이징))<br><br><br>

# 관리자 페이지 - 회원 관리
<img width="1226" alt="image" src="https://github.com/user-attachments/assets/9fab9497-09be-4658-a5d4-a4218fb7e28a" />
<img width="1060" alt="image" src="https://github.com/user-attachments/assets/708d4b65-910a-4980-92c3-ec683d6e43d3" />    
<img width="1065" alt="image" src="https://github.com/user-attachments/assets/a437a732-9e66-49e3-aedb-137f84a5ad84" />  
<img width="1214" alt="image" src="https://github.com/user-attachments/assets/f87a13f9-ae23-4624-839e-e71a9bc846c9" />
(회원의 이름을 조건으로 검색해서 원하는 회원만 검색 가능(+페이징), 관리자 권한으로 부적절한 회원 탈퇴 처리 가능)<br><br><br>

# 관리자 페이지 - 돌봄 예약 관리(+돌봄 케어 로그, 리뷰 관리)
<img width="1253" alt="image" src="https://github.com/user-attachments/assets/a28643cb-20d7-4f68-9b60-4e1cf738cfb2" />
<img width="883" alt="image" src="https://github.com/user-attachments/assets/0ec27386-3e2f-4572-b6b9-c452e3427eb8" />
<img width="591" alt="image" src="https://github.com/user-attachments/assets/cb1f6816-2430-49a7-abea-12356626d75e" />  

(고객의 이름을 조건으로 검색해서 원하는 돌봄 예약만 검색 가능(+페이징), 관리자 권한으로 돌봄 예약 및 부적절한 돌봄 케어 로그 및 부적절한 리뷰 삭제 가능)<br><br><br>

# 관리자 페이지 - 회원 포인트 내역 관리(적립, 사용)
<img width="1243" alt="image" src="https://github.com/user-attachments/assets/0349f07c-56a4-413f-be90-9e61c176d3da" />    
<img width="1232" alt="image" src="https://github.com/user-attachments/assets/bca1c35f-cda7-401f-a0a6-22f7dabd0fae" />  
(고객의 이름을 조건으로 검색해서 원하는 회원만 내역 검색 가능(+페이징))<br><br><br>

--- 
💭 프로젝트를 진행하며 느낀 점  

<br>

이번 프로젝트를 통해 많은 것을 배우는 경험을 할 수 있었습니다. 특히, 초기에는 데이터베이스 조회 성능 최적화를 고려하지 않고 구현을 진행했지만, 실제 운영 환경에서는 대규모 데이터 조회 시 성능 문제가 발생할 수 있음을 깨닫게 되었습니다. 이를 해결하기 위해 쿼리 최적화, 조인 최적화, 불필요한 데이터 조회 최소화 등의 기법을 적용하였고, N+1 문제 해결, 페이징 처리를 통해 성능을 개선하는 경험을 쌓았습니다. 이러한 과정에서 단순한 CRUD를 넘어 데이터베이스와 쿼리 성능을 깊이 있게 이해하게 되었습니다.  

또, 트랜잭션 관리를 고려하여 데이터의 일관성을 유지하는 것에 대해 고민을 하게 되고, 동시성 처리에 대해 어떻게 트랜잭션의 생명주기를 관리해 줘야 하는가에 대한 고민도 하며 적용하는 과정을 거치며 안정적인 애플리케이션 설계의 중요성을 깨달았습니다. 

비록 실제 협업 경험은 없지만, 협업하는 상황을 스스로 가정하며 개발을 진행했고, 프론트엔드가 유연하게 데이터를 활용할 수 있도록 API 응답 방식을 고민하는 과정에서 확장 가능한 설계의 중요성을 깊이 깨닫게 되었습니다. 처음에는 액세스 토큰과 리프레시 토큰을 SSR 방식에서 개발했다 보니 로컬 스토리지와 세션 스토리지를 저장하는 경우가 아닌 브라우저(쿠키)에 저장하는 경우라면 서버에서 저장해야 하는 줄 알았지만, CSR 방식에서는 클라이언트에서 토큰을 받아 저장하는 방법을 사용한다는 것을 알게 되었습니다. 이 과정에서 액세스 토큰과 리프레시 토큰을 로컬 스토리지, 세션 스토리지 브라우저(쿠키) 중 어느 곳에 저장하느냐에 따라 장단점과 특징이 있다는 걸 정확히 이해하게 되었고 또, 페이징 시에 서버에서 정해진 개수의 데이터를 클라이언트에게 반환하는 방식만이 페이징의 전부라고 생각했지만, 프론트엔드에서 디바이스 크기에 따라 페이징 크기를 조정하는 방식도 있다는 걸 배우게 됐습니다.

이 과정에서 데이터베이스 성능 최적화와 도메인 주도 설계(DDD) 개념을 학습하고 적용하는 경험을 하였으며, 단순한 테이블 설계를 넘어 비즈니스 로직과 연계된 도메인 중심 모델링을 고민하는 계기가 되었습니다. 또한, 정규화와 비정규화의 적절한 균형을 고려한 데이터 설계를 시도하면서, 유지보수성과 확장성을 모두 충족하는 구조를 만들기 위해 노력했습니다. 그리고 민감한 보안 문제에 있어서도 스프링 시큐리티의 내부에서 동작하는 구조들에 대해 확실히 파악을 할 수 있던 경험도 하게 되었습니다.

이번 프로젝트를 통해 백엔드 개발자로서 더 나은 설계를 고민하고, 성능 최적화와 확장성까지 고려하는 개발자로 성장할 수 있는 계기가 되었다고 생각합니다.
