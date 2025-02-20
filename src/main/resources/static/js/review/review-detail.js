// 리뷰 삭제 기능
function deleteReview(reviewId, customerId) {
    // 삭제 요청을 보낼 API URL
    const apiUrl = `/api/pets-care/members/${customerId}/reviews/${reviewId}`;

    // 삭제 확인 메시지
    if (confirm("정말로 이 리뷰를 삭제하시겠습니까?")) {
        // AJAX를 사용하여 DELETE 요청을 보냄
        $.ajax({
            url: apiUrl,
            type: 'DELETE',
            contentType: 'application/json',
            success: function(response) {
                alert('리뷰가 성공적으로 삭제되었습니다.');
                // 성공적으로 삭제 후 리뷰 목록 페이지로 이동
                window.location.href = '/pets-care/members/' + customerId + '/reviews';
            },
            error: function(xhr, status, error) {
                alert('리뷰 삭제에 실패했습니다. 다시 시도해 주세요.');
            }
        });
    }
}

// 별점 표시 업데이트 함수
function setStarRating(rating) {
    $('.star').each(function() {
        const starValue = parseFloat($(this).data('value'));

        // 별을 채운 상태
        if (starValue <= rating) {
            $(this).removeClass('empty half-filled').addClass('filled');
        }
        // 반 채워진 상태
        else if (starValue - rating === 0.5) {
            $(this).removeClass('filled empty').addClass('half-filled');
        }
        // 비어있는 상태
        else {
            $(this).removeClass('filled half-filled').addClass('empty');
        }
    });
}

// 별점 클릭 처리
$(document).ready(function() {
    // 별 클릭 시 처리
    $('.star').on('click', function() {
        const starValue = parseFloat($(this).data('value'));  // 클릭한 별의 값
        const currentRating = parseFloat($('#rating').val());  // 현재 선택된 별점 값

        // 클릭한 별과 현재 별점 값을 비교하여 반 칠해지는지 결정
        let newRating = 0;

        if (currentRating < starValue) {
            newRating = starValue;
        } else if (currentRating > starValue) {
            newRating = starValue - 0.5;
        } else {
            newRating = starValue - 0.5;
        }

        // 선택된 별점 값 hidden input에 저장
        $('#rating').val(newRating);

        // 별 UI 업데이트
        setStarRating(newRating);
    });
});

// 리뷰 수정 함수
function submitEditReview(reviewId) {
    // 회원 ID 가져오기
    const customerId = $('#memberData').data('member-id'); // 고객 ID 가져오기

    // 수정된 리뷰 내용 가져오기
    const updatedComment = $('#updatedComment').val();

    // 수정된 별점 가져오기
    const rating = $('#rating').val();

    // API 요청을 위한 데이터 객체 생성
    const requestData = {
        comment: updatedComment,
        rating: rating
    };

    // API 요청 보내기
    $.ajax({
        url: `/api/pets-care/members/${customerId}/reviews/${reviewId}`, // reviewId를 URL 경로에 넣음
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(requestData),
        success: function(response) {
            alert('리뷰가 수정되었습니다.');
            // 수정 후 리뷰 목록 페이지로 이동
            window.location.href = `/pets-care/members/${customerId}/reviews`;
        },
        error: function(error) {
            alert('리뷰 수정에 실패했습니다. 다시 시도해주세요.');
        }
    });
}