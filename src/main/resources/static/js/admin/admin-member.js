document.addEventListener("DOMContentLoaded", function () {
    const deleteButton = document.querySelector("#deleteMemberBtn");

    if (deleteButton) {
        deleteButton.addEventListener("click", function () {
            const memberId = this.getAttribute("data-member-id");

            if (!memberId) {
                alert("회원 ID를 찾을 수 없습니다.");
                return;
            }

            if (!confirm("정말로 이 회원을 탈퇴 처리하시겠습니까?")) {
                return;
            }

            fetch(`/api/pets-care/admin/members/${memberId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }
            })
            .then(response => {
                if (response.ok) {
                    alert("회원이 탈퇴 처리되었습니다.");
                    window.location.href = "/pets-care/admin/members"; // 회원 목록 페이지로 이동
                } else {
                    response.text().then(text => alert("오류 발생: " + text));
                }
            })
            .catch(error => {
                console.error("에러:", error);
                alert("회원 탈퇴 요청 중 오류가 발생했습니다.");
            });
        });
    }

    // 뒤로가기 버튼
    const backBtn = document.querySelector("#backBtn");
    if (backBtn) {
        backBtn.addEventListener("click", function () {
            window.history.back();
        });
    }
});
