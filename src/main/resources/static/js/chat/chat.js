let stompClient = null;
let currentRoomId = null;

document.addEventListener("DOMContentLoaded", () => {
    const memberId = document.getElementById("memberData").dataset.memberId;
    const token = document.getElementById("memberData").dataset.jwtToken;

    const connect = () => {
        const socket = new SockJS("http://localhost:9090/ws-chat");
        stompClient = Stomp.over(socket);

        const headers = {};
        if (token && token !== "undefined" && token.trim() !== "") {    // JWT 토큰이 있을 때만 헤더에 넣음
            headers['Authorization'] = `Bearer ${token}`;
        }

        stompClient.connect(
            headers,
            () => {
                console.log("STOMP 연결 성공.");

                // 1:1 메시지 수신 구독 (로그인 사용자 기준)
                stompClient.subscribe(`/user/queue/chat-message`, (message) => {
                    const chatMessage = JSON.parse(message.body);
                    showMessage(chatMessage);
                });
            },
            (error) => console.error("STOMP 연결 실패.", error)
        );
    };

    const sendMessage = () => {
        const content = document.getElementById("chat-input").value.trim();
        if (!content || !currentRoomId) return;

        stompClient.send(
            `/app/chat-room/${currentRoomId}/message`,
            {},
            JSON.stringify({
                senderId: memberId,
                receiverId: otherUserId, // 선택된 채팅방 상대 id
                message: content,
            })
        );
        document.getElementById("chat-input").value = "";
    };

    const showMessage = (msg) => {
        const chatMessages = document.getElementById("chat-messages");
        const newMsg = document.createElement("div");
        newMsg.textContent = `[${msg.senderId}] ${msg.message}`;
        chatMessages.appendChild(newMsg);
    };

    // 채팅방 아이콘 클릭 → 모달 표시
    document.getElementById("chatroom-icon").addEventListener("click", () => {
        document.getElementById("chatroom-list-modal").classList.remove("d-none");
        loadChatRoomList();
    });

    // 채팅방 모달 닫기
    document.getElementById("close-chatroom-list").addEventListener("click", () => {
        document.getElementById("chatroom-list-modal").classList.add("d-none");
    });

    // 메시지 전송 버튼
    document.getElementById("send-btn").addEventListener("click", sendMessage);

    // 채팅방 목록 불러오기 (ajax)
    const loadChatRoomList = () => {
        fetch(`/api/pets-care/members/${memberId}/chat/rooms/`)
            .then((res) => res.json())
            .then((rooms) => {
                const list = document.getElementById("chatroom-list");
                list.innerHTML = "";
                rooms.forEach((room) => {
                    const li = document.createElement("li");
                    li.textContent = `채팅방 ${room.roomId}`;
                    li.addEventListener("click", () => enterRoom(room.roomId, room.otherUserId));
                    list.appendChild(li);
                });
            });
    };

    const enterRoom = (roomId, receiverId) => {
        currentRoomId = roomId;
        otherUserId = receiverId;
        document.getElementById("chatroom-list-modal").classList.add("d-none");
        document.getElementById("chat-box").classList.remove("d-none");
        document.getElementById("chat-messages").innerHTML = "";

        // 기존 메시지 조회
        fetch(`/api/pets-care/chat/rooms/${roomId}/messages`)
            .then((res) => res.json())
            .then((messages) => {
                messages.forEach(showMessage);
            });
    };

    // 초기 연결 시도
    connect();
});
