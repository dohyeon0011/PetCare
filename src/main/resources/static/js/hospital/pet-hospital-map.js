document.addEventListener("DOMContentLoaded", () => {
    const hospitalListEl = document.getElementById("hospital-list");
    const paginationEl = document.getElementById("hospital-pagination");

    let currentPage = 0;
    const pageSize = 9;

    // 지도 및 마커 관련
    const map = new naver.maps.Map('map', {
        center: new naver.maps.LatLng(37.5665, 126.9780), // 서울시청 초기 위치
        zoom: 13
    });
    let markers = [];

    function loadHospitals(page) {
        fetch(`/api/pets-care/admin/pet-hospitals?page=${page}&size=${pageSize}`)
            .then(async res => {
                console.log("Fetch response status:", res.status, res.statusText);

                if (!res.ok) {
                    const errorText = await res.text();
                    throw new Error(`HTTP ${res.status}: ${errorText || res.statusText}`);
                }

                const contentType = res.headers.get('content-type') || '';
                if (!contentType.includes('application/json')) {
                    const text = await res.text();
                    throw new Error(`Expected JSON but got: ${text}`);
                }
                const text = await res.text();
                if (!text) throw new Error('Empty response body');
                try {
                    const data = JSON.parse(text);
                    return data;
                } catch (e) {
                    throw new Error('Failed to parse JSON: ' + e.message);
                }
            })
            .then(data => {
                try {
                    console.log("pageData:", data);
                    renderHospitalList(data.content);
                    renderPagination(data);
//                    updateMarkers(data.content);
                    currentPage = data.number;
                } catch (e) {
                    console.error("Error inside then:", e);
                    throw e;  // 그래야 catch로 넘어감
                }
            })
            .catch(error => {
                hospitalListEl.innerHTML = `<li class="hospital-item">병원 목록을 불러오지 못했습니다.</li>`;
                paginationEl.innerHTML = "";
                clearMarkers();
                console.error("Fetch error:", error);
            });
    }

    function renderHospitalList(hospitals) {
        if (!Array.isArray(hospitals)) {
            throw new Error('병원 리스트 데이터가 배열이 아닙니다.');
        }
        if (hospitals.length === 0) {
            hospitalListEl.innerHTML = `<li class="hospital-item">표시할 병원이 없습니다.</li>`;
            return;
        }
        hospitalListEl.innerHTML = hospitals.map(h => `
            <li class="hospital-item">
                <div class="hospital-name">${h.name || '정보 없음'}</div>
                <div class="hospital-info">
                    <div class="info-item">우편번호: ${h.streetZipcode || '정보 없음'}</div>
                    <div class="info-item">상세주소: ${h.address || '정보 없음'}</div>
                    <div class="info-item">도로명 주소: ${h.streetAddress || '정보 없음'}</div>
                    <div class="info-item">TEL: ${h.tel || '정보 없음'}</div>
                </div>
            </li>
        `).join("");
    }

    function renderPagination(pageData) {
        const totalPages = pageData.totalPages;
        const current = pageData.number;
        if (totalPages <= 1) {
            paginationEl.innerHTML = "";
            return;
        }
        let html = `<ul class="pagination justify-content-center mt-4">`;
        html += `
            <li class="page-item ${pageData.first ? 'disabled' : ''}">
                <button class="page-link" data-page="0" ${pageData.first ? 'disabled' : ''}>⏮ 처음</button>
            </li>
        `;
        html += `
            <li class="page-item ${pageData.first ? 'disabled' : ''}">
                <button class="page-link" data-page="${current - 1}" ${pageData.first ? 'disabled' : ''}>이전</button>
            </li>
        `;

        // 현재 페이지 중심으로 최대 5개 페이징 번호 보여주기
        const maxButtons = 10;
        let startPage = Math.max(0, current - 2);
        let endPage = Math.min(totalPages - 1, startPage + maxButtons - 1);

        // startPage가 너무 커서 버튼 개수가 부족하면 조정
        if (endPage - startPage + 1 < maxButtons) {
            startPage = Math.max(0, endPage - maxButtons + 1);
        }
        for (let i = startPage; i <= endPage; i++) {
            html += `
                <li class="page-item ${i === current ? 'active' : ''}">
                    <button class="page-link" data-page="${i}">${i + 1}</button>
                </li>
            `;
        }
        html += `
            <li class="page-item ${pageData.last ? 'disabled' : ''}">
                <button class="page-link" data-page="${current + 1}" ${pageData.last ? 'disabled' : ''}>다음</button>
            </li>
        `;
        html += `
            <li class="page-item ${pageData.last ? 'disabled' : ''}">
                <button class="page-link" data-page="${totalPages - 1}" ${pageData.last ? 'disabled' : ''}>끝 ⏭</button>
            </li>
        `;
        html += `</ul>`;
        paginationEl.innerHTML = html;

        paginationEl.querySelectorAll("button.page-link").forEach(btn => {
            btn.addEventListener("click", () => {
                const page = parseInt(btn.dataset.page, 10);
                if (!isNaN(page)) loadHospitals(page);
            });
        });
    }

    // 맵에 마커 찍기
    function updateMarkers(hospitals) {
        clearMarkers();
        hospitals.forEach(h => {
            if (h.lat != null && h.lng != null) {
                const marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(h.lat, h.lng),
                    map: map,
                    title: h.name || '동물 병원'
                });

                const infoWindow = new naver.maps.InfoWindow({
                    content: `<div style="padding:5px; font-size:14px;"><strong>${h.name || '동물 병원'}</strong><br>${h.address || ''}</div>`
                });

                naver.maps.Event.addListener(marker, 'click', () => {
                    infoWindow.open(map, marker);
                });

                markers.push(marker);
            }
        });
        // 맨 첫 병원을 기준으로 중심 이동
        if (hospitals.length > 0 && hospitals[0].lat != null && hospitals[0].lng != null) {
            map.setCenter(new naver.maps.LatLng(hospitals[0].lat, hospitals[0].lng));
        }
    }

    function clearMarkers() {
        markers.forEach(marker => marker.setMap(null));
        markers = [];
    }

    // 최초 페이지 로딩 시
    loadHospitals(currentPage);
});
