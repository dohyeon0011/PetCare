document.addEventListener("DOMContentLoaded", () => {
    const hospitalListEl = document.getElementById("hospital-list");
    const paginationEl = document.getElementById("hospital-pagination");

    const sidoSelect = document.getElementById('sido-select');
    const sigunguSelect = document.getElementById('sigungu-select');
    const filterBtn = document.getElementById('filter-btn');

    let currentSido = '';
    let currentSigungu = '';

    let currentPage = 0;
    const pageSize = 9;

    // 지도 및 마커 관련
    const map = new naver.maps.Map('map', {
        center: new naver.maps.LatLng(37.5665, 126.9780), // 서울시청 초기 위치
        zoom: 13
    });
    let markers = [];
    let infoWindows = [];

    const regionData = {
      '서울특별시': [
        '강남구','강동구','강북구','강서구','관악구','광진구','구로구','금천구',
        '노원구','도봉구','동대문구','동작구','마포구','서대문구','서초구',
        '성동구','성북구','송파구','양천구','영등포구','용산구','은평구',
        '종로구','중구','중랑구'
      ],
      '부산광역시': [
        '중구','서구','동구','영도구','부산진구','동래구','남구','북구',
        '강서구','해운대구','사하구','금정구','연제구','수영구','사상구','기장군'
      ],
      '대구광역시': [
        '중구','동구','서구','남구','북구','수성구','달서구','달성군','군위군'
      ],
      '인천광역시': [
        '중구','동구','미추홀구','연수구','남동구','부평구','계양구','서구',
        '강화군','옹진군'
      ],
      '광주광역시': [
        '동구','서구','남구','북구','광산구'
      ],
      '대전광역시': [
        '동구','중구','서구','유성구','대덕구'
      ],
      '울산광역시': [
        '중구','남구','동구','북구','울주군'
      ],
      '세종특별자치시': [
        '보람동', // 세종은 동 단위로만 구성됨
      ],
      '경기도': [
        '수원시','성남시','고양시','용인시','안산시','안양시','부천시','평택시',
        '화성시','남양주시','파주시','김포시','이천시','안성시','오산시',
        '시흥시','군포시','의왕시','하남시','광명시','광주시','양주시','포천시',
        '여주시','동두천시','구리시','남양주시','의정부시','양평군','가평군','연천군','양주시'
      ],
      '강원도': [
        '춘천시','원주시','강릉시','동해시','태백시','속초시','삼척시','홍천군',
        '횡성군','영월군','평창군','정선군','철원군','화천군','양구군','인제군','고성군','양양군'
      ],
      '충청북도': [
        '청주시','충주시','제천시','음성군','증평군','진천군','괴산군','보은군',
        '옥천군','영동군','단양군'
      ],
      '충청남도': [
        '천안시','공주시','보령시','아산시','서산시','논산시','계룡시','당진시',
        '금산군','부여군','서천군','청양군','홍성군','예산군','태안군'
      ],
      '전라북도': [
        '전주시','군산시','익산시','정읍시','남원시','김제시','완주군','진안군',
        '무주군','장수군','임실군','순창군','고창군','부안군'
      ],
      '전라남도': [
        '목포시','여수시','순천시','나주시','광양시','담양군','곡성군','구례군',
        '고흥군','보성군','화순군','장흥군','강진군','해남군','영암군','무안군',
        '함평군','영광군','장성군','완도군','진도군','신안군'
      ],
      '경상북도': [
        '포항시','경주시','김천시','안동시','구미시','영주시','영천시','상주시',
        '문경시','경산시','군위군','의성군','청송군','영양군','영덕군','청도군',
        '고령군','성주군','칠곡군','예천군','봉화군','울진군','울릉군'
      ],
      '경상남도': [
        '창원시','진주시','통영시','사천시','김해시','밀양시','거제시','양산시',
        '의령군','함안군','창녕군','고성군','남해군','하동군','산청군','함양군',
        '거창군','합천군'
      ],
      '제주특별자치도': [
        '제주시','서귀포시'
      ]
    };

    // 시/도 드롭다운 초기화
    Object.keys(regionData).forEach(sido => {
        const option = document.createElement('option');
        option.value = sido;
        option.textContent = sido;
        sidoSelect.appendChild(option);
    });

    // 시/도 선택 시 시/군/구 채우기
    sidoSelect.addEventListener('change', () => {
        const selectedSido = sidoSelect.value;
        sigunguSelect.innerHTML = '<option value="">시/군/구 선택</option>';

        if (selectedSido && regionData[selectedSido]) {
            regionData[selectedSido].forEach(sigungu => {
                const option = document.createElement('option');
                option.value = sigungu;
                option.textContent = sigungu;
                sigunguSelect.appendChild(option);
            });
            sigunguSelect.disabled = false;
        } else {
            sigunguSelect.disabled = true;
        }
    });

    // 필터 버튼 클릭 시 검색
    filterBtn.addEventListener('click', () => {
        currentSido = sidoSelect.value;
        currentSigungu = sigunguSelect.value;
        loadHospitals(0, currentSido, currentSigungu);
    });

    function loadHospitals(page, sido = '', sigungu = '') {
        let url = `/api/pets-care/admin/pet-hospitals?page=${page}&size=${pageSize}`;
        if (sido) url += `&sido=${encodeURIComponent(sido)}`;
        if (sigungu) url += `&sigungu=${encodeURIComponent(sigungu)}`;

        fetch(url)
            .then(async res => {
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
                    renderHospitalList(data.content);
                    renderPagination(data);
                    updateMarkers(data.content);
                    currentPage = data.number;
                } catch (e) {
                    console.error("Error inside then:", e);
                    throw e;
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
        hospitalListEl.innerHTML = hospitals.map((h, idx) => `
            <li class="hospital-item" data-idx="${idx}">
                <div class="hospital-name">${h.name || '정보 없음'}</div>
                <div class="hospital-info">
                    <div class="info-item">우편번호: ${h.streetZipcode || '정보 없음'}</div>
                    <div class="info-item">상세주소: ${h.address || '정보 없음'}</div>
                    <div class="info-item">도로명 주소: ${h.streetAddress || '정보 없음'}</div>
                    <div class="info-item">TEL: ${h.tel || '정보 없음'}</div>
                </div>
            </li>
        `).join("");

        // 특정 병원 요소 클릭 시 이벤트 처리
        hospitalListEl.querySelectorAll('li.hospital-item').forEach(li => {
            li.style.cursor = 'pointer';
            li.addEventListener('click', () => {
                const index = parseInt(li.dataset.idx, 10);
                if (!isNaN(index) && markers[index]) {
                    const marker = markers[index];
                    const infoWindow = infoWindows[index];

                    // 기존 열려있는 InfoWindow 모두 닫기
                    closeAllInfoWindows();

                    // 지도 중심 이동
                    map.setCenter(marker.getPosition());
                    map.setZoom(15);  // 필요 시 확대 레벨 조정

                    // 해당 마커 InfoWindow 열기
                    infoWindow.open(map, marker);

                    // 지도 위치로 사용자 화면 이동
                    document.getElementById('map').scrollIntoView({ behavior: 'smooth' });
                } else {
                    // 마커(위도, 경도 값이 없는 경우)
                    alert('이 병원은 지도에 위치 정보(위도(lat), 경도(lng))가 없습니다.');
                }
            });
        });
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
                if (!isNaN(page)) loadHospitals(page, currentSido, currentSigungu);
            });
        });
    }

    // 맵에 마커 찍기
    function updateMarkers(hospitals) {
        clearMarkers();
        markers = [];
        infoWindows = [];

        hospitals.forEach(h => {
            if (h.lat != null && h.lng != null) {
                const marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(h.lat, h.lng),
                    map: map,
                    title: h.name || '동물 병원'
                });

                const infoWindow = new naver.maps.InfoWindow({
                    content: `
                        <div style="padding:5px; font-size:14px; color:#222;">
                            <strong>${h.name || '동물 병원'}</strong><br>
                            ${h.streetZipcode ? '우편번호: ' + h.streetZipcode + '<br>' : ''}
                            ${h.address ? '지번 주소: ' + h.address + '<br>' : ''}
                            ${h.streetAddress ? '도로명 주소: ' + h.streetAddress + '<br>' : ''}
                            ${h.tel ? '전화번호: ' + h.tel : ''}
                        </div>`
                });

                naver.maps.Event.addListener(marker, 'click', () => {
                    closeAllInfoWindows();
                    infoWindow.open(map, marker);
                });

                naver.maps.Event.addListener(map, 'click', function () {
                    closeAllInfoWindows();
                });

                markers.push(marker);
                infoWindows.push(infoWindow);
            } else {
                // 좌표 없으면 idx null 자리 확보
                markers.push(null);
                infoWindows.push(null);
            }
        });
        // 맨 첫 병원을 기준으로 중심 이동
        if (hospitals.length > 0 && hospitals[0].lat != null && hospitals[0].lng != null) {
            map.setCenter(new naver.maps.LatLng(hospitals[0].lat, hospitals[0].lng));
        }
    }

    function clearMarkers() {
        markers.forEach(marker => {
            if (marker) marker.setMap(null);
        });
        markers = [];
    }

    function closeAllInfoWindows() {
        infoWindows.forEach(iw => {
            if (iw) iw.close(); // null이 아닌 경우에만 close
        });
    }

    // 최초 페이지 로딩 시
    loadHospitals(currentPage);
});
