/* =========================================================
   HEADER.JS – INIT HEADER BEHAVIOR (NO FETCH)
   ========================================================= */
document.addEventListener("DOMContentLoaded", () => {
    setupUserMenu();
    highlightActiveLink();
    applyDropdownModeOnResize();
    initSubmenuMobileHandlers();
});

/* =========================================================
   AUTH UTILS
   ========================================================= */
function getAuth() {
    if (localStorage.getItem("loggedIn") === "true") {
        return { store: localStorage, username: localStorage.getItem("username") };
    }
    if (sessionStorage.getItem("loggedIn") === "true") {
        return { store: sessionStorage, username: sessionStorage.getItem("username") };
    }
    return null;
}

/* =========================================================
   SETUP USER MENU + LOGIN FORM
   ========================================================= */
function setupUserMenu() {
    const loginBtn = document.getElementById("login-btn");
    const userBox  = document.getElementById("user-menu");
    const auth = getAuth();

    if (auth?.username) {
        loginBtn?.classList.add("d-none");
        userBox?.classList.remove("d-none");

        const nameSpan = document.querySelector("#userMenu .user-name");
        if (nameSpan) nameSpan.textContent = auth.username;

        const avatar = document.querySelector("#userMenu .user-avatar");
        if (avatar) avatar.textContent = auth.username[0].toUpperCase();

        // Badge: ưu tiên id, fallback theo vị trí trong user-menu
        let bellBadge = document.getElementById("bell-badge");
        let chatBadge = document.getElementById("chat-badge");
        if (!bellBadge || !chatBadge) {
            const badges = document.querySelectorAll("#user-menu .icon-btn .badge");
            bellBadge = bellBadge || badges[0];
            chatBadge = chatBadge || badges[1];
        }
        if (bellBadge) bellBadge.textContent = "3";
        if (chatBadge) chatBadge.textContent = "2";
    } else {
        userBox?.classList.add("d-none");
        loginBtn?.classList.remove("d-none");
    }

    // Đăng xuất → điều hướng về route Thymeleaf (nếu có nút rời rạc)
    document.getElementById("logout-btn")?.addEventListener("click", (e) => {
        e.preventDefault();
        localStorage.clear();
        sessionStorage.clear();
        window.location.href = "/user/login";
    });

    // Xử lý form login demo (nếu đang ở trang login render server-side)
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", (e) => {
            e.preventDefault();
            const u = document.getElementById("username")?.value.trim();
            const p = document.getElementById("password")?.value.trim();
            const remember = document.getElementById("rememberMe")?.checked;
            const err = document.getElementById("error-msg");

            const demoUser = "phuoc";
            const demoPass = "123456";

            if (u === demoUser && p === demoPass) {
                const store = remember ? localStorage : sessionStorage;
                store.setItem("loggedIn", "true");
                store.setItem("username", u);
                window.location.href = "/user/home";
            } else {
                if (err) {
                    err.textContent = "❌ Tên đăng nhập hoặc mật khẩu không đúng!";
                    err.classList.remove("d-none");
                } else {
                    alert("Tên đăng nhập hoặc mật khẩu không đúng!");
                }
            }
        });
    }
}

/* =========================================================
   HIGHLIGHT ACTIVE NAV LINK
   ========================================================= */
function highlightActiveLink() {
    const links = document.querySelectorAll(".navbar-nav .nav-link, .dropdown-item");
    if (!links.length) return;

    // VD: /user/lookup -> seg = "lookup"
    const path = window.location.pathname.replace(/\/+$/, "") || "/";
    const seg = path.split("/").filter(Boolean).pop() || "home";

    links.forEach(link => {
        const href = link.getAttribute("href") || "";
        // Chuẩn hoá để có pathname (bỏ query/hash)
        const a = document.createElement("a");
        a.href = href;
        const linkPath = (a.pathname || href).replace(/\/+$/, "") || "/";
        const linkSeg = linkPath.split("/").filter(Boolean).pop() || "";
        const linkSegClean = linkSeg.replace(/\.html$/i, "");

        const isHomeLink = /^\/?$|^\/user(\/(home|index))?$/.test(linkPath);
        const active =
            (linkSegClean && linkSegClean === seg) ||
            (isHomeLink && (seg === "home" || seg === "index" || path === "/user"));

        if (active) {
            link.classList.add("active");
            const parentDropdown = link.closest(".nav-item.dropdown");
            if (parentDropdown) {
                const parentToggle = parentDropdown.querySelector(".nav-link");
                // Trước khi set, loại bỏ active ở các nav-link level-1 khác để tránh nhiều gạch cùng lúc
                document.querySelectorAll(".navbar-nav > .nav-item > .nav-link.active")
                    .forEach(el => { if (el !== parentToggle) el.classList.remove("active"); });
                parentToggle?.classList.add("active");
            } else {
                // Nếu là link level-1, bỏ active ở các anh em khác
                const siblings = document.querySelectorAll(".navbar-nav > .nav-item > .nav-link.active");
                siblings.forEach(el => { if (el !== link) el.classList.remove("active"); });
            }
        }
    });
}

