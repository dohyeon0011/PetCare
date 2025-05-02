let lastRefreshTime = 0;
const REFRESH_INTERVAL = 5 * 60 * 1000; // 5분 (밀리초 단위)

async function refreshAccessTokenIfNeeded() {
    const now = Date.now();
    if (now - lastRefreshTime < REFRESH_INTERVAL) {
        return true; // 아직 새로 고칠 필요 없음
    }

    try {   // 쿨타임 5분이 지나면
        const response = await fetch('/api/auth/token', {
            method: 'POST',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error("세션이 만료되었습니다. 다시 로그인해 주세요.");
        }

        lastRefreshTime = now;
        return true;
    } catch (error) {
        alert(error.message);

        const logoutForm = document.getElementById("logoutForm");
        if (logoutForm) {
            logoutForm.submit();
        } else {
            console.error("로그아웃 폼을 찾을 수 없습니다.");
            window.location.href = "/pets-care/login";
        }

        return false;
    }
}

// 사용자 활동이 있을 때마다 호출(로그인 방식이 소셜(OAuth2)일 때만 토큰 자동 갱신 바인딩)
const isOAuthUser = document.querySelector('meta[name="oauth-login"]')?.content === 'true';
if (isOAuthUser) {
    // 캡처 단계에서 mousedown 이벤트 사용 (기본 동작보다 먼저 처리, 클릭보다 먼저 발생하므로 빠르게 토큰 검사 가능)
    // 사용자 동작 전 먼저 토큰 유효성 체크가 완료되므로, 메시지가 뜨지 않고 바로 로그아웃되는 현상 해결 가능
    document.addEventListener('mousedown', async function (event) {
        const clickable = event.target.closest('a, button');
        if (!clickable) return;

        const ok = await refreshAccessTokenIfNeeded();
        if (!ok) {
            event.preventDefault(); // 링크 이동/버튼 클릭 방지
            event.stopImmediatePropagation();
        }
    }, true);

    // 폼 직접 제출되는 경우도 막기(JS나 enter로 제출될 경우도 안전하게 막음)
    document.addEventListener('submit', async function (event) {
        const ok = await refreshAccessTokenIfNeeded();
        if (!ok) {
            event.preventDefault();
            event.stopImmediatePropagation();   // 다른 이벤트 핸들러까지 완전히 중단
        }
    }, true);
}