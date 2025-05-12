// Redis 기반 챗봇 AI
// UUID 저장 및 불러오기(비회원 게스트인 경우 식별자 UUID를 클라이언트에서 생성)
function getOrCreateGuestUUID() {
  let uuid = localStorage.getItem("guestUUID");
  if (!uuid) {
    uuid = crypto.randomUUID(); // 브라우저 내 UUID 생성
    localStorage.setItem("guestUUID", uuid);    // 로컬 스토리지에 비회원 식별자 UUID 저장
  }
  return uuid;
}

// 챗봇 메시지 서버로 전송
async function sendMessage(message) {
  const uuid = getOrCreateGuestUUID();
  const payload = {
    message: message,
    guestUUID: uuid
  };

  await fetch("/api/chat/send", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
}

// 로그인 시 대화 이전 API 호출(비회원 게스트 신분의 대화 내용을 로그인한 유저로 마이그레이션)
async function migrateGuestChatToUser() {
  const uuid = localStorage.getItem("guestUUID");
  if (!uuid) return;

  await fetch("/api/chat/migrate", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ guestUUID: uuid })
  });

  localStorage.removeItem("guestUUID");
}