// HTML 요소에서 member-id 값을 가져오기
const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

if (memberId === null) {
    console.error("회원 정보를 찾을 수 없습니다.");
}

// 폼 변경 감지 & 페이지 이탈 방지
document.addEventListener("DOMContentLoaded", function () {
    let isFormDirty = false;
    const formInputs = document.querySelectorAll("#editProfileForm input, #editProfileForm textarea");

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

    // 회원 탈퇴 버튼 이벤트 리스너 등록
    const deleteMemberBtn = document.getElementById("deleteMemberBtn");
    if (deleteMemberBtn) {
        deleteMemberBtn.addEventListener("click", deleteMember);
    }

    // 회원 정보 수정 폼 이벤트 리스너 등록
    const editProfileForm = document.getElementById("editProfileForm");
    if (editProfileForm) {
        editProfileForm.addEventListener("submit", updateMember);
    }
});

// 회원 정보 수정 요청
let isFetching = false;

async function updateMember(event) {
    event.preventDefault();
    if (isFetching) return;
    isFetching = true;

    // 기존 오류 메시지 제거
    document.querySelectorAll(".error-message").forEach(el => el.remove());
    document.querySelectorAll(".error").forEach(el => el.classList.remove("error"));

    try {
        const formData = {
            password: document.getElementById("password")?.value,
            name: document.getElementById("name")?.value,
            nickName: document.getElementById("nickName")?.value,
            email: document.getElementById("email")?.value,
            phoneNumber: document.getElementById("phoneNumber")?.value,
            zipcode: document.getElementById("zipcode")?.value,
            address: document.getElementById("address")?.value,
            introduction: document.getElementById("introduction")?.value,
        };

        const careerYearInput = document.getElementById("careerYear");
        if (careerYearInput) {
            formData.careerYear = careerYearInput.value;
        }

        const response = await fetch(`/api/pets-care/members/${memberId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            if (response.status === 400) {
                // 유효성 검사 오류 처리
                const errors = await response.json();
                Object.keys(errors).forEach(field => {
                    const inputField = document.getElementById(field);
                    if (inputField) {
                        inputField.classList.add("error"); // 입력 필드 테두리 강조

                        const errorMessage = document.createElement("div");
                        errorMessage.classList.add("error-message");
                        errorMessage.textContent = errors[field];

                        inputField.insertAdjacentElement("afterend", errorMessage); // 필드 아래 오류 메시지 추가
                    }
                });
                throw new Error("입력 값을 확인하세요.");
            } else {
                throw new Error("회원 정보 수정 실패");
            }
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