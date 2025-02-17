document.addEventListener("DOMContentLoaded", function () {
    const reservationForm = document.getElementById("reservationForm");
    const submitButton = document.getElementById("submitReservation");
    const careAvailableId = document.getElementById("careAvailableId");
    const priceDisplay = document.getElementById("priceDisplay");
    const amountText = document.getElementById("amountText");  // amountText 요소를 찾음
    const amountHint = document.getElementById("amountHint");

    // 서버에서 렌더링된 보유 적립금 값을 가져옴
    const availablePoints = amountText.textContent.replace('₩', '');  // ₩를 제거하고 숫자만 가져오기
    amountHint.textContent = `현재 보유 적립금: ₩${availablePoints}`;  // amountHint에 보유 적립금 표시

    let selectedPrice = careAvailableId.selectedOptions[0].textContent.split(" - ")[1].replace("원", "");

    // 선택한 날짜 가격 업데이트
    careAvailableId.addEventListener("change", function () {
        selectedPrice = careAvailableId.selectedOptions[0].textContent.split(" - ")[1].replace("원", "");
        priceDisplay.textContent = `${selectedPrice} 원`;  // 가격 표시
        updateFinalPrice();  // 최종 결제 금액 계산
    });

    // 적립금 사용 시 최종 결제 금액 계산
    const pointsToUseInput = document.getElementById("pointsToUse");
    pointsToUseInput.addEventListener("input", updateFinalPrice);

    function updateFinalPrice() {
        const pointsToUse = parseInt(pointsToUseInput.value) || 0;
        const finalPrice = Math.max(selectedPrice - pointsToUse, 0);  // 최종 결제 금액
        document.getElementById("finalPrice").textContent = `₩${finalPrice}`;
    }

    reservationForm.addEventListener("submit", function (event) {
        event.preventDefault();
        clearErrorMessages();
        disableButton(true);

        const pointsToUse = parseInt(document.getElementById("pointsToUse").value) || 0;  // 사용자가 입력한 적립금
        const selectedPrice = parseInt(priceDisplay.textContent.replace("₩", "").replace(" 원", "")); // 최종 가격

        const requestData = {
            customerId: document.getElementById("customerId").value,
            sitterId: document.getElementById("sitterId").value,
            careAvailableId: document.getElementById("careAvailableId").value,
            price: selectedPrice,
            petIds: Array.from(document.querySelectorAll('input[name="petIds"]:checked'))
                .map(pet => pet.value),
            requests: document.getElementById("requests").value,
            amount: pointsToUse
        };

        fetch("/api/pets-care/reservations/new", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        })
        .then(response => response.json().then(data => {
            if (!response.ok) {
                throw data;  // 서버에서 받은 에러 데이터를 throw해서 catch로 전달
            }
            return data;  // 정상적으로 응답받으면 데이터를 반환
        }))
        .then(data => {
            if (data && data.customerId) {
                alert("예약이 완료되었습니다!");
                window.location.href = `/pets-care/members/${requestData.customerId}/reservations?page=0&size=10`;
            } else {
                // 정상 응답이라면 필드 에러 메시지를 화면에 표시
                displayErrorMessages(data);
                disableButton(false);
            }
        })
        .catch(error => {
            console.error("Error:", error);
            if (error && error.error) {
                alert(error.error);  // 서버에서 보낸 "error" 메시지를 alert()으로 표시
            } else if (error && typeof error === "object") {
                // 필드별 오류 처리
                displayErrorMessages(error);  // 필드 에러를 표시
            } else {
                alert("예약 요청 중 오류가 발생했습니다.");
            }
            disableButton(false);
        });
    });

    function clearErrorMessages() {
        document.querySelectorAll(".error-message").forEach(el => el.remove());
    }

    function displayErrorMessages(errors) {
        Object.keys(errors).forEach(fieldName => {
            const field = document.querySelector(`[name="${fieldName}"]`);
            if (field) {
                const errorMessage = document.createElement("div");
                errorMessage.classList.add("error-message");
                errorMessage.style.color = "red";
                errorMessage.style.fontSize = "12px";
                errorMessage.style.marginTop = "5px";
                errorMessage.textContent = errors[fieldName];
                field.parentNode.appendChild(errorMessage);
            }
        });
    }

    function disableButton(state) {
        submitButton.disabled = state;
        submitButton.innerHTML = state ? "예약 중..." : "예약하기";
    }
});

