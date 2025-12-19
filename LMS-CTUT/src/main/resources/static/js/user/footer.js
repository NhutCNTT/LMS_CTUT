// /js/user/footer.js
document.addEventListener("DOMContentLoaded", () => {
    // Load footer.html vào placeholder (USER)
    fetch("/components/user/footer.html")
        .then(res => res.text())
        .then(html => {
            const host = document.getElementById("footer-placeholder");
            if (!host) return;
            host.innerHTML = html;

            // 🔹 Đánh dấu đã load footer
            document.body.dataset.footerLoaded = "true";

            // Gọi hàm từ layout.js
            if (typeof checkLayoutReady === "function") {
                checkLayoutReady();
            }
        })
        .catch(err => console.error("Lỗi khi load footer:", err));
});

// Hàm giả lập đổ dữ liệu thống kê (có thể thay bằng API thật)
function updateFooterStats() {
    const stats = {
        books: "21,554",
        shelves: "37,818",
        periodicals: "2",
        digital: "6,560",
        visits: "41,866"
    };

    document.getElementById("stat-books").textContent = stats.books;
    document.getElementById("stat-shelves").textContent = stats.shelves;
    document.getElementById("stat-periodicals").textContent = stats.periodicals;
    document.getElementById("stat-digital").textContent = stats.digital;
    document.getElementById("stat-visits").textContent = stats.visits;
}
