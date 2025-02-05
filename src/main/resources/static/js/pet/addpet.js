// 반려견 추가 버튼 클릭 시
document.getElementById('addMorePets').addEventListener('click', function() {
    const petFormSections = document.getElementById('petFormSections');
    const petCount = petFormSections.querySelectorAll('.pet-section').length;

    const newPetForm = `
        <div class="pet-section">
            <h3>반려견 ${petCount + 1} 정보</h3> <!-- 동적으로 반려견 번호 추가 -->
            <div class="form-group">
                <label for="name">이름</label>
                <input type="text" name="pets[${petCount}].name" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="age">나이</label>
                <input type="number" name="pets[${petCount}].age" class="form-control" min="0" required>
            </div>
            <div class="form-group">
                <label for="breed">품종</label>
                <input type="text" name="pets[${petCount}].breed" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="medicalConditions">건강 상태 및 특이사항</label>
                <textarea name="pets[${petCount}].medicalConditions" class="form-control" rows="2"></textarea>
            </div>
        </div>
    `;
    petFormSections.innerHTML += newPetForm;
});

// 반려견 등록 요청 처리
document.addEventListener('DOMContentLoaded', function() {
    const addPetForm = document.getElementById('addPetForm');
    if (addPetForm) {
        addPetForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const memberId = document.getElementById('memberData').getAttribute('data-member-id');
            const petData = [];
            const petSections = document.querySelectorAll('.pet-section');

            petSections.forEach(section => {
                const name = section.querySelector('input[name$=".name"]').value;
                const age = section.querySelector('input[name$=".age"]').value;
                const breed = section.querySelector('input[name$=".breed"]').value;
                const medicalConditions = section.querySelector('textarea[name$=".medicalConditions"]').value;

                petData.push({
                    name: name,
                    age: age,
                    breed: breed,
                    medicalConditions: medicalConditions
                });
            });

            const body = JSON.stringify(petData);

            // 반려견 등록 요청
            httpRequest("POST", `/api/pets-care/members/${memberId}/pets/new`, body, () => {
                alert("반려견이 등록되었습니다.");
                window.location.href = `/pets-care/members/${memberId}/myPage`;  // 마이페이지로 리다이렉트
            }, () => {
                alert("반려견 등록에 실패했습니다.");
            });
        });
    }
});

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