/*
function login() {
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('password').value;

    fetch('/pets-care/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ loginId, password }), // JSON으로 데이터 전송
    })
        .then((response) => {
            if (!response.ok) {
                // 응답 코드가 200-299 범위가 아닌 경우 에러 처리
                return response.json().then((error) => {
                    throw new Error(error.message || '로그인 실패');
                });
            }
            return response.json(); // JSON 응답 반환
        })
        .then((result) => {
            alert(`로그인 성공! 환영합니다, ${result.name}님!`);
            window.location.href = '/pets-care/main'; // 메인 페이지로 리다이렉트
        })
        .catch((error) => {
            // 에러 메시지 출력
            console.error('로그인 요청 실패:', error);
            alert(error.message || '로그인 요청 중 문제가 발생했습니다.');
        });
}
*/

// 시큐리티 로그인 커스텀 설정 -> application/x-www-form-urlencoded 요청으로 변환(Spring Security 기본 로그인 필터 (UsernamePasswordAuthenticationFilter)는 JSON 요청을 처리하지 않음)
$(document).ready(function () {
    $("#loginForm").on("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        $.ajax({
            type: "POST",
            url: "/login",
            data: $("#loginForm").serialize(), // 폼 데이터를 URL-encoded 형식으로 변환
            headers: {
                "X-CSRF-TOKEN": $("#csrfToken").val() // CSRF 토큰 추가
            },
            success: function () {
                window.location.href = "/pets-care/main"; // 로그인 성공 시 홈으로 이동
            },
            error: function (xhr) {
                try {
                    const errorMessage = JSON.parse(xhr.responseText).message;
                    alert(errorMessage); // 로그인 실패 시 alert 출력
                } catch (e) {
                    alert("로그인 중 문제가 발생했습니다."); // JSON 파싱 실패 시 기본 메시지 출력
                }
            }
        });
    });
});


