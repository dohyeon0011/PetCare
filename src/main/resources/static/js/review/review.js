$(document).ready(function() {
    const customerId = $('#memberData').data('member-id'); // customerId 가져오기

    // 리뷰 작성 버튼 클릭 시 모달 열기
    $('#writeReviewBtn').on('click', function() {
        const reservationId = $(this).data('reservation-id');
        $('#reservationId').val(reservationId);
        $('#reviewId').val(''); // 신규 리뷰는 ID 비움
        $('#comment').val(''); // 댓글 비움
        $('#rating').val(0); // 별점 초기화
        setStarRating(0); // 별점 초기화
        $('#reviewModalLabel').text('리뷰 작성');
        $('#reviewModal').modal('show');
        $('#deleteReviewBtn').hide(); // 삭제 버튼 숨기기 (새로운 리뷰인 경우)
    });

    // 리뷰 수정 버튼 클릭 시 모달 열기
    $('#editReviewBtn').on('click', function() {
        const reviewId = $(this).data('review-id');
        const reservationId = $(this).data('reservation-id'); // 예약 ID 추가로 가져오기

        $('#reviewId').val(reviewId);
        $('#reservationId').val(reservationId);

        // 서버에서 리뷰 정보를 가져와서 모달에 채우는 로직
        getReviewData(reviewId); // 리뷰 데이터를 서버에서 가져오는 함수 호출
        $('#deleteReviewBtn').show(); // 수정 리뷰인 경우 삭제 버튼 보이기
    });

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

    // 페이지 로딩 시, 초기 별점 상태 설정 (기본값)
    const initialRating = parseFloat($('#rating').val());
    if (!isNaN(initialRating)) {
        setStarRating(initialRating);
    }

    // 리뷰 저장 또는 수정
    $('#submitReview').on('click', function() {
        const reviewData = {
            reviewId: $('#reviewId').val(),
            reservationId: $('#reservationId').val(),
            rating: $('#rating').val(),
            comment: $('#comment').val()
        };

        // 리뷰 데이터를 서버에 제출하는 함수 (새로운 리뷰이거나 수정된 리뷰일 때)
        saveReview(reviewData);
    });

    // 리뷰 데이터 저장 (서버로 제출)
    function saveReview(data) {
        const reviewId = data.reviewId;
        const reservationId = data.reservationId;

        let url = '/api/pets-care/members/' + customerId + '/reservations/' + reservationId + '/reviews/new'; // 기본 URL (새로운 리뷰)
        if (reviewId) {
            url = '/api/pets-care/members/' + customerId + '/reviews/' + reviewId; // 리뷰 ID가 있으면 수정 URL
        }

        $.ajax({
            url: url, // 리뷰 ID가 있으면 수정 URL로 변경
            type: reviewId ? 'PUT' : 'POST', // PUT 요청은 수정, POST 요청은 새로운 리뷰 작성
            contentType: 'application/json', // 요청 형식을 JSON으로 설정
            data: JSON.stringify(data), // 데이터를 JSON 문자열로 변환하여 전송
            success: function(response) {
                alert('리뷰가 ' + (reviewId ? '수정' : '저장') + '되었습니다!');
                $('#reviewModal').modal('hide');
                location.reload(); // 페이지 새로 고침
            },
            error: function(error) {
                alert('리뷰 ' + (reviewId ? '수정' : '저장') + '에 실패했습니다.');
            }
        });
    }

    // 서버에서 리뷰 데이터를 가져오는 함수
    function getReviewData(reviewId) {
        $.ajax({
            url: '/api/pets-care/reviews/' + reviewId,  // 리뷰를 가져오는 서버의 URL
            type: 'GET',
            dataType: 'json',  // 서버에서 JSON 형식으로 데이터가 반환된다고 가정
            success: function(response) {
                $('#comment').val(response.comment); // 리뷰 내용 채우기
                $('#rating').val(response.rating);   // 별점 채우기
                setStarRating(response.rating);      // 별점 표시
                $('#reviewModal').modal('show');      // 리뷰 모달 팝업 열기
            },
            error: function(error) {
                console.error('리뷰 데이터 가져오기 실패:', error);
            }
        });
    }

    // 리뷰 삭제 버튼 클릭 시
    $('#deleteReviewBtn').on('click', function() {
        const reviewId = $(this).data('review-id');  // 데이터 속성에서 리뷰 ID 가져오기

        if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
            deleteReview(customerId, reviewId); // customerId를 이미 가져왔으므로 그걸 넘겨서 삭제
        }
    });

    // 서버에 리뷰 삭제 요청 보내기
    function deleteReview(customerId, reviewId) {
        $.ajax({
            url: '/api/pets-care/members/' + customerId + '/reviews/' + reviewId,  // 리뷰 삭제 요청 URL
            type: 'DELETE',
            success: function(response) {
                alert('리뷰가 삭제되었습니다.');
                $('#reviewModal').modal('hide');
                location.reload(); // 페이지 새로 고침
            },
            error: function(error) {
                console.error('리뷰 삭제 실패:', error);
                alert('리뷰 삭제에 실패했습니다.');
            }
        });
    }
});
