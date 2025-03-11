/*
document.addEventListener("DOMContentLoaded", () => {
    // 경력 입력 필드 토글 처리
    const roleSelect = document.getElementById("role");
    const careerField = document.getElementById("careerField");
    const careerInput = document.getElementById("careerYear");

    roleSelect.addEventListener("change", () => {
        if (roleSelect.value === "PET_SITTER") {
            careerField.style.display = "block";
            careerInput.required = true;
        } else {
            careerField.style.display = "none";
            careerInput.required = false;
            careerInput.value = ""; // 값 초기화
        }
    });

    // 회원가입 폼 전송 처리
    const signupForm = document.querySelector("form");
    signupForm.addEventListener("submit", (event) => {
        event.preventDefault(); // 기본 폼 제출 방지

        // 폼 데이터를 JSON으로 변환
        const formData = new FormData(signupForm);
        const formObject = {};
        formData.forEach((value, key) => {
            formObject[key] = value;
        });

        // JSON 형식으로 요청 보내기
        fetch("/api/pets-care/members/new", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formObject),
        })
        .then((response) => {
            if (response.ok) {
                alert("회원가입이 완료되었습니다!");
                window.location.href = "/pets-care/login"; // 회원가입 완료 후 로그인 페이지로 이동
            } else {
                return response.json().then((errorData) => {
                    displayValidationErrors(errorData); // 오류 처리 함수 호출
                    throw new Error("회원가입 오류");
                });
            }
        })
        .catch((error) => {
            console.error("회원가입 요청 중 오류 발생:", error);
            alert("회원가입 중 문제가 발생했습니다. 다시 시도해주세요.");
        });
    });
});

// 서버에서 받은 오류 메시지를 화면에 표시하는 함수
function displayValidationErrors(errors) {
    // 모든 오류 메시지를 각 필드에 표시
    Object.keys(errors).forEach((field) => {
        const errorElement = document.getElementById(field + "Error");
        if (errorElement) {
            errorElement.textContent = errors[field];  // 서버에서 받은 메시지 표시
        }
        const inputElement = document.getElementById(field);
        if (inputElement) {
            inputElement.classList.add("error");  // 오류가 있는 필드에 스타일 추가
        }
    });
}
*/

// 시큐리티 회원가입(+csrf 토큰 추가해서 요청)
document.addEventListener("DOMContentLoaded", () => {
    // 경력 입력 필드 토글 처리
    const roleSelect = document.getElementById("role");
    const careerField = document.getElementById("careerField");
    const careerInput = document.getElementById("careerYear");

    roleSelect.addEventListener("change", () => {
        if (roleSelect.value === "PET_SITTER") {
            careerField.style.display = "block";
            careerInput.required = true;
        } else {
            careerField.style.display = "none";
            careerInput.required = false;
            careerInput.value = ""; // 값 초기화
        }
    });

    // 회원가입 폼 전송 처리
    const signupForm = document.querySelector("form");
    signupForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // 기본 폼 제출 방지

        // 기존 오류 메시지 초기화
        clearValidationErrors();

        // CSRF 토큰 가져오기
        const csrfToken = document.querySelector("input[name='_csrf']").value;

        // 폼 데이터를 JSON으로 변환
        const formData = new FormData(signupForm);
        const formObject = {};
        formData.forEach((value, key) => {
            formObject[key] = value;
        });

        try {
            const response = await fetch("/api/pets-care/members/new", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken, // CSRF 토큰 추가
                },
                body: JSON.stringify(formObject),
            });

            if (response.ok) {
                alert("회원가입이 완료되었습니다!");
                window.location.href = "/pets-care/login"; // 회원가입 완료 후 로그인 페이지로 이동
            } else {
                const errorData = await response.json();
                displayValidationErrors(errorData); // 오류 메시지 표시
            }
        } catch (error) {
            console.error("회원가입 요청 중 오류 발생:", error);
            alert("회원가입 중 문제가 발생했습니다. 다시 시도해주세요.");
        }
    });
});

// 기존 오류 메시지 초기화 함수
function clearValidationErrors() {
    document.querySelectorAll(".error-message").forEach((errorElement) => {
        errorElement.textContent = ""; // 오류 메시지 제거
    });
    document.querySelectorAll(".error").forEach((inputElement) => {
        inputElement.classList.remove("error"); // 입력 필드의 오류 스타일 제거
    });
}

// 서버에서 받은 오류 메시지를 화면에 표시하는 함수
function displayValidationErrors(errors) {
    Object.keys(errors).forEach((field) => {
        const errorElement = document.getElementById(field + "Error");
        if (errorElement) {
            errorElement.textContent = errors[field]; // 서버에서 받은 메시지 표시
        }
        const inputElement = document.getElementById(field);
        if (inputElement) {
            inputElement.classList.add("error"); // 오류가 있는 필드에 스타일 추가
        }
    });
}

