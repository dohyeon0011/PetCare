document.addEventListener("DOMContentLoaded", function () {
    // memberId 가져오기
    const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
    const sitterId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

    // sitterScheduleId 가져오기
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
    const editButtons = document.querySelectorAll(".care-log-card .btn-warning");
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const logIdStr = button.closest('.care-log-card').getAttribute("data-log-id");
            console.log('logIdStr:', logIdStr);  // logId 확인
            const logId = Number(logIdStr);  // 값을 숫자로 변환
            console.log('logId:', logId);  // 변환된 logId 확인
            if (isNaN(logId)) {
                alert("잘못된 돌봄 기록 ID입니다.");
                return;
            }
            const careType = button.closest('.care-log-card').querySelector("p:nth-child(2) span").textContent;
            const description = button.closest('.care-log-card').querySelector("p:nth-child(3) span").textContent;
            openCareLogModal("edit", logId, careType, description);
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
    function openCareLogModal(action, logId = "", careType = "", description = "", imgPath = "") {
        document.getElementById("careLogModal").style.display = "flex";
        if (action === "edit") {
            document.getElementById("modalTitle").textContent = "돌봄 기록 수정";
            document.getElementById("logId").value = logId;
            document.getElementById("careType").value = careType;
            document.getElementById("description").value = description;
            if (imgPath) {
                document.getElementById("imgPath").value = imgPath;
            }
        } else {
            document.getElementById("modalTitle").textContent = "돌봄 기록 추가";
            document.getElementById("careLogForm").reset();  // 폼 초기화
        }
    }

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

        if (!careType || !description) {
            alert("모든 필드를 채워주세요.");
            return;
        }

        const logIdNum = Number(logId);  // 값을 숫자로 변환
        if (isNaN(logIdNum)) {
            alert("잘못된 돌봄 기록 ID입니다.");
            return;
        }

        // 기존 오류 메시지 제거
        const errorContainer = document.getElementById("errorContainer");
        errorContainer.innerHTML = "";

        // URL 수정 (sitterId와 sitterScheduleId 반영)
        const url = logIdNum
            ? `/api/pets-care/members/${sitterId}/care-logs/${logIdNum}`
            : `/api/pets-care/members/${sitterId}/schedules/${sitterScheduleId}/care-logs/new`;

        const method = logIdNum ? "PUT" : "POST";

        const requestBody = {
            careType: careType,
            description: description,
        };

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody),
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
            location.reload(); // 페이지 새로고침 (추가/수정 후)
        })
        .catch(error => {
            alert(error.message);  // 서버에서 받은 오류 메시지 표시
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
