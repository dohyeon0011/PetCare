// HTML 요소에서 member-id 값을 가져오기
const sessionMemberId = document.getElementById("memberData").getAttribute("data-member-id");
const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null; // 'NaN' 체크는 자동으로 되며, 숫자 변환이 잘 되면 문제 없음

// memberId가 null이면 적절한 처리 추가
if (memberId === null) {
    console.error("Member ID is invalid");
    // 처리 로직 추가 (예: 사용자에게 오류 메시지 표시 등)
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

    document.getElementById("editProfileForm").addEventListener("submit", function () {
        isFormDirty = false;
        window.removeEventListener("beforeunload", arguments.callee);
    });
});

// **회원 정보 수정 요청**
let isFetching = false; // 중복 요청 방지 변수

function updateMember(event) {
    event.preventDefault();
    if (isFetching) return;
    isFetching = true;

    // 폼 데이터 수집
    const formData = {
        password: document.getElementById("password").value,
        name: document.getElementById("name").value,
        nickName: document.getElementById("nickName").value,
        email: document.getElementById("email").value,
        phoneNumber: document.getElementById("phoneNumber").value,
        zipcode: document.getElementById("zipcode").value,
        address: document.getElementById("address").value,
        introduction: document.getElementById("introduction").value,
    };

    const careerYearInput = document.getElementById("careerYear");
    if (careerYearInput) {
        formData.careerYear = careerYearInput.value;
    }

    // PUT 요청으로 회원 정보 수정
    fetch(`/api/pets-care/members/${memberId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
        if (response.status === 204) return {}; // No Content 예외 처리
        if (!response.ok) throw new Error("회원 정보 수정 실패");
        return response.json();
    })
    .then(() => {
        alert("회원 정보가 수정되었습니다.");
        window.location.href = `/pets-care/members/${memberId}/myPage`;
    })
    .catch(error => {
        alert("오류 발생: " + error.message);
    })
    .finally(() => {
        isFetching = false; // 요청 완료 후 다시 활성화
    });

    return false;
}
