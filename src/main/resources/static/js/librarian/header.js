// assets/js/header.js
(async function mountHeader() {
  const host = document.getElementById("header-placeholder");
  if (!host) return;

  try {
    const res = await fetch("components/library/header.html", { cache: "no-cache" });
    host.innerHTML = await res.text();
  } catch (err) {
    console.error("Không load được header:", err);
    return;
  }

  // Hotkey Ctrl/⌘+K -> focus search
  document.addEventListener("keydown", (e) => {
    const isMac = navigator.platform.toUpperCase().includes("MAC");
    if ((isMac ? e.metaKey : e.ctrlKey) && e.key.toLowerCase() === "k") {
      e.preventDefault();
      document.getElementById("globalSearch")?.focus();
    }
  });

  // Toggle sidebar
  const btn = document.getElementById("btnToggleSidebar");
  const sidebar = document.getElementById("appSidebar") || document.querySelector(".sidebar");
  btn?.addEventListener("click", () => {
    sidebar?.classList.toggle("is-collapsed");
    document.documentElement.classList.toggle("sidebar-collapsed");
  });
})();
