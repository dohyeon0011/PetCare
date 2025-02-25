document.addEventListener("DOMContentLoaded", function () {
    const cancelButton = document.getElementById("cancelReservation");
    if (cancelButton) {
        cancelButton.addEventListener("click", function () {
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

            if (!sitterId || !sitterScheduleId) {
                alert("예약 정보를 찾을 수 없습니다.");
                return;
            }

            if (!confirm("정말 예약을 취소하시겠습니까?")) {
                return;
            }

            fetch(`/api/pets-care/members/${sitterId}/schedules/${sitterScheduleId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }
            })
            .then(response => response.json().catch(() => null)) // JSON 파싱 시도, 실패하면 null
            .then(data => {
                if (data?.error) {
                    throw new Error(data.error);
                }
                alert("예약이 취소되었습니다.");
                window.location.href = `/pets-care/members/${sitterId}/schedules?page=0&size=10`; // 예약 목록 페이지로 이동
            })
            .catch(error => {
                console.error("예약 취소 오류:", error);
                alert(error.message || "예약 취소 중 오류가 발생했습니다.");
            });
        });
    }
});
