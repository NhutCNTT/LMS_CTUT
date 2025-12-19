// assets/js/footer.js
document.addEventListener("DOMContentLoaded", () => {
  // Load footer.html vào placeholder
  fetch("components/library/footer.html")
    .then(res => res.text())
    .then(data => {
      document.getElementById("footer-placeholder").innerHTML = data;

      // Sau khi footer được chèn, có thể đổ dữ liệu thống kê
      updateFooterStats();
    })
    .catch(err => console.error("Không load được footer:", err));
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
