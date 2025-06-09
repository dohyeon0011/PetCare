let stompClient = null;
let currentRoomId = null;
let receiverUserId = null;
const chatInput = document.getElementById('chat-input');
const sendButton = document.getElementById('send-chat');

document.addEventListener("DOMContentLoaded", () => {
    const memberId = document.getElementById("memberData").dataset.memberId;
    const token = document.getElementById("memberData").dataset.jwtToken;

    // STOMP 연결
    const connect = () => {
        const socket = new SockJS("http://ec2-3-36-128-60.ap-northeast-2.compute.amazonaws.com:9090/ws-chat"); // 배포 환경
//        const socket = new SockJS("http://localhost:9090/ws-chat");   // 개발 환경
        stompClient = Stomp.over(socket);

        const headers = {};
        if (token && token !== "undefined" && token.trim() !== "") {    // JWT 토큰이 있을 때만 헤더에 넣음
            headers['Authorization'] = `Bearer ${token}`;
        }

        // STOMP 연결 후 동작
        stompClient.connect(
            headers,
            () => {
                console.log("STOMP 연결 성공.");
                console.log("Subscribing to /user/queue/chat/message");

                // 1:1 메시지 수신 구독 (로그인 사용자 기준)
                stompClient.subscribe(`/user/queue/chat/message`, (message) => {
                    const parsed = JSON.parse(message.body);

                    // ChatMessageResponse.messageDto 스펙인지, GenericMessage로 감싸진 구조인지 확인(@SendToUser로 받은 것인지, 상대가 보낸 경로를 구독하여 받은 것인지 구별하기 위해서)
                    const isMessageDto = parsed.roomCode && parsed.message;
                    const chatMessage = isMessageDto ? parsed : parsed.body;
                    console.log("받은 메시지:", chatMessage);

                    const isCurrentRoomOpen = chatMessage.roomCode === currentRoomId;
                    const isMyMessage = Number(chatMessage.senderId) === Number(memberId);
                    // 현재 채팅방과 일치할 때만 메시지 표시
                    if (isCurrentRoomOpen || isMyMessage) { // 내가 보낸 메시지나 현재 채팅방에 접속해서 보고 있을 때
                        showMessage(chatMessage);
                    } else { // 다른 채팅방 메시지일 경우 알림과 같은 방법으로 처리
                        showNotification(chatMessage);
                    }
                });
            },
            (error) => console.error("STOMP 연결 실패.", error)
        );
    };

    // 메시지 전송
    const sendMessage = () => {
        const chatInput = document.getElementById("chat-input");
        const content = chatInput.value.trim();

        // STOMP 클라이언트가 연결되어 있는지 확인
        if (!stompClient || !stompClient.connected) {
            console.error("STOMP 클라이언트가 연결되지 않았습니다.");
            return;
        }

        // 수신자 ID 확인
        if (!receiverUserId) {
            console.error("수신자 ID가 설정되지 않았습니다.");
            return;
        }

        // 메시지가 비어있으면 전송하지 않음
        if (!content) {
            console.log("전송할 채팅 메시지가 비어있습니다.");
            return;
        }
        // currentRoomId가 null이면 "new"로 설정(해당 돌봄사와 처음으로 대화를 시작하여 새로운 채팅방도 만든다고 가정)
        const roomId = currentRoomId || "new";

        stompClient.send(
            `/app/chat-rooms/${roomId}/message`,
            {},
            JSON.stringify({
                senderId: memberId,
                receiverId: receiverUserId, // 선택된 채팅방 상대 id
                message: content,
            })
        );
        chatInput.value = "";   // 입력창 초기화
        chatInput.focus();  // 포커스를 다시 입력창에 맞춤 (사용자 경험(편의성) 향상)
        sendButton.disabled = true;
        // 전송 애니메이션
        sendButton.style.transform = 'translateY(0) scale(0.9)';
        setTimeout(() => {
            sendButton.style.transform = '';
        }, 150);
    };

    // 해당 채팅방에 메시지 표시
    const showMessage = (msg) => {
        const chatMessages = document.getElementById("chat-messages");

        // 메시지 컨테이너 생성
        const messageContainer = document.createElement("div");
        messageContainer.className = "message-container"; // 커스텀 컨테이너

        // 메시지 말풍선 생성
        const messageDiv = document.createElement("div");
        messageDiv.className = "message";

        // 본인이 보낸 메시지인지 확인하여 스타일 구분
        if (msg.senderId == memberId) {
            messageContainer.classList.add("sent");
            messageDiv.classList.add("sent");
        } else {
            messageContainer.classList.add("received");
            messageDiv.classList.add("received");
        }
        // 메시지 본문
        messageDiv.textContent = msg.message;

        // 날짜/시간 정보 (말풍선 외부에 위치)
        const timestamp = document.createElement("div");
        timestamp.className = "timestamp";
        timestamp.textContent = formatDateTimeForChatMessage(msg.sentAt);

        // 컨테이너에 말풍선과 timestamp 추가
        messageContainer.appendChild(messageDiv);
        messageContainer.appendChild(timestamp);

        // 채팅 메시지 영역에 컨테이너 추가
        chatMessages.appendChild(messageContainer);

        // 스크롤을 맨 아래로 이동 (새 메시지가 보이도록)
        chatMessages.scrollTop = chatMessages.scrollHeight;
    };

    // 채팅 메시지별 날짜와 시각 포맷 함수 (예: 2025-06-05 14:32 형식)
    const formatDateTimeForChatMessage = (isoString) => {
        const date = new Date(isoString);
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    };

    // 채팅방 아이콘 클릭 -> 채팅방 목록 팝업 표시
    document.getElementById("chatroom-icon").addEventListener("click", () => {
        loadChatRoomList();
        const chatModal = document.getElementById("chatroom-list-modal");
        chatModal.classList.remove("d-none"); // 일단 숨김 해제

        // 다음 repaint 시점에 .show 추가해서 transition 적용
        requestAnimationFrame(() => {
            chatModal.classList.add("show");
        });
    });

    // 채팅방 목록 팝업 닫기
    document.getElementById("close-chatroom-list").addEventListener("click", () => {
        const chatModal = document.getElementById("chatroom-list-modal");
        chatModal.classList.remove("show");
        // transition 시간(0.4초) 후에 d-none 추가해서 완전 숨김 처리
        setTimeout(() => {
            chatModal.classList.add("d-none");
        }, 400);
    });

    // 메시지 전송 버튼 클릭 이벤트
    document.getElementById("send-chat").addEventListener("click", sendMessage);

    // 엔터키로 메시지 전송
    document.getElementById("chat-input").addEventListener("keypress", (event) => {
        // Enter키가 눌렸고, Shift키가 함께 눌리지 않은 경우에만 전송
        // Shift + Enter는 줄바꿈으로 동작
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault(); // 기본 엔터 동작(줄바꿈) 방지
            sendMessage();
        }
    });

    // 채팅방 목록 불러오기 (ajax)
    const loadChatRoomList = () => {
        fetch(`/api/pets-care/members/${memberId}/chat/chat-rooms`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then((rooms) => {
                console.log("rooms:", rooms);
                const list = document.getElementById("chatroom-list");
                list.innerHTML = "";

                if (rooms.length === 0) {
                    const li = document.createElement("li");
                    li.textContent = "참여 중인 채팅방이 없습니다.";
                    li.style.color = "#999";
                    li.style.cursor = "default";
                    list.appendChild(li);
                    return;
                }

                rooms.forEach((room) => {
                    const li = document.createElement("li");
                    li.classList.add("chatroom-item");

                    // 채팅방 이름 (수신자 이름)
                    const nameDiv = document.createElement("div");
                    nameDiv.classList.add("chatroom-name");
                    nameDiv.textContent = room.receiverName;

                    // 최근 메시지
                    const messageDiv = document.createElement("div");
                    messageDiv.classList.add("chatroom-latest-message");
                    messageDiv.textContent = room.latestMessage || "메시지가 없습니다.";

                    // 시간 포맷팅해서 보여주기 (ex: 카카오톡처럼 14:23, 어제, 2023-06-01)
                    const timeDiv = document.createElement("div");
                    timeDiv.classList.add("chatroom-latest-time");
                    timeDiv.textContent = formatDateTimeForChatRoom(room.latestAt);

                    li.appendChild(nameDiv);
                    li.appendChild(messageDiv);
                    li.appendChild(timeDiv);

                    li.addEventListener("click", () => enterRoom(room.roomId, room.receiverId, room.receiverName));
                    list.appendChild(li);
                });
            })
            .catch((error) => {
                console.error("채팅방 목록 로드 실패:", error);
                const list = document.getElementById("chatroom-list");
                list.innerHTML = "<li style='color: red;'>채팅방 목록을 불러올 수 없습니다.</li>";
            });
    };

    // 채팅방별 마지막으로 진행된 메시지 날짜, 시간 표기 설정(카카오톡처럼)
    const formatDateTimeForChatRoom = (dateTimeString) => {
        if (!dateTimeString) return "";

        const date = new Date(dateTimeString);
        const now = new Date();

        const diffMs = now.getTime() - date.getTime();
        const diffSec = Math.floor(diffMs / 1000);
        const diffMin = Math.floor(diffSec / 60);
        const diffHour = Math.floor(diffMin / 60);
        const diffDay = Math.floor(diffHour / 24);

        if (diffSec < 60) {
            return "방금 전";
        } else if (diffMin < 60) {
            return `${diffMin}분 전`;
        } else if (diffHour < 24 && now.getDate() === date.getDate()) {
            // 오늘 날짜 내 1시간 이상인 경우
            return `${diffHour}시간 전`;
        } else if (diffDay === 1) {
            return "어제";
        } else if (diffDay > 1 && diffDay < 7) {
            return `${diffDay}일 전`;
        } else {
            // 7일 이상: YYYY-MM-DD
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, "0");
            const day = date.getDate().toString().padStart(2, "0");
            return `${year}-${month}-${day}`;
        }
    };

    const enterRoom = (roomId, receiverId, receiverName) => {
        currentRoomId = roomId;
        receiverUserId = receiverId;

        // 팝업(모달) 닫고 채팅창 열기
        document.getElementById("chatroom-list-modal").classList.add("d-none");
        document.getElementById("chatroom-list-modal").classList.remove("show");
        document.getElementById("chatroom-window").classList.remove("d-none");

        // 채팅방 제목 업데이트
        document.getElementById("chatroom-title").textContent = `${receiverName}님과의 채팅방 `;

        // 기존 메시지 목록 초기화
        document.getElementById("chat-messages").innerHTML = "";
        document.getElementById("chat-messages").innerHTML = "<div style='text-align:center; color: #999;'>이전 메시지가 없습니다.</div>";

        // 기존 메시지 조회
        fetch(`/api/pets-care/members/${memberId}/chat/chat-rooms/${roomId}/messages`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then((data) => {
                const messages = data.chatMessages; // chatMessages 리스트 배열 추출
                messages.forEach(showMessage);      // 각 메시지를 화면에 출력
            })
            .catch((error) => {
                console.error("채팅 메시지 로드 실패:", error);
                const chatMessages = document.getElementById("chat-messages");
                const errorMsg = document.createElement("div");
                errorMsg.textContent = "메시지를 불러올 수 없습니다.";
                errorMsg.style.color = "red";
                errorMsg.style.textAlign = "center";
                errorMsg.style.padding = "20px";
                chatMessages.appendChild(errorMsg);
            })
            .finally(() => {
                // 채팅창이 열린 후 입력창에 포커스
                document.getElementById("chat-input").focus();
            });
    };

    document.getElementById("sitterChatButton")?.addEventListener("click", () => {
        const sitterId = document.getElementById("sitterChatButton").dataset.sitterId;
        const sitterName = document.getElementById("sitterChatButton").dataset.sitterName;

        if (!sitterId || !sitterName) {
            alert("채팅할 돌봄사 정보가 없습니다.");
            return;
        }

        // 서버에 해당 돌봄사와의 기존 채팅방 존재 여부만 확인 (생성 X)
        fetch(`/api/pets-care/members/${memberId}/chat/chat-rooms-exists?sitterId=${sitterId}`, {
            method: "GET",
        })
            .then((res) => {
                if (!res.ok) throw new Error("채팅방 존재 확인 실패");
                return res.json();
            })
            .then((data) => {
                const { roomId, receiverId } = data || {}; // data가 null(해당 돌봄사와 진행하던 채팅방 정보가 없는 경우)이면 빈 객체 할당

                if (roomId && receiverId) {
                    // 해당 돌봄사와 진행하던 기존 채팅방이 있는 경우 enterRoom을 호출해 메시지 불러오기
                    enterRoom(roomId, receiverId, sitterName);
                } else {
                    // 없을 경우: 새로운 메시지 전송 시 'new'로 처리
                    currentRoomId = null;
                    receiverUserId = sitterId;
                }

                // 채팅창 UI 열기만
                document.getElementById("chatroom-window").classList.remove("d-none");
                document.getElementById("chatroom-title").textContent = `${sitterName}님과의 채팅`;
                document.getElementById("chat-messages").innerHTML = "<div style='text-align:center; color: #999;'>이전 메시지가 없습니다.</div>";
                document.getElementById("chat-input").focus();
            })
    });

    // 채팅창 닫기
    document.getElementById("close-chatroom-window").addEventListener("click", () => {
        document.getElementById("chatroom-window").classList.add("d-none");
        // 채팅방 정보 초기화
        currentRoomId = null;
        receiverUserId = null;
    });

    // 외부 클릭시 팝업(모달) 닫기
    document.addEventListener("click", (event) => {
        const chatroomIcon = document.getElementById("chatroom-icon");
        const chatroomModal = document.getElementById("chatroom-list-modal");

        // 채팅방 아이콘이나 팝업(모달) 내부를 클릭한 게 아니라면 팝업(모달) 닫기
        if (!chatroomIcon.contains(event.target) && !chatroomModal.contains(event.target)) {
            chatroomModal.classList.add("d-none");
            chatroomModal.classList.remove("show");
        }
    });

    // 채팅창 알림 표시
    function showNotification(msg) {
        console.log("알림 표시 시도:", msg);
        // UI 상단이나 알림 창에 표시
        const notify = document.getElementById("chat-notification");
        if (!notify) {
            return;
        }
        notify.classList.remove("d-none");
        notify.textContent = `${msg.senderName}님의 새 메시지: ${msg.message}`;
        notify.classList.add("show");

        // 알림창 클릭 시 해당 채팅방으로 이동
        notify.onclick = () => {
            if (msg.roomCode) {
                enterRoom(msg.roomCode, msg.senderId, msg.senderName);
            }
        };

        // 5초 표시 후 자동 숨기기
        setTimeout(() => {
            notify.classList.remove("show");
            notify.classList.add("d-none");
        }, 4000);
    }

    // 입력 내용에 따라 버튼 활성화/비활성화
    chatInput.addEventListener('input', function() {
        if (this.value.trim()) {
            sendButton.disabled = false;
        } else {
            sendButton.disabled = true;
        }
    });

    // STOMP 연결
    connect();

    // 페이지 언로드시 연결 해제
    window.addEventListener("beforeunload", () => {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect();
            console.log("STOMP 연결 해제");
        }
    });

    // 초기 상태: 버튼 비활성화
    sendButton.disabled = true;
});
