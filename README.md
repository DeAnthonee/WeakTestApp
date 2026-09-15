# King Septic Services

Website and Android app for a septic service business. One repo, one shared
backend, one place to change the business details.

> The business name, phone number, email and service area in this repo are
> **placeholders**. See [Make it yours](#make-it-yours) to change them.

## What's here

```
├── website/                 Static marketing site + "request service" form
│   ├── index.html           Home: hero, services, how it works, pumping calculator
│   ├── services.html        Service catalog with details
│   ├── about.html           About the company, service area, hours
│   ├── contact.html         Request service / free quote form
│   ├── js/config.js         <-- business details + service list live here
│   ├── js/main.js           Fills config into the pages, nav, calculator
│   └── js/quote-form.js     Form validation + Firestore (or email) submission
├── android/                 Android app (Kotlin, Material 3)
│   ├── domain/              Pure Kotlin business rules + unit tests (no Android SDK needed)
│   └── app/                 The app: Home, Services, Request service, Maintenance
├── firebase.json            Firebase Hosting (website) + Firestore config
├── firestore.rules          Security rules for the shared `serviceRequests` collection
└── .github/workflows/       CI: builds and tests the app, checks the website
```

### The app

| Screen | What it does |
| --- | --- |
| Home | Tagline, one-tap call, request service, emergency card, hours |
| Services | Catalog of services, each with a "request this service" button |
| Request service | Form with validation. Saves to Firestore, or opens the email app if Firebase isn't configured |
| Maintenance | Pumping-interval calculator, "next service due" date, warning signs, care tips |

### The website

Plain HTML, CSS and JavaScript. No build step, no framework, so it can be
hosted anywhere (Firebase Hosting, Netlify, GitHub Pages, cPanel). The contact
form saves to Firestore when configured, otherwise it opens the visitor's email
app with the request filled in.

## Quick start

### Website

Open `website/index.html` in a browser, or serve the folder:

```bash
cd website
python3 -m http.server 8080     # then open http://localhost:8080
```

### Android app

1. Install [Android Studio](https://developer.android.com/studio) (Ladybug or newer, JDK 17 bundled).
2. **File → Open** and choose the `android/` folder.
3. Run the `app` configuration on an emulator or phone (Android 8.0+).

From the command line:

```bash
cd android
./gradlew :domain:test :app:testDebugUnitTest   # unit tests
./gradlew :app:assembleDebug                    # APK in app/build/outputs/apk/debug/
```

The `domain` module has no Android dependencies, so its tests run anywhere
with a JDK.

## Make it yours

| What | Where |
| --- | --- |
| Business name, phone, email, hours, service area | `website/js/config.js` **and** `android/app/src/main/res/values/strings.xml` (the "Business details" block) |
| List of services | `website/js/config.js` (`services`) **and** `android/domain/src/main/kotlin/com/kingseptic/domain/SepticService.kt`. Keep the `id`s identical in both. |
| Colors | `website/css/styles.css` (`:root` variables) and `android/app/src/main/res/values/colors.xml` |
| Logo / app icon | `website/assets/logo.svg`, `website/favicon.svg`, `android/app/src/main/res/drawable/ic_launcher_foreground.xml` |
| Page copy | The `.html` files in `website/` |
| Android package name | `com.kingseptic.app` in `android/app/build.gradle` (`applicationId` and `namespace`) plus the folder names under `src/main/java`. Android Studio's **Refactor → Rename** on the package does this safely. |

## Firebase (optional, recommended)

Without Firebase everything still works: requests from the website and the app
are handed to an email app addressed to your business email. With Firebase,
requests are stored in Firestore where you can see them in the console.

1. Create a project at <https://console.firebase.google.com> and enable **Firestore** (production mode).
2. Install the CLI and deploy the security rules from the repo root:
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase use --add          # pick your project
   firebase deploy --only firestore:rules
   ```
3. **Website:** in Project settings → *Your apps* add a **Web** app, copy its
   `firebaseConfig` object into `website/js/config.js` as the `firebase` value.
   To host the site on Firebase too: `firebase deploy --only hosting`.
4. **Android:** in Project settings → *Your apps* add an **Android** app with the
   package name `com.kingseptic.app`, download `google-services.json` and put it
   at `android/app/google-services.json` (it is git-ignored). Rebuild the app.

Requests land in the `serviceRequests` collection with these fields:
`customerName`, `phone` (10 digits), `email`, `address`, `serviceId`,
`serviceName`, `preferredDate`, `notes`, `isEmergency`, `source`
(`web`/`android`), `status` (`new`), `createdAt`. The rules in
`firestore.rules` only allow anonymous *creates* that match this shape; reading
and managing requests happens in the Firebase console (or a future admin tool).

## Pumping calculator

Both the app and the website estimate a pumping interval from tank size and
household size using the widely published Penn State Extension table
(approximately `years = 0.013 × gallons ÷ people − 0.65`, scaled by ⅔ with a
garbage disposal). The recommendation shown to customers is capped at 5 years.
See `PumpingScheduleCalculator.kt` and its tests.

## CI

* **Android** workflow: validates the Gradle wrapper, runs unit tests and
  builds a debug APK (downloadable from the workflow run).
* **Website** workflow: syntax-checks the JavaScript and verifies that every
  page only links to files that exist.

## Ideas for later

* Customer accounts with service history and "next pumping due" push reminders
* An admin screen or dashboard for dispatching and updating request status
* Online payments and invoices
* Reviews / testimonials section on the site
