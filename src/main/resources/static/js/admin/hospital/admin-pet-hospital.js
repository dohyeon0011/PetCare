const fileInput = document.getElementById('csvFile');
const fileInputContainer = document.getElementById('fileInputContainer');
const fileSelected = document.getElementById('fileSelected');
const fileName = document.getElementById('fileName');
const fileSize = document.getElementById('fileSize');
const submitBtn = document.getElementById('submitBtn');
const progressBar = document.getElementById('progressBar');
const progressFill = document.getElementById('progressFill');

// 파일 선택 처리
fileInput.addEventListener('change', handleFileSelect);

// 드래그 앤 드롭 처리
fileInputContainer.addEventListener('dragover', (e) => {
    e.preventDefault();
    fileInputContainer.classList.add('dragover');
});

fileInputContainer.addEventListener('dragleave', () => {
    fileInputContainer.classList.remove('dragover');
});

fileInputContainer.addEventListener('drop', (e) => {
    e.preventDefault();
    fileInputContainer.classList.remove('dragover');
    const files = e.dataTransfer.files;
    if (files.length > 0) {
        fileInput.files = files;
        handleFileSelect();
    }
});

function handleFileSelect() {
    const file = fileInput.files[0];
    if (file) {
        fileName.textContent = file.name;
        fileSize.textContent = formatFileSize(file.size);
        fileSelected.style.display = 'flex';
        submitBtn.disabled = false;
    }
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

document.getElementById('csvUploadForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!fileInput.files[0]) {
        alert('파일을 선택해주세요.');
        return;
    }
    // 업로드 시작
    submitBtn.disabled = true;
    submitBtn.textContent = '업로드 중...';
    progressBar.style.display = 'block';

    const form = e.target;
    const formData = new FormData(form);
    try {
        // 진행률 애니메이션 시작
        let progress = 0;
        const progressInterval = setInterval(() => {
            if (progress < 90) {
                progress += Math.random() * 10;
                progressFill.style.width = Math.min(progress, 90) + '%';
            }
        }, 100);

        const response = await fetch('/api/pets-care/admin/pet-hospitals', {
            method: 'POST',
            body: formData
        });
        // 진행률 완료
        clearInterval(progressInterval);
        progressFill.style.width = '100%';

        const text = await response.text();
        setTimeout(() => {
            alert(text); // 응답 메시지 alert
            window.location.href = '/pets-care/admin/pet-hospitals';
        }, 300);

    } catch (error) {
        progressFill.style.width = '0%';
        progressBar.style.display = 'none';
        submitBtn.disabled = false;
        submitBtn.textContent = '업로드 시작';

        alert('오류 발생: ' + error.message);
        console.error('에러:', error);
    }
});

function resetForm() {
    fileInput.value = '';
    fileSelected.style.display = 'none';
    submitBtn.disabled = true;
    submitBtn.textContent = '업로드 시작';
    progressBar.style.display = 'none';
    progressFill.style.width = '0%';
}