/* =========================================================
   HEADER NAV — Dropdown: Desktop=Hover, Mobile=Click (Bootstrap)
   ========================================================= */
const DESKTOP_MIN = 992;

function applyDropdownMode() {
    // Tác động các dropdown NAV chính, BỎ QUA #userMenu (user luôn click)
    const toggles = document.querySelectorAll(
        ".navbar .nav-item.dropdown > .dropdown-toggle:not(#userMenu)"
    );
    const isMobile = window.innerWidth < DESKTOP_MIN;

    toggles.forEach(t => {
        if (isMobile) {
            t.setAttribute("data-bs-toggle", "dropdown");
            t.setAttribute("data-bs-auto-close", "outside");
        } else {
            // Desktop: dùng hover CSS -> gỡ click để tránh xung đột
            t.removeAttribute("data-bs-toggle");
            t.removeAttribute("data-bs-auto-close");

            // Nếu còn .show thì đóng lại
            const menu = t.parentElement && t.parentElement.querySelector(".dropdown-menu");
            if (menu && menu.classList.contains("show") && typeof bootstrap !== "undefined") {
                const inst = bootstrap.Dropdown.getInstance(t) || new bootstrap.Dropdown(t);
                inst.hide();
                t.setAttribute("aria-expanded", "false");
            }
        }
    });

    // Đảm bảo user menu luôn click trên mọi kích thước
    const userToggle = document.querySelector("#userMenu");
    if (userToggle) {
        userToggle.setAttribute("data-bs-toggle", "dropdown");
        userToggle.setAttribute("data-bs-auto-close", "outside");
    }
}

function applyDropdownModeOnResize() {
    applyDropdownMode();
    let tmr;
    window.addEventListener("resize", () => {
        clearTimeout(tmr);
        tmr = setTimeout(applyDropdownMode, 150);
    });

    // Desktop: không nhảy trang với href="#" ở NAV (trừ #userMenu)
    document.addEventListener("click", (e) => {
        const link = e.target.closest(".navbar .nav-item.dropdown > .dropdown-toggle:not(#userMenu)");
        if (!link) return;
        const isMobile = window.innerWidth < DESKTOP_MIN;
        if (!isMobile && link.getAttribute("href") === "#") e.preventDefault();
    });

    // Mobile: click item thì đóng dropdown cha (bao gồm cả user)
    document.addEventListener("click", (e) => {
        const item = e.target.closest(".dropdown-menu .dropdown-item");
        if (!item) return;
        const isMobile = window.innerWidth < DESKTOP_MIN;
        if (!isMobile) return;
        const parentDropdown = item.closest(".nav-item.dropdown, .dropdown");
        const toggle = parentDropdown?.querySelector(".dropdown-toggle");
        if (toggle && typeof bootstrap !== "undefined") {
            const inst = bootstrap.Dropdown.getInstance(toggle) || new bootstrap.Dropdown(toggle);
            inst.hide();
            toggle.setAttribute("aria-expanded", "false");
        }
    });
}

/* =========================================================
   SUBMENU CẤP 2 – MOBILE CLICK (DESKTOP dùng hover CSS)
   ========================================================= */
function initSubmenuMobileHandlers() {
    // Click để mở submenu cấp 2 trên mobile
    document.querySelectorAll(".dropdown-submenu > a").forEach(function (a) {
        a.addEventListener("click", function (e) {
            const isMobile = window.innerWidth < DESKTOP_MIN;
            if (!isMobile) return; // desktop vẫn hover
            const menu = this.nextElementSibling;
            if (menu && menu.classList.contains("dropdown-menu")) {
                e.preventDefault();
                e.stopPropagation();
                menu.classList.toggle("show");
            }
        });
    });

    // Khi dropdown cha đóng -> đóng luôn submenu con
    document.querySelectorAll(".dropdown").forEach(function (dd) {
        dd.addEventListener("hide.bs.dropdown", function () {
            this.querySelectorAll(".dropdown-menu.show").forEach(function (m) {
                m.classList.remove("show");
            });
        });
    });
}
