document.addEventListener("DOMContentLoaded", function () {
    let page = 1; // 현재 페이지 번호 (0부터 시작)
    const pageSize = 5; // 서버에서 설정한 페이지 크기
    const sitterId = document.getElementById("sitterData").getAttribute("data-sitter-id");
    const loadMoreBtn = document.getElementById("load-more-reviews");
    const reservationButton = document.getElementById("reservationButton");

    if (loadMoreBtn) {
        loadMoreBtn.addEventListener("click", function () {
            fetch(`/api/pets-care/reservable/members/${sitterId}/reviews?page=${page}`)
                .then(response => response.json())
                .then(data => {
                    const reviewList = document.getElementById("review-list");

                    // 리뷰 추가
                    data.reviews.forEach(review => {
                        const reviewDiv = document.createElement("div");
                        reviewDiv.classList.add("review");
                        reviewDiv.innerHTML = `
                            <p><strong>${review.customerNickName}</strong> 님의 후기</p>
                            <p class="rating">
                                ${generateStarRating(review.rating)}
                            </p>
                            <p>${review.comment}</p>
                        `;
                        reviewList.appendChild(reviewDiv);
                    });

                    // 총 리뷰 개수보다 더 많은 데이터를 요청할 경우 버튼 숨김
                    if ((page + 1) * pageSize >= data.totalReviews) {
                        loadMoreBtn.style.display = "none";
                    }

                    page++; // 다음 페이지 증가
                })
                .catch(error => console.error("리뷰 로드 실패:", error));
        });
    }

    // 별점 HTML을 생성하는 함수 (Thymeleaf 스타일 적용)
    function generateStarRating(rating) {
        let starsHtml = '';
        for (let i = 1; i <= 5; i++) {
            if (i <= rating) {
                starsHtml += '<span class="star filled">★</span>'; // 채워진 별
            } else if (i === Math.ceil(rating) && rating % 1 !== 0) {
                starsHtml += '<span class="star half-filled">★</span>'; // 반 채워진 별
            } else {
                starsHtml += '<span class="star empty">★</span>'; // 비어있는 별
            }
        }
        return starsHtml;
    }

    if (reservationButton) {
        reservationButton.addEventListener('click', function() {
            // data-* 속성에서 값을 가져옵니다.
            var customerId = reservationButton.getAttribute("data-current-user-id");

            // 예약 처리 요청
            fetch(`/pets-care/members/${customerId}/sitters/${sitterId}/reservations/new`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'  // JSON 형식으로 응답 받기
                }
            })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        // 서버에서 반환된 에러 메시지를 alert()으로 띄우기
                        alert(errorData.error);  // 에러 메시지를 alert()로 띄우기
                        throw new Error(errorData.error);  // 추가적인 예외 처리
                    });
                }
                // 성공적으로 처리된 경우 (예: 예약 페이지로 이동)
                window.location.href = response.url;  // 예약 페이지로 이동
            })
            .catch(error => {
                console.error('Error:', error);  // 발생한 오류 콘솔에 출력
            });
        });
    }
});
