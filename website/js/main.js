/* Shared behaviour: fills business details from config.js, renders the
 * service catalog, handles the mobile nav and the pumping calculator. */
(function () {
  "use strict";

  var cfg = window.SITE_CONFIG || { business: {}, services: [] };
  var biz = cfg.business;

  function formatPhone(digits) {
    var d = String(digits || "").replace(/\D/g, "");
    if (d.length === 11 && d.charAt(0) === "1") d = d.slice(1);
    if (d.length !== 10) return digits || "";
    return "(" + d.slice(0, 3) + ") " + d.slice(3, 6) + "-" + d.slice(6);
  }

  function normalizePhone(raw) {
    var d = String(raw || "").replace(/\D/g, "");
    if (d.length === 11 && d.charAt(0) === "1") d = d.slice(1);
    return d.length === 10 ? d : null;
  }

  function getPath(obj, path) {
    return path.split(".").reduce(function (o, k) { return o == null ? o : o[k]; }, obj);
  }

  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c];
    });
  }

  function applyConfig() {
    document.querySelectorAll("[data-config]").forEach(function (el) {
      var value = getPath(cfg, el.getAttribute("data-config"));
      if (value != null) el.textContent = value;
    });
    document.querySelectorAll("[data-tel]").forEach(function (el) {
      el.setAttribute("href", "tel:" + biz.phone);
      if (el.getAttribute("data-tel") !== "keep-text") {
        el.textContent = (el.getAttribute("data-tel") === "prefix" ? "Call " : "") + formatPhone(biz.phone);
      }
    });
    document.querySelectorAll("[data-mailto]").forEach(function (el) {
      el.setAttribute("href", "mailto:" + biz.email);
      el.textContent = biz.email;
    });
    document.querySelectorAll("[data-hours]").forEach(function (el) {
      el.innerHTML = (biz.hours || []).map(function (h) {
        return "<li><span>" + escapeHtml(h.label) + "</span><span>" + escapeHtml(h.value) + "</span></li>";
      }).join("");
    });
    document.querySelectorAll("[data-towns]").forEach(function (el) {
      el.innerHTML = (biz.serviceTowns || []).map(function (t) {
        return "<li>" + escapeHtml(t) + "</li>";
      }).join("");
    });
    document.querySelectorAll("[data-year]").forEach(function (el) {
      el.textContent = String(new Date().getFullYear());
    });
    document.title = document.title.replace("{{name}}", biz.name || "");
  }

  function renderServices() {
    document.querySelectorAll("[data-services]").forEach(function (el) {
      var mode = el.getAttribute("data-services");
      el.innerHTML = cfg.services.map(function (s) {
        var badge = s.emergency ? '<span class="badge">Emergency available</span>' : "";
        if (mode === "detailed") {
          return (
            '<article class="service-detail" id="' + escapeHtml(s.id) + '">' +
            "<h3>" + escapeHtml(s.name) + "</h3>" + badge +
            '<p class="lead">' + escapeHtml(s.summary) + "</p>" +
            "<p>" + escapeHtml(s.details) + "</p>" +
            '<a class="btn btn-primary" href="contact.html?service=' + encodeURIComponent(s.id) + '">Request this service</a>' +
            "</article>"
          );
        }
        return (
          '<a class="card service-card" href="services.html#' + escapeHtml(s.id) + '">' +
          "<h3>" + escapeHtml(s.name) + "</h3>" +
          "<p>" + escapeHtml(s.summary) + "</p>" + badge +
          "</a>"
        );
      }).join("");
    });
  }

  function setUpNav() {
    var toggle = document.querySelector("[data-nav-toggle]");
    var nav = document.getElementById("site-nav");
    if (toggle && nav) {
      toggle.addEventListener("click", function () {
        var open = nav.classList.toggle("open");
        toggle.setAttribute("aria-expanded", open ? "true" : "false");
      });
    }
    var page = (location.pathname.split("/").pop() || "index.html").replace(/\.html$/, "") || "index";
    document.querySelectorAll("#site-nav a[href]").forEach(function (a) {
      var target = a.getAttribute("href").replace(/\.html$/, "");
      if (target === page || (page === "index" && target === "index")) a.classList.add("active");
    });
  }

  /* Pumping interval estimate, based on the Penn State Extension table. */
  function estimatePumping(tankGallons, occupants, hasDisposal) {
    var years = 0.013 * tankGallons / occupants - 0.65;
    if (hasDisposal) years *= 2 / 3;
    years = Math.max(0.5, years);
    var recommended = Math.min(5, Math.max(0.5, years));
    return {
      estimatedYears: Math.round(years * 10) / 10,
      recommendedYears: Math.round(recommended * 10) / 10,
    };
  }

  function addMonths(date, months) {
    var d = new Date(date.getTime());
    d.setMonth(d.getMonth() + months);
    return d;
  }

  function setUpCalculator() {
    var form = document.querySelector("[data-calculator]");
    if (!form) return;
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      var tank = parseInt(form.elements.tank.value, 10);
      var people = parseInt(form.elements.people.value, 10);
      var disposal = form.elements.disposal.checked;
      var last = form.elements.last && form.elements.last.value ? new Date(form.elements.last.value + "T00:00:00") : null;
      var out = form.querySelector("[data-calc-result]");
      if (!(tank >= 250 && tank <= 5000)) {
        out.innerHTML = '<p class="error">Enter a tank size between 250 and 5,000 gallons.</p>';
        return;
      }
      if (!(people >= 1 && people <= 20)) {
        out.innerHTML = '<p class="error">Enter a household size between 1 and 20.</p>';
        return;
      }
      var r = estimatePumping(tank, people, disposal);
      var note = r.estimatedYears > r.recommendedYears
        ? "Your tank could go roughly " + r.estimatedYears + " years, but we recommend service at least every 5 years to catch problems early."
        : "Based on the Penn State Extension pumping guide. Call us for a free assessment.";
      var dueHtml = "";
      if (last && !isNaN(last.getTime())) {
        var due = addMonths(last, Math.round(r.recommendedYears * 12));
        var dueText = due.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
        dueHtml = due < new Date()
          ? '<p class="result" style="color:var(--emergency)">Overdue: service was due around ' + dueText + "</p>"
          : '<p class="result">Next service due around <strong>' + dueText + "</strong></p>";
      }
      out.innerHTML =
        '<p class="result">Pump about every <strong>' + r.recommendedYears + " years</strong></p>" +
        dueHtml +
        "<p>" + escapeHtml(note) + "</p>" +
        '<a class="btn btn-primary" href="contact.html?service=pumping">Schedule pumping</a>';
    });
  }

  /* Progressive web app: offline cache + "Install app" button. */
  function setUpPwa() {
    if ("serviceWorker" in navigator && location.protocol !== "file:") {
      window.addEventListener("load", function () {
        navigator.serviceWorker.register("sw.js").catch(function (err) { console.warn("Service worker not registered", err); });
      });
    }
    var deferredPrompt = null;
    var buttons = document.querySelectorAll("[data-install]");
    window.addEventListener("beforeinstallprompt", function (e) {
      e.preventDefault();
      deferredPrompt = e;
      buttons.forEach(function (b) { b.hidden = false; });
    });
    window.addEventListener("appinstalled", function () {
      deferredPrompt = null;
      buttons.forEach(function (b) { b.hidden = true; });
    });
    buttons.forEach(function (b) {
      b.addEventListener("click", function () {
        if (!deferredPrompt) return;
        deferredPrompt.prompt();
        deferredPrompt.userChoice.then(function () { deferredPrompt = null; b.hidden = true; });
      });
    });
  }

  window.KS = {
    config: cfg,
    formatPhone: formatPhone,
    normalizePhone: normalizePhone,
    escapeHtml: escapeHtml,
    estimatePumping: estimatePumping,
  };

  document.addEventListener("DOMContentLoaded", function () {
    applyConfig();
    renderServices();
    setUpNav();
    setUpCalculator();
    setUpPwa();
  });
})();
