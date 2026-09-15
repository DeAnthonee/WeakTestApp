/* Contact / quote request form. Saves to Firestore when SITE_CONFIG.firebase
 * is set; otherwise opens the visitor's email app with the request filled in. */
(function () {
  "use strict";

  var FIREBASE_SDK = "https://www.gstatic.com/firebasejs/11.1.0/";
  var COLLECTION = "serviceRequests";

  var form = document.getElementById("quote-form");
  if (!form) return;

  var KS = window.KS;
  var cfg = KS.config;
  var biz = cfg.business;

  function fillServiceOptions() {
    var select = form.elements.service;
    cfg.services.forEach(function (s) {
      var opt = document.createElement("option");
      opt.value = s.id;
      opt.textContent = s.name;
      select.appendChild(opt);
    });
    var preselected = new URLSearchParams(location.search).get("service");
    if (preselected && cfg.services.some(function (s) { return s.id === preselected; })) {
      select.value = preselected;
      if (preselected === "emergency") form.elements.emergency.checked = true;
    }
  }

  function serviceName(id) {
    var s = cfg.services.filter(function (x) { return x.id === id; })[0];
    return s ? s.name : id;
  }

  function readForm() {
    var f = form.elements;
    return {
      customerName: f.name.value.trim(),
      phone: f.phone.value.trim(),
      email: f.email.value.trim(),
      address: f.address.value.trim(),
      serviceId: f.service.value,
      preferredDate: f.date.value.trim(),
      notes: f.notes.value.trim(),
      isEmergency: f.emergency.checked,
    };
  }

    function validate(r) {
    var errors = {};
    if (r.customerName.length < 2) errors.name = "Please enter your name.";
    if (!KS.normalizePhone(r.phone)) errors.phone = "Please enter a valid 10-digit phone number.";
    if (r.email && !/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(r.email)) {
      errors.email = "Please enter a valid email address.";
    }
    if (r.address.length < 5) errors.address = "Please enter the service address.";
    if (!r.serviceId) errors.service = "Please choose a service.";
    return errors;
  }

  function showErrors(errors) {
    form.querySelectorAll("[data-error-for]").forEach(function (el) {
      var key = el.getAttribute("data-error-for");
      el.textContent = errors[key] || "";
      var field = form.elements[key];
      if (field) field.classList.toggle("invalid", !!errors[key]);
    });
  }

  function setBusy(busy) {
    var button = form.querySelector("button[type=submit]");
    button.disabled = busy;
    button.textContent = busy ? "Sending…" : "Send request";
  }

  function showSuccess(message) {
    form.hidden = true;
    var panel = document.getElementById("quote-success");
    panel.querySelector("[data-success-message]").textContent = message;
    panel.hidden = false;
    panel.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  function showFailure(message) {
    var el = form.querySelector("[data-form-error]");
    el.textContent = message + " Please call us at " + KS.formatPhone(biz.phone) + ".";
  }

  function toDocument(r) {
    return {
      customerName: r.customerName,
      phone: KS.normalizePhone(r.phone),
      email: r.email,
      address: r.address,
      serviceId: r.serviceId,
      serviceName: serviceName(r.serviceId),
      preferredDate: r.preferredDate,
      notes: r.notes,
      isEmergency: r.isEmergency,
      source: "web",
      status: "new",
      createdAt: null, // replaced with serverTimestamp() below
    };
  }

  function submitToFirestore(r) {
    return Promise.all([
      import(FIREBASE_SDK + "firebase-app.js"),
      import(FIREBASE_SDK + "firebase-firestore.js"),
    ]).then(function (mods) {
      var appMod = mods[0];
      var fs = mods[1];
      var app = appMod.getApps().length ? appMod.getApp() : appMod.initializeApp(cfg.firebase);
      var db = fs.getFirestore(app);
      var doc = toDocument(r);
      doc.createdAt = fs.serverTimestamp();
      return fs.addDoc(fs.collection(db, COLLECTION), doc);
    });
  }

  function submitByEmail(r) {
    var lines = [
      "New service request from the " + biz.name + " website",
      "",
      "Name: " + r.customerName,
      "Phone: " + KS.formatPhone(r.phone),
    ];
    if (r.email) lines.push("Email: " + r.email);
    lines.push("Address: " + r.address);
    lines.push("Service: " + serviceName(r.serviceId));
    if (r.preferredDate) lines.push("Preferred date: " + r.preferredDate);
    lines.push("Emergency: " + (r.isEmergency ? "YES" : "No"));
    if (r.notes) lines.push("", "Notes:", r.notes);

    var subject = (r.isEmergency ? "EMERGENCY - " : "") + "Service request: " + serviceName(r.serviceId);
    window.location.href =
      "mailto:" + encodeURIComponent(biz.email) +
      "?subject=" + encodeURIComponent(subject) +
      "&body=" + encodeURIComponent(lines.join("\n"));
  }

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    var request = readForm();
    var errors = validate(request);
    showErrors(errors);
    if (Object.keys(errors).length) return;

    if (!cfg.firebase) {
      submitByEmail(request);
      showSuccess("Your email app should open with the request filled in. Hit send and we'll call you back to confirm a time.");
      return;
    }

    setBusy(true);
    submitToFirestore(request)
      .then(function () {
        showSuccess("Thanks! We'll call you shortly to confirm a time.");
      })
      .catch(function (err) {
        console.error(err);
        showFailure("We couldn't send your request right now.");
      })
      .then(function () { setBusy(false); });
  });

  fillServiceOptions();
})();
