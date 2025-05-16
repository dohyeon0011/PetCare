// Redis 기반 챗봇 AI

// 챗봇 메시지 서버로 전송 (자유 입력 메시지)
async function sendMessage(message) {
  const uuid = getOrCreateGuestUUID();
  const payload = {
    message: message,
    guestUUID: uuid
  };

  try {
    const response = await fetch("/api/pets-care/chatbot/send", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    const reply = await response.json(); // ChatMessage DTO

    // 클라이언트 메시지에도 timestamp 추가
    const now = new Date();
    const kst = new Date(now.getTime() + 9 * 60 * 60 * 1000);   // UTC 시간에서 9시간을 ms 단위로 더해서 KST로 보정
    const clientTs = kst.toISOString().slice(0, 16).replace("T", " ");
    appendMessage({ message, type: "send", timestamp: clientTs });

    // 서버 응답은 DTO의 timestamp 사용
    appendMessage(reply);

  } catch (error) {
    console.error("메시지 전송 오류:", error);
    const now = new Date();
    const clientTs = now.toISOString().slice(0,16).replace("T"," ");
    appendMessage({ message: "서버 오류가 발생했습니다.", type: "answer", timestamp: clientTs });
  }
}

// UUID 저장 및 불러오기(비회원 게스트인 경우 식별자 UUID를 클라이언트에서 생성)
function getOrCreateGuestUUID() {
  let uuid = localStorage.getItem("guestUUID");
  if (!uuid) {
    // crypto.randomUUID() 지원 여부 확인 후 fallback
    if (window.crypto && typeof window.crypto.randomUUID === 'function') {
        uuid = crypto.randomUUID(); // 브라우저 내 UUID 생성
    } else {
        uuid = generateUUID(); // fallback 사용
    }
    localStorage.setItem("guestUUID", uuid);    // 로컬 스토리지에 비회원 식별자 UUID 저장
  }
  return uuid;
}

// 배포 서버에서 crypto.randomUUID() 사용이 안될 경우(배포 서버 환경의 내장 브라우저 호환성 -> 최신 브라우저에서만 지원이 됨.)
function generateUUID() {
  // fallback UUID generator
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// ------------------ 버튼 기반 응답 처리 ------------------ //
document.addEventListener('DOMContentLoaded', () => {
  fetch('/api/pets-care/chatbot/buttons')
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP 오류: ${response.status}`);
      }
      return response.json(); // JSON 바로 파싱
    })
    .then(data => {
      console.log("파싱 성공:", data);

      const container = document.getElementById('button-container');

      if (!Array.isArray(data)) {
        throw new TypeError("응답 데이터가 배열이 아닙니다.");
      }

      data.forEach(button => {
        const btn = document.createElement('button');
        btn.innerText = button.label;
        btn.classList.add('chat-button');
        btn.addEventListener('click', () => handleButtonClick(button.keyword, button.label));
        container.appendChild(btn);
      });
    })
    .catch(err => {
      console.error("버튼 로딩 또는 파싱 실패:", err);
    });
});

// 버튼 클릭 시 키워드 기반 챗봇 응답 요청
async function handleButtonClick(keyword, label) {
  const uuid = getOrCreateGuestUUID(); // 게스트 UUID (쿠키 or localStorage)
  const memberId = document.getElementById('memberData')?.dataset?.memberId; // 로그인된 사용자 ID
  const now = new Date();   // 클라이언트가 보낸 채팅도 시간을 표시하기 위해서
  const kst = new Date(now.getTime() + 9 * 60 * 60 * 1000);   // UTC 시간에서 9시간을 ms 단위로 더해서 KST로 보정
  const clientTs = kst.toISOString().slice(0, 16).replace("T", " ");

  // 버튼 메시지도 timestamp와 함께
  appendMessage({ message: label, type: "send", timestamp: clientTs });

  // 로그인 유저면 memberId 포함, 아니면 guestUUID 포함
  const payload = {
    keyword: keyword,
    ...(memberId ? { memberId: memberId } : { guestUUID: uuid })
  };

  try {
    const response = await fetch("/api/pets-care/chatbot/button", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    const reply = await response.json(); // ChatMessage DTO
    appendMessage(reply);
  } catch (err) {
    console.error(err);
    appendMessage({ message: "서버 오류가 발생했습니다.", type: "answer", timestamp: clientTs });
  }
}

// 페이지 로드 시 대화 기록 불러오기 + 안내 메시지
document.addEventListener('DOMContentLoaded', () => {
  const memberId = document.getElementById('memberData')?.dataset?.memberId;

  if (!memberId) {
    initGuestUUID(); // 비회원인 경우에만 UUID 초기화
  }
  if (memberId) {
    migrateGuestChatToUser(); // 로그인 사용자일 때만 게스트 기록 이전
  }

  // 대화 기록 불러오기
  loadChatHistory().then(messagesLoaded => {
    if (!messagesLoaded) {
      showWelcomeMessage();
    }
  });

  // 전송 버튼 클릭 시 메시지 전송
  const sendBtn = document.getElementById("send-btn");
  const inputField = document.getElementById("user-input");

  sendBtn.addEventListener("click", async () => {
    const message = inputField.value.trim();
    if (message !== "") {
      await sendMessage(message);
      inputField.value = ""; // 입력창 초기화
    }
  });

  // 엔터 키로도 메시지 전송
  inputField.addEventListener("keydown", async (e) => {
    if (e.key === "Enter") {
      e.preventDefault(); // 혹시 모를 form submit 방지
      sendBtn.click();    // 버튼 클릭 이벤트 트리거
    }
  });
});

// 대화 기록 조회 시 UUID 확인 후 재발급 용도
function initGuestUUID() {
  if (!localStorage.getItem("guestUUID")) {
    const uuid = (window.crypto && typeof window.crypto.randomUUID === 'function')
    ? crypto.randomUUID()
    : generateUUID();
    localStorage.setItem("guestUUID", uuid);
  }
}

// 로그인 시 대화 이전 API 호출(비회원 게스트 신분의 대화 내용을 로그인한 유저로 마이그레이션)
async function migrateGuestChatToUser() {
  const uuid = localStorage.getItem("guestUUID");
  if (!uuid) return;

  await fetch("/api/pets-care/chatbot/migrate", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ guestUUID: uuid })
  });

  localStorage.removeItem("guestUUID");
}

async function loadChatHistory() {
  const guestUUID = localStorage.getItem("guestUUID");
  const url = guestUUID ? `/api/pets-care/chatbot/history?guestUUID=${guestUUID}` : '/api/pets-care/chatbot/history';

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  });

  if (response.ok) {
    const chatHistory = await response.json();
    if (chatHistory.length === 0) {
      return false;
    }
    chatHistory.forEach(chatMessage => {
      appendMessage(chatMessage); // chatMessage는 {sender, message, type, timestamp}
    });
    return true;
  } else {
    console.error("대화 기록을 불러오는 데 실패했습니다.");
    return false;
  }
}

// ------------------ 공통 메시지 UI ------------------ //
// 사용자 화면 메시지 출력
function appendMessage(chatMessage) {
  const chatLog = document.getElementById("chat-log");

  // 1) wrapper 생성
  const wrapper = document.createElement("div");
  wrapper.classList.add("message-wrapper", chatMessage.type === "send" ? "user" : "bot");

  // 2) 말풍선(message) 생성
  const messageDiv = document.createElement("div");
  messageDiv.classList.add("message");
  messageDiv.innerHTML = chatMessage.message.replace(/\n/g, "<br>");

  // 3) 타임스탬프(timestamp) 생성
  const tsDiv = document.createElement("div");
  tsDiv.classList.add("timestamp");
  tsDiv.textContent = chatMessage.timestamp;

  // 4) wrapper에 차례로 붙이고 로그에 추가
  wrapper.appendChild(messageDiv);
  wrapper.appendChild(tsDiv);
  chatLog.appendChild(wrapper);
  chatLog.scrollTop = chatLog.scrollHeight;
}

document.getElementById("chatbot-toggle").addEventListener("click", () => {
  document.getElementById("chatbot-container").classList.remove("d-none");
});

document.getElementById("chatbot-close").addEventListener("click", () => {
  document.getElementById("chatbot-container").classList.add("d-none");
});

function showWelcomeMessage() {
    const now = new Date();
    const kst = new Date(now.getTime() + 9 * 60 * 60 * 1000);   // UTC 시간에서 9시간을 ms 단위로 더해서 KST로 보정
    const clientTs = kst.toISOString().slice(0, 16).replace("T", " ");

    const message = "안녕하세요! 🐶 반려견 챗봇입니다.\n무엇을 도와드릴까요?";

    appendMessage({ message: message, type: "answer", timestamp: clientTs });
}

// 챗봇 토글
const chatbotToggle = document.getElementById('chatbot-toggle');
const chatbotContainer = document.getElementById('chatbot-container');
const chatbotClose = document.getElementById('chatbot-close');

chatbotToggle.addEventListener('click', () => {
  chatbotContainer.classList.remove('d-none');
  setTimeout(() => {
    chatbotContainer.classList.add('show');
  }, 10);
});

chatbotClose.addEventListener('click', () => {
  chatbotContainer.classList.remove('show');
  setTimeout(() => {
    chatbotContainer.classList.add('d-none');
  }, 300); // 애니메이션 끝나고 숨김 처리
});
