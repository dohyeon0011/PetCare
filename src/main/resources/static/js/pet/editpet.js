// 반려견 삭제 기능
const deleteButtons = document.querySelectorAll('.delete-pet-btn');

deleteButtons.forEach(button => {
    button.addEventListener('click', event => {
        const petId = button.getAttribute('data-pet-id');
        const memberId = document.getElementById('memberData').getAttribute('data-member-id');

        if (confirm('정말로 삭제하시겠습니까?')) {
            httpRequest("DELETE", `/api/pets-care/members/${memberId}/pets/${petId}`, null, () => {
                alert("삭제가 완료되었습니다.");

                // DOM에서 해당 반려견 정보 삭제
                const petSection = button.closest('.pet-section');
                if (petSection) {
                    petSection.remove();
                }
                // 삭제 후 확인
                checkAndRedirectToMyPage();
            }, () => {
                alert("삭제에 실패했습니다.");
            });
        }
    });
});

// 반려견 수정
document.getElementById('editPetForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const memberId = document.getElementById('memberData').getAttribute('data-member-id');

    const petData = [];
    const petSections = document.querySelectorAll('.pet-section');

    petSections.forEach(section => {
        const petId = section.querySelector('input[name$=".id"]').value;
        const name = section.querySelector('input[name$=".name"]').value;
        const age = section.querySelector('input[name$=".age"]').value;
        const breed = section.querySelector('input[name$=".breed"]').value;
        const medicalConditions = section.querySelector('textarea[name$=".medicalConditions"]').value;

        petData.push({
            id: petId,
            name: name,
            age: age,
            breed: breed,
            medicalConditions: medicalConditions
        });
    });

    const body = JSON.stringify(petData);

    httpRequest("PUT", `/api/pets-care/members/${memberId}/pets`, body, () => {
        alert("반려견 정보가 성공적으로 수정되었습니다.");
        window.location.href = `/pets-care/members/${memberId}/myPage`;  // 마이페이지로 리다이렉트
    }, () => {
        alert("반려견 정보 수정에 실패했습니다.");
    });
});

// 반려견이 없으면 마이페이지로 리다이렉트하는 함수
function checkAndRedirectToMyPage() {
    const petSections = document.querySelectorAll('.pet-section');

    // 남아있는 반려견 정보가 없는 경우
    if (petSections.length === 0) {
        alert("모든 반려견 정보가 삭제되었습니다. 처음 화면으로 돌아갑니다.");
        const memberId = document.getElementById('memberData').getAttribute('data-member-id');
        window.location.href = `/pets-care/members/${memberId}/myPage`;  // 마이페이지로 리다이렉트
    }
}

// HTTP 요청 함수
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        } else {
            response.text().then(errorMessage => fail(errorMessage));
        }
    });
}
