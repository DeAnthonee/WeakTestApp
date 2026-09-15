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
  nav();
  forms();
  schema();
})();
