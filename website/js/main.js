(function () {
  "use strict";
  var S = window.SITE || {};
  var tokens = Object.assign({}, S.tokens || {});
  var digits = String(tokens.PHONE || "").replace(/\D/g, "");
  tokens.PHONE_TEL = digits.length === 10 ? "+1" + digits : digits.length === 11 ? "+" + digits : tokens.PHONE || "";

  function fill(str) {
    return str.replace(/\[\[([A-Z0-9_]+)\]\]/g, function (m, key) {
      return Object.prototype.hasOwnProperty.call(tokens, key) ? tokens[key] : m;
    });
  }

  function inject(root) {
    var walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
    var node, nodes = [];
    while ((node = walker.nextNode())) if (node.nodeValue.indexOf("[[") > -1) nodes.push(node);
    nodes.forEach(function (n) { n.nodeValue = fill(n.nodeValue); });
    var attrs = ["href", "content", "value", "placeholder", "aria-label"];
    root.querySelectorAll("[href],[content],[value],[placeholder],[aria-label]").forEach(function (el) {
      attrs.forEach(function (a) {
        var v = el.getAttribute(a);
        if (v && v.indexOf("[[") > -1) el.setAttribute(a, fill(v));
      });
    });
  }

  function track(name, params) {
    if (typeof window.gtag === "function") window.gtag("event", name, params || {});
  }

  function analytics() {
    var id = S.GA4_MEASUREMENT_ID;
    if (!id || id.indexOf("[[") > -1) return;
    window.dataLayer = window.dataLayer || [];
    window.gtag = function () { window.dataLayer.push(arguments); };
    window.gtag("js", new Date());
    window.gtag("config", id);
    var tag = document.createElement("script");
    tag.async = true;
    tag.src = "https://www.googletagmanager.com/gtag/js?id=" + encodeURIComponent(id);
    document.head.appendChild(tag);
    document.addEventListener("click", function (e) {
      var a = e.target.closest && e.target.closest("a[href^='tel:']");
      if (a) track("phone_call", { page_path: location.pathname, link_text: text(a) });
    });
  }

  function nav() {
    var toggle = document.querySelector(".nav-toggle");
    var menu = document.getElementById("nav");
    if (toggle && menu) {
      toggle.addEventListener("click", function () {
        var open = menu.classList.toggle("open");
        toggle.setAttribute("aria-expanded", open ? "true" : "false");
      });
    }
    var here = location.pathname.replace(/index\.html$/, "");
    document.querySelectorAll(".nav a").forEach(function (a) {
      var href = a.getAttribute("href");
      if (href.indexOf("#") === -1 && href === here) a.setAttribute("aria-current", "page");
    });
  }

  function forms() {
    document.querySelectorAll("form[data-form]").forEach(function (form) {
      form.addEventListener("submit", function (e) {
        e.preventDefault();
        var button = form.querySelector("[type=submit]");
        var data = { access_key: S.FORM_ACCESS_KEY, subject: form.getAttribute("data-form") + " from " + S.name + " website", from_name: S.name + " website" };
        new FormData(form).forEach(function (v, k) { data[k] = v; });
        if (data.botcheck) return;
        button.disabled = true;
        button.textContent = "Sending…";
        fetch("https://api.web3forms.com/submit", {
          method: "POST",
          headers: { "Content-Type": "application/json", Accept: "application/json" },
          body: JSON.stringify(data)
        }).then(function (r) { return r.json(); }).then(function (res) {
          if (!res.success) throw new Error(res.message || "Form rejected");
          var msg = document.createElement("p");
          msg.className = "form-msg";
          msg.setAttribute("role", "status");
          msg.textContent = "Got it, we'll call you within the hour.";
          track("generate_lead", { page_path: location.pathname, service: data.service || "", form: form.getAttribute("data-form") });
          form.replaceWith(msg);
          msg.focus();
        }).catch(function () {
          var err = form.querySelector(".form-error") || document.createElement("p");
          err.className = "form-msg error form-error";
          err.setAttribute("role", "alert");
          err.textContent = "That didn't send. Call " + tokens.PHONE + " and we'll take it by phone.";
          if (!err.parentNode) form.appendChild(err);
          button.disabled = false;
          button.textContent = button.getAttribute("data-label") || "Request a quote";
        });
      });
    });
  }

  function anchors() {
    function jump() {
      var id = location.hash.slice(1);
      var target = id && document.getElementById(id);
      if (!target) return;
      document.documentElement.classList.add("anchoring");
      target.scrollIntoView();
      requestAnimationFrame(function () {
        requestAnimationFrame(function () { document.documentElement.classList.remove("anchoring"); });
      });
    }
    window.addEventListener("load", jump);
    window.addEventListener("hashchange", jump);
  }

  function calculator() {
    var form = document.querySelector("form[data-calculator]");
    if (!form) return;
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      var out = form.querySelector(".calc-out");
      var tank = parseInt(form.elements.tank.value, 10);
      var people = parseInt(form.elements.people.value, 10);
      var disposal = form.elements.disposal.checked;
      var last = form.elements.last.value ? new Date(form.elements.last.value + "T00:00:00") : null;
      if (!(tank >= 250 && tank <= 5000)) { out.innerHTML = "<p>Enter a tank size between 250 and 5,000 gallons.</p>"; return; }
      if (!(people >= 1 && people <= 20)) { out.innerHTML = "<p>Enter a household size between 1 and 20.</p>"; return; }
      var years = 0.013 * tank / people - 0.65;
      if (disposal) years *= 2 / 3;
      years = Math.max(0.5, years);
      var recommended = Math.round(Math.min(5, years) * 10) / 10;
      var html = '<p class="big">Pump about every ' + recommended + " years</p>";
      if (last && !isNaN(last.getTime())) {
        var due = new Date(last.getTime());
        due.setMonth(due.getMonth() + Math.round(recommended * 12));
        var text = due.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
        html += due < new Date() ? "<p><strong>Overdue.</strong> Service was due around " + text + ".</p>" : "<p>Next service due around <strong>" + text + "</strong>.</p>";
      }
      html += years > 5
        ? "<p>Your tank could go longer, but we recommend service at least every five years so problems are caught while they are still cheap.</p>"
        : "<p>Based on the Penn State Extension pumping table. Call us for a free assessment.</p>";
      out.innerHTML = html;
    });
  }

  function text(el) { return el ? el.textContent.replace(/\s+/g, " ").trim() : ""; }

  function schema() {
    var body = document.body;
    var types = (body.getAttribute("data-schema") || "").split(",").map(function (s) { return s.trim(); }).filter(Boolean);
    var url = S.url || location.origin;
    var canonical = document.querySelector("link[rel=canonical]");
    var pageUrl = canonical ? canonical.href : location.href;
    var cityName = body.getAttribute("data-area");
    var areaServed = (S.cities || []).filter(function (c) { return !cityName || c.name === cityName; })
      .map(function (c) { return { "@type": "City", name: c.name + ", WA" }; });
    var business = {
      "@type": "LocalBusiness",
      "@id": url + "/#business",
      name: S.name,
      legalName: S.legalName,
      url: url,
      telephone: tokens.PHONE,
      email: tokens.EMAIL,
      image: url + "/assets/og.png",
      priceRange: "$$",
      address: { "@type": "PostalAddress", streetAddress: tokens.STREET, addressLocality: S.address.city, addressRegion: S.address.region, addressCountry: S.address.country },
      areaServed: (S.cities || []).map(function (c) { return { "@type": "City", name: c.name + ", WA" }; }),
      openingHoursSpecification: S.openingHours,
      sameAs: [tokens.GOOGLE_BUSINESS_PROFILE_URL].concat(S.sameAs || []).filter(function (u) { return u && u.indexOf("[[") === -1; })
    };
    var graph = [business];
    if (types.indexOf("Service") > -1) {
      graph.push({
        "@type": "Service",
        "@id": pageUrl + "#service",
        name: body.getAttribute("data-service-name") || text(document.querySelector("h1")),
        description: body.getAttribute("data-service-desc") || text(document.querySelector("meta[name=description]")),
        serviceType: body.getAttribute("data-service-type") || "Septic service",
        provider: { "@id": url + "/#business" },
        areaServed: areaServed,
        url: pageUrl
      });
    }
    if (types.indexOf("FAQPage") > -1) {
      var qa = [];
      document.querySelectorAll(".faq details").forEach(function (d) {
        qa.push({ "@type": "Question", name: text(d.querySelector("summary")), acceptedAnswer: { "@type": "Answer", text: text(d.querySelector(".answer")) } });
      });
      if (qa.length) graph.push({ "@type": "FAQPage", "@id": pageUrl + "#faq", mainEntity: qa });
    }
    var script = document.createElement("script");
    script.type = "application/ld+json";
    script.textContent = JSON.stringify({ "@context": "https://schema.org", "@graph": graph });
    document.head.appendChild(script);
  }

  inject(document.documentElement);
  analytics();
  nav();
  anchors();
  forms();
  calculator();
  schema();
})();
