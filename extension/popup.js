const API_BASE = "http://localhost:8080"; 

const loader = document.getElementById("loader");
const matchesDiv = document.getElementById("matches");
const scorecardDiv = document.getElementById("scorecard");

const REFRESH_INTERVAL = 20000; 


loadMatches();
setInterval(loadMatches, REFRESH_INTERVAL);

function loadMatches() {
    showLoader(true);
    fetch(`${API_BASE}/api/scorecard`)
        .then(res => res.json())
        .then(matches => {
            showLoader(false);
            renderMatches(matches);
        })
        .catch(err => {
            console.error(err);
            showLoader(false);
            matchesDiv.innerHTML = "<p>Failed to load matches.</p>";
        });
}

function showLoader(show) {
    loader.style.display = show ? "block" : "none";
}

function renderMatches(matches) {
    matchesDiv.innerHTML = "";
    matches.forEach(match => {
        const card = document.createElement("div");
        card.className = "match-card";
        card.innerHTML = `
  <div class="match-header">
    <span class="series-name">${match.series}</span>
    <span class="match-format">${match.format}</span>
  </div>
  <div class="teams">
    <div class="team">
      <div class="team-name">${match.team1.name}</div>
      <div class="team-score">${match.team1.score}</div>
    </div>
    <div class="vs">vs</div>
    <div class="team">
      <div class="team-name">${match.team2.name}</div>
      <div class="team-score">${match.team2.score}</div>
    </div>
  </div>
  <div class="match-status">${match.status}</div>
  <div class="scheduled-time">${match.scheduledTime || ""}</div>
`;

        card.onclick = () => loadScorecard(match.id, match);
        matchesDiv.appendChild(card);
    });
}

let scorecardRefreshInterval;

function loadScorecard(matchId, match) {
    matchesDiv.style.display = "none";
    scorecardDiv.style.display = "block";
    scorecardDiv.innerHTML = "";
    showLoader(true);

    
    if (scorecardRefreshInterval) clearInterval(scorecardRefreshInterval);

    const loadAndRender = () => {
        fetch(`${API_BASE}/api/scorecard/${matchId}`)
            .then(res => res.json())
            .then(innings => {
                showLoader(false);
                renderScorecard(innings, matchId, match);
            })
            .catch(err => {
                console.error(err);
                showLoader(false);
                scorecardDiv.innerHTML = "<p>Failed to load scorecard.</p>";
            });
    };

    loadAndRender(); 
}

function renderScorecard(innings, matchId, match) {
    scorecardDiv.innerHTML = `
  <button id="backButton" class="back-button">⬅ Back</button>
  <button id="refreshButton" class="refresh-button">🔄 Refresh</button>
`;
    const backButton = document.getElementById("backButton");
    const refreshButton = document.getElementById("refreshButton");

    if (!backButton || !refreshButton) {
        console.error("Buttons not found in DOM!");
        return;
    }

    backButton.addEventListener("click", goBack);
    refreshButton.addEventListener("click", () => manualRefresh(matchId, match));
    let c = 1;
    innings.forEach(inning => {
        let html = `<div class="innings-card">`;
        if (c == 1) {
            html += `
                    <div class="innings-header">
                        <span class="team-name">${match.team1.name}</span>
                        <span class="team-total">${match.team1.score}</span>
                    </div>
                    <h4>Batting</h4>
                    `;
        }
        else {
            html += `
                    <div class="innings-header">
                        <span class="team-name">${match.team2.name}</span>
                        <span class="team-total">${match.team2.score}</span>
                    </div>
                    <h4>Batting</h4>
                    `;
        }
        html += `<table><tr><th>Batter</th><th>R</th><th>B</th><th>4s</th><th>6s</th><th>SR</th></tr>`;

        inning.batting.forEach(player => {
            const name = player.status && (player.status.toLowerCase().includes("not out")||player.status.toLowerCase() === "batting")
                ? `${player.name} <span class="not-out-tag">(not out)</span>`
                : player.name;
            html += `<tr><td>${name}</td><td>${player.runs}</td><td>${player.balls}</td><td>${player.fours}</td><td>${player.sixes}</td><td>${player.sr}</td></tr>`;
        });
        html += `</table>`;

        html += `<p>Extras: ${inning.extras}</p>`;
        html += `<p>Total: ${inning.total}</p>`;
        html += `<p>Yet to Bat: ${inning.yetToBat.join(', ')}</p>`;

        html += `<h4>Bowling</h4>`;
        html += `<table><tr><th>Bowler</th><th>O</th><th>M</th><th>R</th><th>W</th><th>NB</th><th>WD</th><th>ECO</th></tr>`;
        inning.bowling.forEach(bowler => {
            html += `<tr><td>${bowler.name}</td><td>${bowler.overs}</td><td>${bowler.maidens}</td><td>${bowler.runs}</td><td>${bowler.wickets}</td><td>${bowler.nb}</td><td>${bowler.wd}</td><td>${bowler.eco}</td></tr>`;
        });
        html += `</table>`;

        html += `</div>`;
        scorecardDiv.insertAdjacentHTML('beforeend', html);
        c++;
    });
}

function manualRefresh(matchId, match) {
    showLoader(true);
    loadScorecard(matchId, match);
}

function goBack() {
    if (scorecardRefreshInterval) clearInterval(scorecardRefreshInterval);
    scorecardDiv.style.display = "none";
    matchesDiv.style.display = "block";
}
