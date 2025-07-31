document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("replyForm");

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const reportId = document.getElementById("reportId").value;
        const replyContent = document.getElementById("replyContent").value.trim();
        const csrfToken = document.querySelector('input[name="_csrf"]')?.value;

        if (!replyContent) {
            alert("답변 내용을 입력해주세요.");
            return;
        }

        fetch(`/api/pets-care/admin/reports/${reportId}/users`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            body: JSON.stringify({ replyContent })
        })
        .then(response => {
            if (!response.ok) throw new Error("답변 등록 실패");
            alert("답변이 성공적으로 등록되었습니다.");
            window.location.href = "/pets-care/admin/reports/users";
        })
        .catch(error => {
            console.error("에러 발생:", error);
            alert("답변 등록 중 오류가 발생했습니다.");
        });
    });
});
