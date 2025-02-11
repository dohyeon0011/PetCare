document.addEventListener("DOMContentLoaded", function () {
    let page = 1; // 현재 페이지 번호 (0부터 시작)
    const pageSize = 5; // 서버에서 설정한 페이지 크기
    const sitterId = document.getElementById("sitterData").getAttribute("data-sitter-id");
    const loadMoreBtn = document.getElementById("load-more-reviews");

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
});
