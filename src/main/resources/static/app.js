// The prayers we show, in order. "key" matches the field name in our API's JSON.
const PRAYERS = [
  { key: "fajr", name: "Fajr" },
  { key: "sunrise", name: "Sunrise" },
  { key: "dhuhr", name: "Dhuhr" },
  { key: "asr", name: "Asr" },
  { key: "maghrib", name: "Maghrib" },
  { key: "isha", name: "Isha" },
];

const RAMADAN_PREVIEW_DATE = "2026-03-01";

let current = null;     // the latest data from our API
let isPreview = false;  // true when looking at a date other than today

// ---------- Helpers ----------

// "16:43" -> "4:43 PM"
function to12Hour(time24) {
  const [h, m] = time24.split(":").map(Number);
  const suffix = h >= 12 ? "PM" : "AM";
  const hour12 = h % 12 === 0 ? 12 : h % 12;
  return `${hour12}:${String(m).padStart(2, "0")} ${suffix}`;
}

// "16:43" -> a Date object for today at 4:43 PM (or tomorrow if addDays = 1)
function timeToday(time24, addDays = 0) {
  const [h, m] = time24.split(":").map(Number);
  const d = new Date();
  d.setDate(d.getDate() + addDays);
  d.setHours(h, m, 0, 0);
  return d;
}

// milliseconds -> "2h 05m 09s"
function formatCountdown(ms) {
  const total = Math.max(0, Math.floor(ms / 1000));
  const h = Math.floor(total / 3600);
  const m = Math.floor((total % 3600) / 60);
  const s = total % 60;
  return `${h}h ${String(m).padStart(2, "0")}m ${String(s).padStart(2, "0")}s`;
}

// Today's date as "YYYY-MM-DD" in the user's own time zone
function todayIso() {
  const d = new Date();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${month}-${day}`;
}

// The next prayer after right now (skips Sunrise, which is not a prayer)
function findNextPrayer(data) {
  const now = new Date();
  for (const p of PRAYERS) {
    if (p.key === "sunrise") continue;
    const t = timeToday(data[p.key]);
    if (t > now) return { key: p.key, name: p.name, time: t };
  }
  // After Isha: the next prayer is tomorrow's Fajr
  return { key: "fajr", name: "Fajr", time: timeToday(data.fajr, 1) };
}

function showMessage(text) {
  document.getElementById("message").textContent = text;
}

// ---------- Loading data from our Spring Boot API ----------

async function loadTimes() {
  const city = document.getElementById("city").value.trim() || "Nashville";
  const country = document.getElementById("country").value.trim() || "US";
  const date = document.getElementById("date").value; // "" or "YYYY-MM-DD"

  const params = new URLSearchParams({ city, country });
  if (date) params.append("date", date);

  showMessage("Loading...");
  try {
    const response = await fetch(`/api/prayer-times?${params}`);
    if (!response.ok) throw new Error(`Server returned ${response.status}`);
    current = await response.json();
    isPreview = date !== "" && date !== todayIso();
    render();
    tick();
    showMessage("");
  } catch (error) {
    console.error(error);
    showMessage("Could not load prayer times. Check the city and country and try again.");
  }
}

// ---------- Drawing the page ----------

function render() {
  const d = current;
  const [hDay, hMonthNum, hYear] = d.hijriDate.split("-");

  document.getElementById("location").textContent = `${d.city}, ${d.country}`;
  document.getElementById("gregorian").textContent = d.date;
  document.getElementById("hijri").textContent = `${Number(hDay)} ${d.hijriMonth} ${hYear} AH`;

  // Ramadan mode: show or hide the Ramadan card
  const ramadanSection = document.getElementById("ramadan-section");
  ramadanSection.hidden = !d.ramadan;
  if (d.ramadan) {
    document.getElementById("ramadan-day").textContent = `Ramadan Day ${d.ramadanDay}`;
    document.getElementById("suhoor").textContent = to12Hour(d.suhoorEnds);
    document.getElementById("iftar").textContent = to12Hour(d.iftar);
  }

  // Prayer times table
  const tbody = document.getElementById("times-body");
  tbody.innerHTML = "";
  for (const p of PRAYERS) {
    const row = document.createElement("tr");
    row.dataset.key = p.key;
    const nameCell = document.createElement("td");
    nameCell.textContent = p.name;
    const timeCell = document.createElement("td");
    timeCell.textContent = to12Hour(d[p.key]);
    row.append(nameCell, timeCell);
    tbody.append(row);
  }
}

// Runs every second to update countdowns and the highlighted row
function tick() {
  if (!current) return;

  const nextLabel = document.getElementById("next-label");
  const nextCountdown = document.getElementById("next-countdown");
  const next = isPreview ? null : findNextPrayer(current);

  if (isPreview) {
    nextLabel.textContent = "Preview mode";
    nextCountdown.textContent = "Countdowns only run for today";
  } else {
    nextLabel.textContent = `Next: ${next.name} at ${to12Hour(current[next.key])}`;
    nextCountdown.textContent = formatCountdown(next.time - new Date());
  }

  for (const row of document.querySelectorAll("#times-body tr")) {
    row.classList.toggle("next", next !== null && row.dataset.key === next.key);
  }

  if (current.ramadan) {
    const fastLabel = document.getElementById("fast-label");
    const fastCountdown = document.getElementById("fast-countdown");
    if (isPreview) {
      fastLabel.textContent = "Preview of a Ramadan day";
      fastCountdown.textContent = "";
    } else {
      const now = new Date();
      const suhoorEnds = timeToday(current.suhoorEnds);
      const iftar = timeToday(current.iftar);
      let label;
      let target;
      if (now < suhoorEnds) {
        label = "Suhoor ends in";
        target = suhoorEnds;
      } else if (now < iftar) {
        label = "Iftar in";
        target = iftar;
      } else {
        label = "Suhoor ends in";
        target = timeToday(current.suhoorEnds, 1);
      }
      fastLabel.textContent = label;
      fastCountdown.textContent = formatCountdown(target - now);
    }
  }
}

// ---------- Buttons and start-up ----------

document.getElementById("search-form").addEventListener("submit", (event) => {
  event.preventDefault();
  loadTimes();
});

document.getElementById("today-btn").addEventListener("click", () => {
  document.getElementById("date").value = "";
  loadTimes();
});

document.getElementById("ramadan-demo-btn").addEventListener("click", () => {
  document.getElementById("date").value = RAMADAN_PREVIEW_DATE;
  loadTimes();
});

loadTimes();
setInterval(tick, 1000);