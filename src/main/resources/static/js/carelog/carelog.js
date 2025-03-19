document.addEventListener("DOMContentLoaded", function () {
    const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
    const sitterId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

    const scheduleDetailElement = document.getElementById("scheduleDetail");
    const sitterScheduleId = scheduleDetailElement ? parseInt(scheduleDetailElement.getAttribute("data-sitter-schedule-id"), 10) : null;

    if (isNaN(sitterScheduleId)) {
        alert("돌봄 일정 ID가 올바르지 않습니다.");
        return;
    }

    // 돌봄 기록 추가 버튼 클릭 시 팝업 열기
    const addCareLogButton = document.querySelector(".button-group .btn-primary");
    if (addCareLogButton) {
        addCareLogButton.addEventListener("click", function () {
            openCareLogModal("add");
        });
    }

    // 돌봄 기록 수정 버튼 클릭 시 팝업 열기
    const editButtons = document.querySelectorAll(".edit-care-log-btn");
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const careLogCard = button.closest('.care-log-card');

            if (!careLogCard) {
                console.error("care-log-card 요소를 찾을 수 없습니다.");
                return;
            }

            const logIdStr = careLogCard.getAttribute("data-log-id");
            const logId = Number(logIdStr);
            if (isNaN(logId)) {
                alert("잘못된 돌봄 기록 ID입니다.");
                return;
            }

            const careTypeElem = careLogCard.querySelector("p:nth-of-type(1) span");
            const descriptionElem = careLogCard.querySelector("p:nth-of-type(2) span");

            const careType = careTypeElem.textContent.trim();
            const description = descriptionElem.textContent.trim();

            // 이미지 URL 찾기
            const imageElem = careLogCard.querySelector("image"); // 이미지 요소가 존재한다고 가정
            const image = imageElem ? imageElem.src : ""; // 이미지가 없으면 빈 문자열로 설정

            // openCareLogModal 함수 호출, image URL도 전달
            openCareLogModal("edit", logId, careType, description, image);
        });
    });

    // 돌봄 기록 삭제 버튼 클릭 시 삭제 처리
    const deleteButtons = document.querySelectorAll(".care-log-card .btn-danger");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const logIdStr = button.closest('.care-log-card').getAttribute("data-log-id");
            console.log('logIdStr (delete):', logIdStr);  // 삭제 시 logId 확인
            const logId = Number(logIdStr);  // 값을 숫자로 변환
            console.log('logId (delete):', logId);  // 변환된 logId 확인
            if (isNaN(logId)) {
                alert("잘못된 돌봄 기록 ID입니다.");
                return;
            }
            if (confirm("정말 삭제하시겠습니까?")) {
                deleteCareLog(logId);
            }
        });
    });

    // 팝업 닫기 버튼
    const closeModalButton = document.getElementById("closeModal");
    if (closeModalButton) {
        closeModalButton.addEventListener("click", function () {
            closeCareLogModal();
        });
    }

    // 돌봄 기록 추가 또는 수정 팝업 열기
    function openCareLogModal(action, logId = "", careType = "", description = "", image = "") {
        document.getElementById("careLogModal").style.display = "flex";
        if (action === "edit") {
            document.getElementById("modalTitle").textContent = "돌봄 기록 수정";
            document.getElementById("logId").value = logId;
            document.getElementById("careType").value = careType;
            document.getElementById("description").value = description;
            // 이미지 표시 (만약 이미지 URL이 있다면)
            if (image) {
                const imageElement = document.getElementById("image");
                imageElement.value = ""; // 기존 입력값 초기화
                const imagePreview = document.getElementById("imagePreview");
                imagePreview.src = imageUrl; // 이미지 미리보기 URL 설정
                imagePreview.style.display = "block"; // 미리보기 이미지 표시
            }
        } else {
            document.getElementById("modalTitle").textContent = "돌봄 기록 추가";
            document.getElementById("careLogForm").reset();  // 폼 초기화
            document.getElementById("imagePreview").style.display = "none"; // 이미지 미리보기 숨기기
        }
    }

    // 이미지 미리보기 업데이트 (파일 업로드 시)
    document.getElementById("image").addEventListener("change", function (event) {
        const file = event.target.files[0];
        const imagePreview = document.getElementById("imagePreview");
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                imagePreview.src = e.target.result;
                imagePreview.style.display = "block"; // 이미지 미리보기 표시
            };
            reader.readAsDataURL(file);
        }
    });

    // 팝업 닫기
    function closeCareLogModal() {
        document.getElementById("careLogModal").style.display = "none";
    }

    // 돌봄 기록 추가 또는 수정
    document.getElementById("careLogForm")?.addEventListener("submit", function (e) {
        e.preventDefault();

        const logId = document.getElementById("logId").value;
        const careType = document.getElementById("careType").value;
        const description = document.getElementById("description").value;
        const careLogImage = document.getElementById("image").files[0];  // 이미지 파일

        if (!careType || !description) {
            alert("모든 필드를 채워주세요.");
            return;
        }

        const logIdNum = Number(logId);
        if (isNaN(logIdNum)) {
            alert("잘못된 돌봄 기록 ID입니다.");
            return;
        }

        const formData = new FormData();
        formData.append("request", new Blob([JSON.stringify({
            careType: careType,
            description: description
        })], { type: "application/json" }));

        if (careLogImage) {
            formData.append("careLogImage", careLogImage);
        }

        const url = logIdNum
            ? `/api/pets-care/members/${sitterId}/care-logs/${logIdNum}`
            : `/api/pets-care/members/${sitterId}/schedules/${sitterScheduleId}/care-logs/new`;

        const method = logIdNum ? "PUT" : "POST";

        fetch(url, {
            method: method,
            body: formData,
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || "알 수 없는 오류가 발생했습니다.");
                });
            }
            return response.json();
        })
        .then(data => {
            alert(data.message || "돌봄 기록이 저장되었습니다.");
            closeCareLogModal();
            location.reload();
        })
        .catch(error => {
            alert(error.message);
            console.error("오류 발생:", error);
        });
    });

    // 돌봄 기록 삭제
    function deleteCareLog(logId) {
        fetch(`/api/pets-care/members/${sitterId}/care-logs/${logId}`, {
            method: "DELETE"
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("서버 응답 오류: " + response.status);
            }
            return response.text(); // 서버 상태 코드 응답이 204인 경우를 대비
        })
        .then(() => {
            alert("돌봄 기록이 삭제되었습니다.");
            location.reload();
        })
        .catch(error => {
            alert("돌봄 기록 삭제에 실패했습니다. (" + error.message + ")");
            console.error(error);
        });
    }
});
