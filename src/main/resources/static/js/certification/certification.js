document.addEventListener("DOMContentLoaded", function () {
    const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
    const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

    if (!memberId) {
        alert("회원 정보를 찾을 수 없습니다.");
        return;
    }

    // 자격증 추가 버튼 클릭
    document.getElementById("addMoreCertifications")?.addEventListener("click", function () {
        const certificationFormSections = document.getElementById("certificationFormSections");

        // 새로운 자격증 폼 추가
        const certificationCount = certificationFormSections.querySelectorAll(".certification-section").length; // 기존 자격증 개수
        const newCertificationForm = document.createElement("div");
        newCertificationForm.classList.add("certification-section");
        newCertificationForm.innerHTML = `
            <h3>자격증 ${certificationCount + 1} 정보</h3>
            <div class="form-group">
                <label for="name">자격증 이름</label>
                <input type="text" name="certifications[${certificationCount}].name" class="form-control" required placeholder="자격증 이름을 입력하세요.">
            </div>
        `;

        certificationFormSections.appendChild(newCertificationForm);

        // 추가 후 번호 업데이트
        updateCertificationNumbers();
    });

    // 자격증 등록 폼 제출
    document.getElementById("addCertificationForm")?.addEventListener("submit", function (event) {
        event.preventDefault();
        submitCertificationData("POST", `/api/pets-care/members/${memberId}/certifications/new`, "자격증이 등록되었습니다.");
    });

    // 자격증 수정 폼 제출
    document.getElementById("editCertificationForm")?.addEventListener("submit", function (event) {
        event.preventDefault();
        submitCertificationData("PUT", `/api/pets-care/members/${memberId}/certifications`, "자격증 정보가 수정되었습니다.");
    });

    // 자격증 삭제 버튼 클릭
    document.querySelectorAll(".delete-certification-btn").forEach(button => {
        button.addEventListener("click", function () {
            const certificationId = button.getAttribute("data-certification-id");

            if (confirm("정말로 삭제하시겠습니까?")) {
                httpRequest("DELETE", `/api/pets-care/members/${memberId}/certifications/${certificationId}`, null, () => {
                    alert("삭제가 완료되었습니다.");

                    // 삭제된 자격증 섹션 제거
                    button.closest(".certification-section")?.remove();

                    // 삭제 후 자격증 번호 다시 매기기
                    updateCertificationNumbers();

                    // 자격증이 하나도 없으면 마이페이지로 이동
                    checkAndRedirectToMyPage();
                }, () => {
                    alert("삭제에 실패했습니다.");
                });
            }
        });
    });

    // 자격증 번호 업데이트 함수
    function updateCertificationNumbers() {
        document.querySelectorAll(".certification-section").forEach((section, index) => {
            section.querySelector("h3").textContent = `자격증 ${index + 1} 정보`;

            // input name 속성도 업데이트 (서버 전송 시 필요)
            section.querySelectorAll("input, textarea").forEach(input => {
                const nameAttr = input.getAttribute("name");
                if (nameAttr) {
                    input.setAttribute("name", nameAttr.replace(/\[\d+\]/, `[${index}]`));
                }
            });
        });
    }

    // 자격증 정보 제출 함수
    function submitCertificationData(method, url, successMessage) {
        const certificationData = [];
        document.querySelectorAll(".certification-section").forEach((section) => {
            const certificationId = section.querySelector('input[name$=".id"]')?.value || null;
            const name = section.querySelector('input[name$=".name"]').value;
            certificationData.push({ id: certificationId, name: name });
        });

        httpRequest(method, url, JSON.stringify(certificationData), () => {
            alert(successMessage);
            window.location.href = `/pets-care/members/${memberId}/myPage`;
        }, () => {
            alert("작업에 실패했습니다.");
        });
    }

    // 자격증이 없으면 마이페이지로 이동
    function checkAndRedirectToMyPage() {
        if (document.querySelectorAll(".certification-section").length === 0) {
            alert("모든 자격증 정보가 삭제되었습니다. 처음 화면으로 돌아갑니다.");
            window.location.href = `/pets-care/members/${memberId}/myPage`;
        }
    }

    // HTTP 요청 함수
    function httpRequest(method, url, body, success, fail) {
        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: body,
        }).then(response => {
            if (response.ok) {
                success();
            } else {
                response.json().then(errors => {
                    // 필드 에러를 화면에 표시
                    displayFieldErrors(errors);
                    fail("입력 값을 확인하세요.");  // 작업 실패 메시지는 그대로
                }).catch(err => {
                    fail("서버 응답을 처리하는 중 오류가 발생했습니다.");
                });
            }
        }).catch(err => {
            fail("네트워크 오류가 발생했습니다.");
        });
    }

    // 필드 오류 메시지를 동적으로 표시하는 함수
    function displayFieldErrors(errors) {
        // 기존 오류 메시지 제거
        document.querySelectorAll(".error-message").forEach(el => el.remove());
        document.querySelectorAll(".error").forEach(el => el.classList.remove("error"));

        // 각 필드의 오류 메시지 처리
        Object.keys(errors).forEach(field => {
            const inputField = document.querySelector(`[name="${field}"]`);
            if (inputField) {
                inputField.classList.add("error"); // 입력 필드 테두리 강조

                const errorMessage = document.createElement("div");
                errorMessage.classList.add("error-message");
                errorMessage.textContent = errors[field];

                inputField.insertAdjacentElement("afterend", errorMessage); // 필드 아래 오류 메시지 추가
            }
        });
    }
});
