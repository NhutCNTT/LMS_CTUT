// assets/js/sidebar.js
(async function mountSidebar() {
  const host = document.getElementById("sidebar-placeholder");
  if (!host) return;

  try {
    const res = await fetch("components/library/sidebar.html", { cache: "no-cache" });
    host.innerHTML = await res.text();

    // Sau khi load xong mới set active
    setActiveSidebarLink();
  } catch (err) {
    console.error("Không load được sidebar:", err);
  }
})();

function setActiveSidebarLink() {
  const current = getCurrentPageId();
  document.querySelectorAll(".sidebar .nav-link.active").forEach(a => a.classList.remove("active"));
  document.querySelectorAll(".sidebar .nav-link").forEach(a => {
    const hrefFile = (a.getAttribute("href") || "").split("/").pop() || "dashboard.html";
    if (hrefFile === current) {
      a.classList.add("active");
      a.setAttribute("aria-current", "page");
    }
  });
}

/* Helpers */
function getCurrentPageId() {
  const manual = document.body.getAttribute("data-page");
  if (manual) return manual;
  const parts = location.pathname.split("/").filter(Boolean);
  return parts.pop() || "dashboard.html";
}
