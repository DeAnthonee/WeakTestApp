/* Service worker: makes the site installable and usable offline.
 * Bump VERSION whenever you change the files in SHELL. */
var VERSION = "king-septic-v1";
var SHELL = [
  "index.html",
  "services.html",
  "maintenance.html",
  "about.html",
  "contact.html",
  "404.html",
  "css/styles.css",
  "js/config.js",
  "js/main.js",
  "js/quote-form.js",
  "assets/logo.svg",
  "assets/icon-192.png",
  "assets/icon-512.png",
  "assets/icon-maskable-512.png",
  "favicon.svg",
  "manifest.webmanifest",
];

self.addEventListener("install", function (event) {
  event.waitUntil(
    caches.open(VERSION)
      .then(function (cache) { return cache.addAll(SHELL); })
      .then(function () { return self.skipWaiting(); })
  );
});

self.addEventListener("activate", function (event) {
  event.waitUntil(
    caches.keys()
      .then(function (keys) {
        return Promise.all(keys.filter(function (k) { return k !== VERSION; }).map(function (k) { return caches.delete(k); }));
      })
      .then(function () { return self.clients.claim(); })
  );
});

function cachePut(request, response) {
  var copy = response.clone();
  caches.open(VERSION).then(function (cache) { cache.put(request, copy); });
  return response;
}

self.addEventListener("fetch", function (event) {
  var request = event.request;
  if (request.method !== "GET" || new URL(request.url).origin !== self.location.origin) return;

  // Pages: network first so visitors see fresh content, cache when offline.
  if (request.mode === "navigate") {
    event.respondWith(
      fetch(request)
        .then(function (response) { return cachePut(request, response); })
        .catch(function () {
          return caches.match(request, { ignoreSearch: true })
            .then(function (cached) { return cached || caches.match("index.html"); });
        })
    );
    return;
  }

  // Assets: cache first, refresh in the background.
  event.respondWith(
    caches.match(request).then(function (cached) {
      var network = fetch(request)
        .then(function (response) { return response.ok ? cachePut(request, response) : response; })
        .catch(function () { return cached; });
      return cached || network;
    })
  );
});
