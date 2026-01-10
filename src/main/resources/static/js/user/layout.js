// layout.js — tải header/footer an toàn, KHÔNG bao giờ để trang trắng

// (1) KHỞI ĐỘNG: có thể che tạm, nhưng sẽ có timeout gỡ để không kẹt
document.documentElement.classList.add("loading");

// (2) Phao an toàn: nếu 1.2s vẫn chưa xong, cứ hiện trang
const bail = setTimeout(() => {
    document.body.dataset.headerLoaded = "true";
    document.body.dataset.footerLoaded = "true";
    showPage();
}, 1200);

function showPage() {
    // Gỡ trạng thái loading để hiện trang
    document.documentElement.classList.remove("loading");
}

// ---- TẢI THÀNH PHẦN ----
async function fetchInto(selector, url) {
    const el = document.querySelector(selector);
    if (!el) return false;

    try {
        const res = await fetch(url, { cache: "no-store" });
        if (!res.ok) throw new Error(`${url} -> ${res.status}`);
        el.innerHTML = await res.text();
        return true;
    } catch (e) {
        console.warn("[FETCH FAIL]", url, e);
        // Fallback nhỏ để không rỗng nếu fetch hỏng
        if (selector === "#header-placeholder") {
            el.innerHTML = `
        <header class="site-header">
          <div class="wrap"><a class="brand" href="/">MyLibrary</a></div>
        </header>`;
        }
        if (selector === "#footer-placeholder") {
            const y = new Date().getFullYear();
            el.innerHTML = `
        <footer class="site-footer">
          <div class="wrap"><p>&copy; ${y} CTUT Library — Fallback</p></div>
        </footer>`;
        }
        return false;
    }
}

// ---- THEO DÕI CỜ & HIỆN TRANG ----
function checkLayoutReady() {
    if (
        document.body.dataset.headerLoaded === "true" &&
        document.body.dataset.footerLoaded === "true"
    ) {
        clearTimeout(bail);
        showPage(); // luôn hiện, không dùng body.ready nữa
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    // khởi tạo cờ
    document.body.dataset.headerLoaded = "false";
    document.body.dataset.footerLoaded = "false";

    // tải song song
    const [hOk, fOk] = await Promise.allSettled([
        fetchInto("#header-placeholder", "/components/user/header.html"),
        fetchInto("#footer-placeholder", "/components/user/footer.html"),
    ]);

    // set cờ bất kể ok/fail (vì đã có fallback => không để trang rỗng)
    document.body.dataset.headerLoaded = "true";
    document.body.dataset.footerLoaded = "true";
    checkLayoutReady();
});
