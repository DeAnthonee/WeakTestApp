# Northbank Septic website

Static marketing site for Northbank Septic, a septic pumping, inspection and
grease trap company in Vancouver, WA. Plain HTML, CSS and JavaScript in
`website/`. No build step, no framework, no service worker.

## Layout

```
website/
├── index.html                 Home
├── septic-pumping/            Service page with pricing table
├── septic-inspection/         Clark County Report of System Status
├── grease-trap-pumping/       Restaurant route service
├── service-area/              Overview plus one page per city
├── septic-care-guide/         Pumping calculator, warning signs, care tips
├── about/  contact/  404.html
├── css/styles.css             Design tokens at the top; everything styled through them
├── js/config.js               Business details, hours, cities, form key
├── js/main.js                 Injects config, nav toggle, forms, JSON-LD
├── assets/og.png              Social sharing image
├── sitemap.xml  robots.txt  favicon.svg
scripts/check-links.js         Verifies every local link resolves (used by CI)
lighthouserc.json              Lighthouse CI thresholds (95+ mobile)
```

## Placeholders to fill in

Everything business-specific lives in `website/js/config.js`. The `tokens`
block holds the values that appear across every page and in the JSON-LD:

| Token | What it is |
| --- | --- |
| `PHONE` | Display phone number, e.g. `(360) 555-0100`. The `tel:` links are derived from it. |
| `EMAIL` | Contact email |
| `STREET` | Street address as it should appear in the footer and schema |
| `CERT` | Certification or permit line shown in the footer |
| `GOOGLE_BUSINESS_PROFILE_URL` | Link for "Read our Google reviews" |
| `HOURS_SHORT` | One-line hours for the top bar |

Also in `config.js`: `FORM_ACCESS_KEY` (your [Web3Forms](https://web3forms.com)
access key; forms post there and show an inline confirmation), `hours`,
`openingHours` (schema.org hours), `sameAs` (social profile URLs; entries
still wrapped in `[[ ]]` are ignored) and the `cities` list used for the
service area schema.

In HTML, write a token as `[[PHONE]]` and the script replaces it on load,
including inside `href` attributes. The legal line, canonical URLs and page
copy are written directly in the HTML.

## Deploying to Cloudflare Pages

1. Push this repository to GitHub.
2. In Cloudflare Pages, create a project from the repo.
3. Build command: none. Build output directory: `website`.
4. Add the custom domain `northbankseptic.com`.

Any static host works the same way: publish the `website/` folder as the site
root. Page URLs are folders with an `index.html`, so `/septic-pumping/`
resolves everywhere without rewrite rules.

## Checks

* `node scripts/check-links.js website` verifies every `href` and `src`
  points at a file that exists.
* `node --check website/js/*.js` catches syntax errors.
* The `Website` GitHub Actions workflow runs both, plus Lighthouse CI on
  mobile with performance, accessibility and SEO required at 95 or higher.

## Editing the design

Colors, type and spacing are CSS custom properties at the top of
`website/css/styles.css`; change a token and every page follows. Fonts are
Barlow Condensed and Source Sans 3 from Google Fonts, loaded once in each
page's `<head>`.
