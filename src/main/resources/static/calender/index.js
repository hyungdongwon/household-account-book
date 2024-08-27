let date = new Date();

const renderCalendar = () => {
    const viewYear = date.getFullYear();
    const viewMonth = date.getMonth();

    document.querySelector('.year-month').textContent = `${viewYear}년 ${viewMonth + 1}월`;

    const prevLast = new Date(viewYear, viewMonth, 0);
    const thisLast = new Date(viewYear, viewMonth + 1, 0);

    const PLDate = prevLast.getDate();
    const PLDay = prevLast.getDay();

    const TLDate = thisLast.getDate();
    const TLDay = thisLast.getDay();

    const prevDates = [];
    const thisDates = [...Array(TLDate + 1).keys()].slice(1);
    const nextDates = [];

    if (PLDay !== 6) {
        for (let i = 0; i < PLDay + 1; i++) {
            prevDates.unshift(PLDate - i);
        }
    }

    for (let i = 1; i < 7 - TLDay; i++) {
        nextDates.push(i);
    }

    const dates = prevDates.concat(thisDates, nextDates);
    const firstDateIndex = dates.indexOf(1);
    const lastDateIndex = dates.lastIndexOf(TLDate);
    const today = new Date();
    const isToday = (d, m, y) => d === today.getDate() && m === today.getMonth() && y === today.getFullYear();

    dates.forEach((date, i) => {
        const condition = i >= firstDateIndex && i < lastDateIndex + 1
            ? 'this'
            : 'other';
        const todayClass = isToday(date, viewMonth, viewYear) ? 'today' : '';
        dates[i] = `<div class="date ${condition} ${todayClass}" onclick="openModal(${viewYear}, ${viewMonth + 1}, ${date})"><span class="day-number">${date}</span></div>`;
    });

    document.querySelector('.dates').innerHTML = dates.join('');
};

const openModal = (year, month, day) => {
    const modal = document.getElementById("myModal");
    const modalBody = document.getElementById("modal-body");

    const formattedMonth = month.toString().padStart(2, '0');
    const formattedDay = day.toString().padStart(2, '0');
    const formattedDate = `${year}-${formattedMonth}-${formattedDay}`;

    // AJAX 요청을 통해 서버에서 데이터를 가져옴
    fetch(`/api/financial-info?date=${formattedDate}`)
        .then(response => response.json())
        .then(data => {
            const incometotal = data.incometotal;
            const outcometotal = data.outcometotal;

            // 모달 내용 설정
            modalBody.innerHTML = `
                <p>날짜: ${formattedDate}</p>
                <br>
                <p>수입: ${incometotal} 원</p>
                <p>지출: ${outcometotal} 원</p>

                <h2>수입목록</h2>
                 <table>
                         <thead>
                             <tr>
                                 <th>수입내역</th>
                                 <th>금액</th>
                                 <th>메모</th>
                             </tr>
                         </thead>
                         <tbody>
                             ${data.incomeDTOList.map(income => `
                                 <tr>
                                     <td>${income.incomeContent}</td>
                                     <td>${income.price}</td>
                                     <td>${income.memo}</td>
                                 </tr>
                             `).join('')}
                         </tbody>
                     </table>
                 <h2>지출목록</h2>
                 <table>
                      <thead>
                          <tr>
                              <th>지출내역</th>
                              <th>금액</th>
                              <th>메모</th>
                          </tr>
                      </thead>
                      <tbody>
                          ${data.outcomeDTOList.map(outcome => `
                              <tr>
                                  <td>${outcome.outcomeContent}</td>
                                  <td>${outcome.price}</td>
                                  <td>${outcome.memo}</td>
                              </tr>
                          `).join('')}
                      </tbody>
                  </table>
            `;

            // 모달 창 띄우기
            modal.style.display = "block";
        })
        .catch(error => {
            console.error("Error fetching financial data:", error);
            modalBody.innerHTML = "<p>데이터를 가져오는 중 오류가 발생했습니다.</p>";
            modal.style.display = "block";
        });
};

// 모달 창 닫기 기능
const closeModal = () => {
    const modal = document.getElementById("myModal");
    modal.style.display = "none";
};

// 모달 닫기 버튼 이벤트 연결
document.querySelector(".close").onclick = closeModal;

// 모달 영역 외부 클릭 시 모달 닫기
window.onclick = function(event) {
    const modal = document.getElementById("myModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
};

const prevMonth = () => {
    date.setMonth(date.getMonth() - 1);
    renderCalendar();
};

const nextMonth = () => {
    date.setMonth(date.getMonth() + 1);
    renderCalendar();
};

const goToday = () => {
    date = new Date();
    renderCalendar();
};

renderCalendar();
