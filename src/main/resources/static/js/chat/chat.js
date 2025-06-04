let stompClient = null;
let currentRoomId = null;
let receiverUserId = null;

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
    };

    const showMessage = (msg) => {
        const chatMessages = document.getElementById("chat-messages");
        const newMsg = document.createElement("div");
        newMsg.className = "message"; // CSS 스타일링을 위한 클래스 추가

        // 본인이 보낸 메시지인지 확인하여 스타일 구분
        if (msg.senderId == memberId) {
            newMsg.classList.add("sent");
        } else {
            newMsg.classList.add("received");
        }

        newMsg.textContent = `[${msg.senderId}] ${msg.message}`;
        chatMessages.appendChild(newMsg);

        // 스크롤을 맨 아래로 이동 (새 메시지가 보이도록)
        chatMessages.scrollTop = chatMessages.scrollHeight;
    };

    // 채팅방 아이콘 클릭 -> 채팅방 목록 팝업 표시
    document.getElementById("chatroom-icon").addEventListener("click", () => {
        loadChatRoomList();
        const chatModal = document.getElementById("chatroom-list-modal");
        chatModal.classList.remove("d-none");
        chatModal.classList.add("show");
    });

    // 채팅방 목록 팝업 닫기
    document.getElementById("close-chatroom-list").addEventListener("click", () => {
        const chatModal = document.getElementById("chatroom-list-modal");
        chatModal.classList.remove("show");
        chatModal.classList.add("d-none");
    });

    // 메시지 전송 버튼 클릭 이벤트
    document.getElementById("send-chat").addEventListener("click", sendMessage);

    // 엔터키로 메시지 전송
    document.getElementById("chat-input").addEventListener("keypress", (event) => {
        // Enter키가 눌렸고, Shift키가 함께 눌리지 않은 경우에만 전송
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault(); // 기본 엔터 동작(줄바꿈) 방지
            sendMessage();
        }
        // Shift + Enter는 줄바꿈으로 동작 (기본 동작 유지)
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
                    li.textContent = `채팅방 ${room.roomId}`;
                    li.addEventListener("click", () => enterRoom(room.roomId, room.receiverId));
                    list.appendChild(li);
                });
            })
            .catch((error) => {
                console.error("채팅방 목록 로드 실패:", error);
                const list = document.getElementById("chatroom-list");
                list.innerHTML = "<li style='color: red;'>채팅방 목록을 불러올 수 없습니다.</li>";
            });
    };

    const enterRoom = (roomId, receiverId) => {
        currentRoomId = roomId;
        receiverUserId = receiverId;

        // 팝업(모달) 닫고 채팅창 열기
        document.getElementById("chatroom-list-modal").classList.add("d-none");
        document.getElementById("chatroom-list-modal").classList.remove("show");
        document.getElementById("chatroom-window").classList.remove("d-none");

        // 채팅방 제목 업데이트
        document.getElementById("chatroom-title").textContent = `채팅방 ${roomId}`;

        // 기존 메시지 목록 초기화
        document.getElementById("chat-messages").innerHTML = "";

        // 기존 메시지 조회
        fetch(`/api/pets-care/members/${memberId}/chat/chat-rooms/${roomId}/messages`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then((messages) => {
                messages.forEach(showMessage);
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
                    enterRoom(roomId, receiverId);
                } else {
                    // 없을 경우: 새로운 메시지 전송 시 'new'로 처리
                    currentRoomId = null;
                    receiverUserId = sitterId;
                }

                // 채팅창 UI 열기만
                document.getElementById("chatroom-window").classList.remove("d-none");
                document.getElementById("chatroom-title").textContent = `${sitterName}님과의 채팅`;
                document.getElementById("chat-messages").innerHTML = "";
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

    // STOMP 연결
    connect();

    // 페이지 언로드시 연결 해제
    window.addEventListener("beforeunload", () => {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect();
            console.log("STOMP 연결 해제");
        }
    });
});
