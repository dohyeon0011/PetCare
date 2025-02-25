document.addEventListener("DOMContentLoaded", function () {
    const cancelButton = document.getElementById("cancelReservation");
    if (cancelButton) {
        cancelButton.addEventListener("click", function () {
            const reservationId = cancelButton.getAttribute("data-reservation-id");
            const customerId = document.getElementById("memberData").getAttribute("data-member-id");

            if (!customerId || !reservationId) {
                alert("예약 정보를 찾을 수 없습니다.");
                return;
            }

            if (!confirm("정말 예약을 취소하시겠습니까?")) {
                return;
            }

            fetch(`/api/pets-care/members/${customerId}/reservations/${reservationId}`, {
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
                window.location.href = `/pets-care/members/${customerId}/reservations?page=0&size=10`; // 예약 목록 페이지로 이동
            })
            .catch(error => {
                console.error("예약 취소 오류:", error);
                alert(error.message || "예약 취소 중 오류가 발생했습니다.");
            });
        });
    }
});
