# King Septic Services

Website and installable web app for a septic service business. Plain HTML,
CSS and JavaScript with no build step, so it can be hosted anywhere. Visitors
can add it to their phone's home screen and it keeps working offline.

> The business name, phone number, email and service area in this repo are
> **placeholders**. See [Make it yours](#make-it-yours) to change them.

## What's here

```
├── website/                    The site / web app (deploy this folder)
│   ├── index.html              Home: hero, services, how it works, pumping calculator
│   ├── services.html           Service catalog with details
│   ├── maintenance.html        Septic care guide: calculator, warning signs, tips
│   ├── about.html              About the company, service area, hours
│   ├── contact.html            Request service / free quote form
│   ├── 404.html                Not-found page
│   ├── manifest.webmanifest    Web app name, icons and colors (for "Add to home screen")
│   ├── sw.js                   Service worker: offline cache
│   ├── css/styles.css          All styles; brand colors are variables at the top
│   ├── js/config.js            <-- business details + service list live here
│   ├── js/main.js              Fills config into the pages, nav, calculator, install button
│   ├── js/quote-form.js        Form validation + Firestore (or email) submission
│   └── assets/                 Logo and app icons
├── firebase.json               Firebase Hosting + Firestore config
├── firestore.rules             Security rules for the `serviceRequests` collection
└── .github/workflows/          CI: syntax and link checks for the site
```

### Features

* **Request service form** with validation. Saves to Firestore when Firebase is
  configured; otherwise it opens the visitor's email app with the request
  filled in. `contact.html?service=pumping` preselects a service.
* **Pumping calculator** (home and care guide): estimates the pumping interval
  from tank size, household size and garbage-disposal use, and the next due
  date from the last service.
* **Septic care guide**: warning signs of trouble and eight maintenance tips.
* **One-tap call** everywhere, with a 24/7 emergency band.
* **Web app**: installable on Android, iOS and desktop; pages and assets are
  cached for offline use; an "Install app" button appears when the browser
  offers installation.

## Quick start

Open `website/index.html` in a browser, or serve the folder (the service
worker and install prompt need `http://` or `https://`, not `file://`):

```bash
cd website
python3 -m http.server 8080     # then open http://localhost:8080
```

## Make it yours

| What | Where |
| --- | --- |
| Business name, phone, email, hours, service area, towns | `website/js/config.js` |
| Name and colors of the installed app | `website/manifest.webmanifest`, and the `theme-color` / `apple-mobile-web-app-title` tags in each page's `<head>` |
| List of services | `website/js/config.js` (`services`) |
| Colors | `website/css/styles.css` (`:root` variables) |
| Logo and icons | `website/assets/logo.svg`, `website/favicon.svg`, `website/assets/icon-*.png` |
| Page copy | The `.html` files in `website/` |

After changing any file listed in `sw.js`'s `SHELL` array, bump its `VERSION`
string so installed copies pick up the new files.

## Firebase (optional, recommended)

Without Firebase everything still works: requests are handed to an email app
addressed to your business email. With Firebase, requests are stored in
Firestore where you can see them in the console, and the site can be hosted on
Firebase Hosting.

1. Create a project at <https://console.firebase.google.com> and enable **Firestore** (production mode).
2. Install the CLI and deploy the security rules from the repo root:
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase use --add          # pick your project
   firebase deploy --only firestore:rules
   ```
3. In Project settings → *Your apps* add a **Web** app and copy its
   `firebaseConfig` object into `website/js/config.js` as the `firebase` value.
4. To host the site on Firebase: `firebase deploy --only hosting`.

Requests land in the `serviceRequests` collection with these fields:
`customerName`, `phone` (10 digits), `email`, `address`, `serviceId`,
`serviceName`, `preferredDate`, `notes`, `isEmergency`, `source` (`web`),
`status` (`new`), `createdAt`. The rules in `firestore.rules` only allow
anonymous *creates* that match this shape; reading and managing requests
happens in the Firebase console (or a future admin tool).

## Hosting elsewhere

Any static host works: upload the contents of `website/` to Netlify, GitHub
Pages, Cloudflare Pages or a cPanel `public_html`. The service worker needs
HTTPS, which all of these provide.

## Pumping calculator

The estimate uses the widely published Penn State Extension table
(approximately `years = 0.013 × gallons ÷ people − 0.65`, scaled by ⅔ with a
garbage disposal). The recommendation shown to customers is capped at 5 years.

## Claude Code skills

`.claude/skills/` holds skills that Claude Code loads automatically when
working in this repo, so UI changes follow good design and accessibility
practice. Their sources and licenses:

| Skill | Source | License |
| --- | --- | --- |
| `frontend-design` | [anthropics/skills](https://github.com/anthropics/skills) | Apache 2.0 |
| `webapp-testing` | [anthropics/skills](https://github.com/anthropics/skills) | Apache 2.0 |
| `web-design-guidelines` | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) | MIT |
| `writing-guidelines` | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) | MIT |

To update them, copy the newer folders from those repos over the ones here.

## Ideas for later

* Customer accounts with service history and "next pumping due" reminders
* An admin screen for dispatching and updating request status
* Online payments and invoices
* Reviews / testimonials section
