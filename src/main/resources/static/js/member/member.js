// HTML 요소에서 member-id 값을 가져오기
const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

if (memberId === null) {
    console.error("회원 정보를 찾을 수 없습니다.");
}

document.addEventListener("DOMContentLoaded", function () {
    let isFormDirty = false;
    const formInputs = document.querySelectorAll("#editProfileForm input, #editProfileForm textarea");

    const roleSelect = document.getElementById("role");
    const careerField = document.getElementById("careerField");
    const careerInput = document.getElementById("careerYear");

    // 초기 설정: roleSelect의 값에 따라 careerField 표시 여부 결정
    if (roleSelect.value == "PET_SITTER") {
        careerField.style.display = "block";
        careerInput.required = true;
    } else {
        careerField.style.display = "none";
        careerInput.required = false;
        careerInput.value = ""; // 초기화
    }

    // 역할 변경 시 경력 입력 필드 보이기/숨기기
    roleSelect.addEventListener("change", () => {
        if (roleSelect.value == "PET_SITTER") {
            careerField.style.display = "block";
            careerInput.required = true;
        } else {
            careerField.style.display = "none";
            careerInput.required = false;
            careerInput.value = ""; // 초기화
        }
    });

    // 폼 변경 감지 (페이지 이탈 방지)
    formInputs.forEach(input => {
        input.addEventListener("input", () => isFormDirty = true);
        input.addEventListener("change", () => isFormDirty = true);
    });

    window.addEventListener("beforeunload", function (event) {
        if (isFormDirty) {
            event.preventDefault();
            event.returnValue = "회원 변경 사항이 저장되지 않을 수 있습니다. 그래도 이동하시겠습니까?";
        }
    });

    document.getElementById("editProfileForm")?.addEventListener("submit", function () {
        isFormDirty = false;
        window.removeEventListener("beforeunload", arguments.callee);
    });

    // 프로필 이미지 미리보기 기능 추가
    const profileImageInput = document.getElementById("profileImage");
    const profilePreview = document.getElementById("profilePreview");
    const deleteProfileImageBtn = document.getElementById("deleteProfileImageBtn");

    // 파일 선택 시 미리보기 업데이트
    if (profileImageInput && profilePreview) {
        profileImageInput.addEventListener("change", function (event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    profilePreview.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // 프로필 이미지 삭제 버튼 클릭 시 기본 이미지로 변경
    if (deleteProfileImageBtn) {
        deleteProfileImageBtn.addEventListener("click", function (event) {
            event.preventDefault(); // 폼 제출을 방지

            if (!confirm("프로필 이미지를 기본 이미지로 변경하시겠습니까?")) {
                return;
            }

            // 프로필 이미지를 기본 이미지로 변경 (클라이언트 측에서만 처리)
            profilePreview.src = "/images/default-profile.png";  // 기본 이미지로 설정

            // 파일 선택 필드 초기화
            profileImageInput.value = "";

            // 삭제 버튼 숨기기
            deleteProfileImageBtn.style.display = "none";
        });
    }

    // 프로필 이미지 미리보기 클릭 시 파일 선택
    if (profilePreview) {
        profilePreview.addEventListener("click", function () {
            profileImageInput.click();  // 파일 input을 클릭하는 효과
        });
    }
});

// 회원 정보 수정 요청
let isFetching = false;

async function updateMember(event) {
    event.preventDefault();
    if (isFetching) return;
    isFetching = true;

    document.querySelectorAll(".error-message").forEach(el => el.remove());
    document.querySelectorAll(".error").forEach(el => el.classList.remove("error"));

    try {
        const formData = new FormData();

        formData.append("request", new Blob([JSON.stringify({
            password: document.getElementById("password")?.value,
            name: document.getElementById("name")?.value,
            nickName: document.getElementById("nickName")?.value,
            email: document.getElementById("email")?.value,
            phoneNumber: document.getElementById("phoneNumber")?.value,
            zipcode: document.getElementById("zipcode")?.value,
            address: document.getElementById("address")?.value,
            role: document.getElementById("role")?.value,
            introduction: document.getElementById("introduction")?.value,
            careerYear: document.getElementById("careerYear")?.value || null
        })], { type: "application/json" }));

        // 프로필 이미지 추가
        const profileImage = document.getElementById("profileImage").files[0];
        if (profileImage) {
            formData.append("profileImage", profileImage);
        }

        const response = await fetch(`/api/pets-care/members/${memberId}`, {
            method: "PUT",
            body: formData
        });

        if (!response.ok) {
            if (response.status === 400) {
                const errors = await response.json();

                // 비밀번호 오류 메시지 처리
                if (errors.details) {
                    const passwordField = document.getElementById("password");
                    if (passwordField) {
                        passwordField.classList.add("error"); // 비밀번호 필드에 error 클래스 추가

                        const errorMessage = document.createElement("div");
                        errorMessage.classList.add("error-message");
                        errorMessage.textContent = errors.details;  // error.details를 사용하여 메시지 표시

                        passwordField.insertAdjacentElement("afterend", errorMessage); // 오류 메시지 삽입
                    }
                }

                // 다른 필드에 대한 오류 메시지 처리
                Object.keys(errors).forEach(field => {
                    if (field !== "details") {  // 비밀번호 오류는 이미 처리했으므로 제외
                        const inputField = document.getElementById(field);
                        if (inputField) {
                            inputField.classList.add("error");

                            const errorMessage = document.createElement("div");
                            errorMessage.classList.add("error-message");
                            errorMessage.textContent = errors[field];

                            inputField.insertAdjacentElement("afterend", errorMessage);
                        }
                    }
                });
                throw new Error("입력 값을 확인하세요.");
            } else {
                throw new Error("회원 정보 수정 실패");
            }
        }

        const data = await response.json();

        if (data.roleChanged) {
            alert("회원 역할이 변경되었습니다. 다시 로그인 해주세요.");

            // 로그아웃 폼 자동 제출
            const logoutForm = document.getElementById("logoutForm");
            if (logoutForm) {
                logoutForm.submit();
            } else {
                console.error("로그아웃 폼을 찾을 수 없습니다.");
            }
            return;
        }

        alert("회원 정보가 수정되었습니다.");
        window.location.href = `/pets-care/members/${memberId}/myPage`;
    } catch (error) {
        alert(error.message);
    } finally {
        isFetching = false;
    }
}

// 회원 탈퇴 요청
async function deleteMember() {
    if (!memberId) {
        alert("회원 정보를 찾을 수 없습니다.");
        return;
    }

    if (!confirm("정말로 회원을 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.")) {
        return;
    }

    try {
        const response = await fetch(`/api/pets-care/members/${memberId}`, {
            method: "DELETE"
        });

        if (!response.ok && response.status !== 204) throw new Error("회원 탈퇴 실패");

        alert("회원 탈퇴가 완료되었습니다.");
        window.location.href = "/logout";
    } catch (error) {
        alert("오류 발생: " + error.message);
    }
}
