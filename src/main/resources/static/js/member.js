// HTML 요소에서 member-id 값을 가져오기
const sessionMemberId = document.getElementById("memberData").getAttribute("data-member-id");
const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;  // 'NaN' 체크는 자동으로 되며, 숫자 변환이 잘 되면 문제 없음

// memberId가 null이면 적절한 처리 추가
if (memberId === null) {
    console.error('Member ID is invalid');
    // 처리 로직 추가 (예: 사용자에게 오류 메시지 표시 등)
}

function updateMember(event) {
    event.preventDefault();

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
        if (!response.ok) {
            throw new Error("회원 정보 수정 실패");
        }
        return response.json();  // 응답 데이터 받기
    })
    .then(data => {
        alert("회원 정보가 수정되었습니다.");

        // 최신 데이터 요청 (수정된 회원 정보) 후 리다이렉트
        return fetch(`/api/pets-care/members/${memberId}/myPage`);
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("회원 정보를 다시 불러오는 데 실패했습니다.");
        }
        return response.json();  // 최신 데이터 받기
    })
    .then(updatedData => {
        // 최신 데이터로 화면 업데이트 (여기서는 리다이렉트 이전에 화면을 업데이트)
        document.getElementById("name").textContent = updatedData.name;
        document.getElementById("nickName").textContent = updatedData.nickName;
        document.getElementById("email").textContent = updatedData.email;
        document.getElementById("phoneNumber").textContent = updatedData.phoneNumber;
        document.getElementById("zipcode").textContent = updatedData.zipcode;
        document.getElementById("address").textContent = updatedData.address;
        document.getElementById("introduction").textContent = updatedData.introduction;

        // 'CUSTOMER' 역할인 경우에만 careerYear 요소 업데이트
        const memberRole = updatedData.role; // 서버에서 받은 데이터로 member.role 확인

        if (memberRole === 'CUSTOMER') {
            const careerYearElement = document.getElementById("careerYear");
            if (careerYearElement) {
                careerYearElement.textContent = updatedData.careerYear;
            } else {
                console.error('Career Year element not found');
            }
        }

        // 페이지 리다이렉트 (회원 정보 페이지로)
        window.location.href = `/pets-care/members/${memberId}/myPage`;
    })
    .catch(error => {
        alert("오류 발생: " + error.message);
    });

    return false;
}
