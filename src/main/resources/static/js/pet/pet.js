document.addEventListener("DOMContentLoaded", function () {
    const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
    const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

    if (!memberId) {
        alert("회원 정보를 찾을 수 없습니다.");
        return;
    }

    document.getElementById("addMorePets")?.addEventListener("click", function () {
        const petFormSections = document.getElementById("petFormSections");

        // 새로운 반려견 폼 추가
        const petCount = petFormSections.querySelectorAll(".pet-section").length; // 기존 반려견 개수
        const newPetForm = document.createElement("div");
        newPetForm.classList.add("pet-section");
        newPetForm.innerHTML = `
            <h3>반려견 ${petCount + 1} 정보</h3>
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
            <div class="form-group">
                <label for="profileImage${petCount}">프로필 이미지</label>
                <input type="file" name="pets[${petCount}].profileImage" class="form-control-file" accept="image/*">
            </div>
        `;

        petFormSections.appendChild(newPetForm);

        // 추가 후 번호 업데이트
        updatePetNumbers();
    });

    // 반려견 등록
    document.getElementById("addPetForm")?.addEventListener("submit", function (event) {
        event.preventDefault();
        submitPetData("POST", `/api/pets-care/members/${memberId}/pets/new`, "반려견이 등록되었습니다.");
    });

    // 반려견 수정
    document.getElementById("editPetForm")?.addEventListener("submit", function (event) {
        event.preventDefault();
        submitPetData("PUT", `/api/pets-care/members/${memberId}/pets`, "반려견 정보가 수정되었습니다.");
    });

    // 반려견 삭제
    document.querySelectorAll(".delete-pet-btn").forEach(button => {
        button.addEventListener("click", function () {
            const petId = button.getAttribute("data-pet-id");

            if (confirm("정말로 삭제하시겠습니까?")) {
                httpRequest("DELETE", `/api/pets-care/members/${memberId}/pets/${petId}`, null, () => {
                    alert("삭제가 완료되었습니다.");

                    // 삭제된 반려견 섹션 제거
                    button.closest(".pet-section")?.remove();

                    // 삭제 후 반려견 번호 다시 매기기
                    updatePetNumbers();

                    // 반려견이 하나도 없으면 마이페이지로 이동
                    checkAndRedirectToMyPage();
                }, () => {
                    alert("삭제에 실패했습니다.");
                });
            }
        });
    });

    // 반려견 번호 업데이트 함수
    function updatePetNumbers() {
        document.querySelectorAll(".pet-section").forEach((section, index) => {
            section.querySelector("h3").textContent = `반려견 ${index + 1} 정보`;

            // input name 속성도 업데이트 (서버 전송 시 필요)
            section.querySelectorAll("input, textarea").forEach(input => {
                const nameAttr = input.getAttribute("name");
                if (nameAttr) {
                    input.setAttribute("name", nameAttr.replace(/\[\d+\]/, `[${index}]`));
                }
            });
        });
    }

    function submitPetData(method, url, successMessage) {
        const formData = new FormData();

        // 반려견 정보 추가
        const petData = [];
        document.querySelectorAll(".pet-section").forEach((section, index) => {
            // 반려견 ID 추가 (기존 반려견 수정 시 필요)
            const petIdInput = section.querySelector('input[name$=".id"]');

            if (petIdInput) {
                formData.append(`requests[${index}].id`, petIdInput.value);
            }

            const petInfo = {
                name: section.querySelector('input[name$=".name"]').value,
                age: section.querySelector('input[name$=".age"]').value,
                breed: section.querySelector('input[name$=".breed"]').value,
                medicalConditions: section.querySelector('textarea[name$=".medicalConditions"]').value
            };
            petData.push(petInfo);

            // 반려견 사진 추가
            const fileInput = section.querySelector('input[type="file"]');
            if (fileInput && fileInput.files.length > 0) {
                Array.from(fileInput.files).forEach(file => {
                    formData.append(`requests[${index}].profileImage`, file);   // 파일 순서를 맞추어 전송
                });
            }
        });

        // 반려견 정보를 JSON 형태로 변환하여 FormData에 추가
        petData.forEach((pet, index) => {
            formData.append(`requests[${index}].name`, pet.name);
            formData.append(`requests[${index}].age`, pet.age);
            formData.append(`requests[${index}].breed`, pet.breed);
            formData.append(`requests[${index}].medicalConditions`, pet.medicalConditions);
        });

        // 콘솔 로그로 FormData 내용을 출력
        formData.forEach((value, key) => {
            console.log(key, value);
        });

        // 요청 보내기
        httpRequest(method, url, formData, () => {
            alert(successMessage);
            window.location.href = `/pets-care/members/${memberId}/myPage`;
        }, () => {
            alert("작업에 실패했습니다.");
        });
    }

    // 반려견이 없으면 마이페이지로 이동
    function checkAndRedirectToMyPage() {
        if (document.querySelectorAll(".pet-section").length === 0) {
            alert("모든 반려견 정보가 삭제되었습니다. 처음 화면으로 돌아갑니다.");
            window.location.href = `/pets-care/members/${memberId}/myPage`;
        }
    }

    // HTTP 요청 함수 수정 (FormData 지원)
    function httpRequest(method, url, body, success, fail) {
        fetch(url, {
            method: method,
            body: body, // FormData 전송
        }).then(response => {
            if (response.ok) {
                success();
            } else {
                response.text().then(errorMessage => fail(errorMessage));
            }
        });
    }
});
