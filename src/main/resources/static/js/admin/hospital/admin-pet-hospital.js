document.getElementById('csvUploadForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);

    fetch('/api/pets-care/admin/pet-hospital', {
        method: 'POST',
        body: formData
    })
    .then(async response => {
        const text = await response.text();
        alert(text); // 응답 메시지 alert
        window.location.href = '/pets-care/main'; // 메인 페이지로 리다이렉트
    })
    .catch(error => {
        alert('오류 발생: ' + error);
        console.error('에러:', error);
    });
});