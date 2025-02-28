document.addEventListener("DOMContentLoaded", function () {
    // 예약 취소 버튼 이벤트
    document.getElementById("cancelReservation")?.addEventListener("click", function () {
        if (confirm("정말로 예약을 취소하시겠습니까?")) {
            const reservationId = getReservationId();

            fetch(`/api/pets-care/admin/reservations/${reservationId}`, {
                method: "DELETE",
            })
                .then(response => {
                    // 상태 코드가 204 (No Content)일 경우 예약 취소 성공 처리
                    if (response.ok) {
                        alert("예약이 취소되었습니다.");
                        location.reload();
                    } else {
                        // 상태 코드가 200 이외일 경우 오류 처리
                        alert("예약 취소 중 오류가 발생했습니다.");
                    }
                })
                .catch(error => alert("예약 취소 중 오류가 발생했습니다."));
        }
    });

    // 돌봄 기록 삭제 이벤트
    document.querySelectorAll(".deleteCareLog").forEach(button => {
        button.addEventListener("click", function () {
            if (confirm("이 돌봄 기록을 삭제하시겠습니까?")) {
                const careLogId = this.dataset.id;

                fetch(`/api/pets-care/admin/care-logs/${careLogId}`, {
                    method: "DELETE",
                })
                    .then(response => response.json())
                    .then(data => {
                        alert("돌봄 기록이 삭제되었습니다.");
                        location.reload();
                    })
                    .catch(error => alert("돌봄 기록 삭제 중 오류가 발생했습니다."));
            }
        });
    });

    // 리뷰 삭제 이벤트
    document.getElementById("deleteReview")?.addEventListener("click", function () {
        if (confirm("이 이용 후기를 삭제하시겠습니까?")) {
            const reviewId = this.dataset.id;

            fetch(`/api/pets-care/admin/reviews/${reviewId}`, {
                method: "DELETE",
            })
                .then(response => response.json())
                .then(data => {
                    alert("이용 후기가 삭제되었습니다.");
                    location.reload();
                })
                .catch(error => alert("이용 후기 삭제 중 오류가 발생했습니다."));
        }
    });

    function getReservationId() {
        // URL 경로에서 reservationId 추출
        const pathParts = window.location.pathname.split('/');
        return pathParts[pathParts.length - 1];  // 경로의 마지막 부분이 reservationId
    }
});
