// ACTIVE MENU (NO FETCH). Có thể gộp vào header.js để đỡ trùng.
document.addEventListener("DOMContentLoaded", () => {
    try {
        const links = document.querySelectorAll(".navbar-nav .nav-link, .dropdown-item");
        if (!links.length) return;

        const path = window.location.pathname.replace(/\/+$/, "");
        const seg = path.split("/").filter(Boolean).pop() || "home";

        links.forEach(link => {
            const href = link.getAttribute("href") || "";
            const hrefSeg = href.split("/").filter(Boolean).pop() || "";
            const hrefClean = hrefSeg.replace(/\.html$/i, "");
            const isHomeLink = href === "/" || /\/user(\/(home|index))?$/.test(href);

            if ((hrefClean && hrefClean === seg) || (isHomeLink && (seg === "home" || seg === "index"))) {
                link.classList.add("active");
                const parentDropdown = link.closest(".dropdown");
                if (parentDropdown) parentDropdown.querySelector(".nav-link")?.classList.add("active");
            }
        });
    } catch (e) {
        console.error("active-menu init error:", e);
    }
});
