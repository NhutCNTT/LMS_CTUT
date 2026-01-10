// /js/user/announcement.js  (khối USER)
document.addEventListener("DOMContentLoaded", () => {
    // Dummy data — sau này thay bằng dữ liệu backend
    const dummyData = [
        { title: "Thông báo 1", date: "26/09/2025", content: "Chi tiết thông báo 1" },
        { title: "Thông báo 2", date: "27/09/2025", content: "Chi tiết thông báo 2" },
        { title: "Thông báo 3", date: "28/09/2025", content: "Chi tiết thông báo 3" }
    ];

    const container = document.getElementById("announcement-results");
    const searchInput = document.getElementById("searchInput");
    const searchBtn = document.getElementById("searchBtn");

    // --- Utils ---
    // Parse dd/MM/yyyy -> Date
    function parseVNDate(s) {
        const [d, m, y] = (s || "").split("/").map(Number);
        return new Date(y || 0, (m || 1) - 1, d || 1);
    }

    // Debounce helper
    function debounce(fn, delay = 250) {
        let t;
        return (...args) => {
            clearTimeout(t);
            t = setTimeout(() => fn(...args), delay);
        };
    }

    // --- Render ---
    function render(data) {
        if (!Array.isArray(data) || data.length === 0) {
            container.innerHTML = `
        <div class="col-12">
          <div class="text-center py-4 text-muted">Không có thông báo nào phù hợp.</div>
        </div>`;
            return;
        }

        // Sort theo ngày mới → cũ
        const sorted = [...data].sort((a, b) => parseVNDate(b.date) - parseVNDate(a.date));

        container.innerHTML = sorted.map(item => `
      <div class="col-md-6 col-lg-4 mb-3">
        <div class="notice-card h-100 p-3 border rounded">
          <h5 class="mb-1">${item.title}</h5>
          <p class="meta small text-muted mb-2">${item.date}</p>
          <p class="mb-3">${item.content}</p>
          <a href="#" class="btn btn-sm btn-login" aria-label="Xem chi tiết ${item.title}">Chi tiết</a>
        </div>
      </div>
    `).join("");
    }

    // --- Search logic ---
    function doSearch() {
        const q = (searchInput.value || "").trim().toLowerCase();
        if (!q) {
            render(dummyData);
            return;
        }
        const results = dummyData.filter(x =>
            (x.title || "").toLowerCase().includes(q) ||
            (x.content || "").toLowerCase().includes(q)
        );
        render(results);
    }

    const doSearchDebounced = debounce(doSearch, 250);

    // Render ban đầu
    render(dummyData);

    // Sự kiện search
    searchBtn?.addEventListener("click", doSearch);
    searchInput?.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            doSearch();
        }
    });
    searchInput?.addEventListener("input", doSearchDebounced);
});
