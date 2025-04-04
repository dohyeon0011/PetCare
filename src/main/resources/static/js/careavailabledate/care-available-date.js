// HTML 요소에서 member-id 값을 가져오기
const sessionMemberId = document.getElementById("memberData")?.getAttribute("data-member-id");
const memberId = sessionMemberId && !isNaN(Number(sessionMemberId)) ? Number(sessionMemberId) : null;

if (memberId === null) {
    console.error("회원 정보를 찾을 수 없습니다.");
}

// 가격 입력 필드에서 천 단위마다 쉼표 추가
const priceInput = document.getElementById("price");

if (priceInput) {
    priceInput.addEventListener("input", function () {
        let rawValue = this.value.replace(/,/g, ""); // 기존 쉼표 제거
        if (!isNaN(rawValue) && rawValue !== "") {
            this.value = formatNumberWithCommas(rawValue);
        }
    });

    priceInput.addEventListener("focus", function () {
        this.value = this.value.replace(/,/g, ""); // 포커스를 얻으면 순수 숫자로 변경
    });

    priceInput.addEventListener("blur", function () {
        this.value = formatNumberWithCommas(this.value); // 포커스를 잃으면 다시 쉼표 추가
    });
}

// 숫자를 천 단위마다 쉼표로 포맷하는 함수
function formatNumberWithCommas(value) {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 돌봄 가능 날짜 등록 폼 제출 이벤트
document.getElementById("addCareAvailableDateForm")?.addEventListener("submit", function (event) {
    event.preventDefault();
    resetErrorMessages(); // 폼 제출 시 기존의 오류 메시지 숨기기

    const availableAt = document.getElementById("availableAt").value;
    const price = document.getElementById("price").value.replace(/,/g, ""); // 쉼표 제거 후 숫자로 변환

    // 요청 데이터
    const requestData = {
        availableAt: availableAt,
        price: price,
    };

    axios.post(`/api/pets-care/members/${memberId}/care-available-dates/new`, requestData)
        .then(response => {
            alert("돌봄 가능 날짜가 등록되었습니다.");
            window.location.href = `/pets-care/members/${memberId}/care-available-dates?page=0&size=9`;
        })
        .catch(error => {
            console.error("등록 실패:", error);

            if (error.response && error.response.data) {
                const errorData = error.response.data;

                // 필드별 검증 오류 메시지가 있을 경우
                if (typeof errorData === 'object' && !errorData.error) {
                    console.log("필드별 검증 오류 데이터:", errorData);  // 서버 응답 확인용 로그

                    // 각 필드에 대한 오류 메시지 처리
                    for (let field in errorData) {
                        if (errorData.hasOwnProperty(field)) {
                            const errorMessage = errorData[field];

                            // showError() 함수를 이용해 오류 메시지 표시
                            showError(errorMessage, `${field}-error`);
                        }
                    }
                } else if (errorData.error) {
                    // "error" 필드를 통해 일반적인 오류 메시지 처리
                    alert(Object.values(errorData)[0]);  // error 메시지에서 "error" 필드 제외하고, 메시지만 표시
                }
            } else {
                alert("알 수 없는 오류가 발생했습니다.");
            }
        });
});

// 돌봄 가능 날짜 수정 처리
function editCareAvailableDate() {
    const careAvailableDateId = document.getElementById('careAvailableDateId').value;
    const availableAt = document.getElementById('availableAt').value;
    const price = document.getElementById('price').value.replace(/,/g, ""); // 쉼표 제거 후 숫자로 변환
    const status = document.getElementById('status').value;

    // 오류 메시지 초기화
    resetErrorMessages();

    const requestData = {
        availableAt: availableAt,
        price: price,
        status: status,
    };

    axios.put(`/api/pets-care/members/${memberId}/care-available-dates/${careAvailableDateId}`, requestData)
        .then(response => {
            alert("돌봄 가능 날짜가 수정되었습니다.");
            window.location.href = `/pets-care/members/${memberId}/care-available-dates/${careAvailableDateId}`;
        })
        .catch(error => {
            console.error("수정 실패:", error);

            if (error.response && error.response.data) {
                const errorData = error.response.data;

                // DTO 검증 오류 (필드별 에러) 처리
                if (typeof errorData === 'object' && !errorData.error) {
                    console.log("필드별 검증 오류 데이터:", errorData);  // 서버 응답 확인용 로그

                    Object.keys(errorData).forEach(field => {
                        showError(errorData[field], field + "-error"); // 필드별 에러 메시지 표시
                    });

                    return;  // 여기서 종료 → alert() 표시 안 함
                }

                // 일반적인 서비스 로직 오류 처리 (error 필드 처리)
                if (errorData.error) {
                    alert(errorData.error);  // 단일 오류 메시지만 alert로 표시
                } else {
                    alert("알 수 없는 오류가 발생했습니다.");
                }
            } else {
                alert("알 수 없는 오류가 발생했습니다.");
            }
        });
}

// 돌봄 가능 날짜 삭제
function deleteCareAvailableDate(dateId) {
    if (!confirm("정말로 이 돌봄 가능 날짜를 삭제하시겠습니까?")) {
        return;
    }

    // 날짜 삭제 요청
    axios.delete(`/api/pets-care/members/${memberId}/care-available-dates/${dateId}`)
        .then(response => {
            alert("돌봄 가능 날짜가 삭제되었습니다.");
            // 삭제 후, 서버에서 최신 데이터를 받아옴
            axios.get(`/api/pets-care/members/${memberId}/care-available-dates?page=0&size=9`)
                .then(response => {
                    const careAvailableDates = response.data;  // 반환된 날짜 리스트
                    if (careAvailableDates.length === 0) {
                        // 날짜가 없을 경우 안내 메시지 표시
                        document.getElementById("noDatesMessage").style.display = 'block';
                    } else {
                        // 날짜가 있으면 해당 UI 업데이트
                        document.getElementById("noDatesMessage").style.display = 'none';
                    }
                })
                .catch(error => {
                    console.error("데이터 로드 실패:", error);
                });

            // 리다이렉트 후, 서버에서 최신 데이터를 반영하여 UI를 업데이트
            window.location.href = `/pets-care/members/${memberId}/care-available-dates?page=0&size=9`;
        })
        .catch(error => {
            console.error("삭제 실패:", error);
            if (error.response && error.response.data) {
                showError(error.response.data);  // 서버에서 반환된 오류 메시지 표시
            } else {
                showError("돌봄 가능 날짜 삭제에 실패했습니다.");
            }
        });
}

// 필드별 에러 메시지 표시
function showError(message, fieldId) {
    const errorElement = document.getElementById(fieldId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';  // 오류 메시지 표시
    } else {
        console.warn(`오류 메시지를 표시할 요소를 찾을 수 없음: ${fieldId}`);
    }
}

// 모든 에러 메시지 숨기기
function resetErrorMessages() {
    const errorFields = document.querySelectorAll(".error-message");
    errorFields.forEach(field => {
        field.style.display = 'none';
        field.textContent = '';  // 기존 메시지도 초기화
    });
}

// 서버 오류에서 구체적인 메시지 추출하기
function extractErrorMessage(error) {
    // 서버에서 반환한 에러 메시지가 객체인 경우, 그 안에서 message를 찾아 반환r
    if (error.response && error.response.data) {
        const errorData = error.response.data;
        if (typeof errorData === 'object' && errorData.message) {
            return errorData.message;  // message 속성이 있으면 그 값을 반환
        } else {
            return JSON.stringify(errorData);  // 객체로 반환된 경우 전체를 문자열로 반환
        }
    } else {
        return "알 수 없는 오류가 발생했습니다.";  // 서버 응답이 없을 경우
    }
}
