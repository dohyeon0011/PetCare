 document.getElementById("userReportForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const data = {
        reportedUserId: document.getElementById("reportedUserId").value,
        title: document.getElementById("reportTitle").value.trim(),
        content: document.getElementById("reportContent").value.trim()
    };

    fetch('/api/pets-care/reports/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]')?.value
        },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (res.ok) {
            alert("신고가 성공적으로 접수되었습니다.");
            location.href = '/pets-care/main';
        } else {
            return res.json().then(err => Promise.reject(err));
        }
    })
    .catch(err => {
        console.error(err);
        alert("신고 접수 중 오류가 발생했습니다.");
    });
});