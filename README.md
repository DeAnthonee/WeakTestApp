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
├── fonts/                     Self-hosted woff2 files (Barlow Condensed, Source Sans 3)
├── js/config.js               Business details, hours, cities, form key
├── js/main.js                 Injects config, nav toggle, forms, JSON-LD
├── assets/og.png              Social sharing image
├── sitemap.xml  robots.txt  favicon.svg
├── _headers                   Cloudflare Pages headers: noindex on *.pages.dev, security, font caching
scripts/check-links.js         Verifies every local link resolves (used by CI)
scripts/check-seo.js           Titles, descriptions, canonicals, sitemap and schema hooks (used by CI)
lighthouserc.json              Lighthouse CI thresholds (95+ mobile)
docs/seo/                      SEO plan, launch checklist and work orders for the hosting engineer
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
access key; forms post there and show an inline confirmation),
`GA4_MEASUREMENT_ID` (a Google Analytics `G-...` ID; when set, the site loads
GA4 and sends a `phone_call` event for every tap on a phone number and a
`generate_lead` event for every form that sends), `hours`,
`openingHours` (schema.org hours), `sameAs` (social profile URLs; entries
still wrapped in `[[ ]]` are ignored) and the `cities` list used for the
service area schema.

In HTML, write a token as `[[PHONE]]` and the script replaces it on load,
including inside `href` attributes. The legal line, canonical URLs and page
copy are written directly in the HTML.

## Deploying to Cloudflare Pages

1. In Cloudflare Pages, create a project from this repository's default
   branch.
2. Framework preset: none. Build command: leave empty. Build output
   directory: `website`.
3. Add the custom domain `northbankseptic.com`.

Hosting notes:

* No build step, no server code, no database, no environment variables.
  Everything in `website/` is served as-is.
* No `_redirects` file is needed. Page URLs are folders with an
  `index.html`, and `website/404.html` is picked up automatically as the
  not-found page. `website/_headers` adds `X-Robots-Tag: noindex` on the
  `*.pages.dev` preview host so search engines index only the custom domain,
  plus security headers and long caching for fonts.
* `www.northbankseptic.com` should 301 to the apex domain with a Cloudflare
  redirect rule rather than being added as a second custom domain; see
  `docs/seo/work-orders/`.
* All asset paths are root-relative (`/css/...`, `/fonts/...`), so the site
  works at the apex domain over HTTPS. Fonts are self-hosted; the only
  third-party request is the form submission below.
* Quote forms POST JSON to Web3Forms (`https://api.web3forms.com/submit`)
  with the public access key from `website/js/config.js`. The key is meant
  to be public and is not a secret. Until a real key is set, submissions
  show the fallback "call us" message.
* Canonical URLs, Open Graph URLs and `sitemap.xml` are written for
  `https://northbankseptic.com`.

Any static host works the same way: publish the `website/` folder as the site
root.

## Checks

* `node scripts/check-links.js website` verifies every `href` and `src`
  points at a file that exists.
* `node scripts/check-seo.js website` verifies every page has a unique title
  and description, one `h1`, a canonical URL matching its path, Open Graph
  tags and a `data-schema` hook, and that the sitemap and pages match.
* `node --check website/js/*.js` catches syntax errors.
* The `Website` GitHub Actions workflow runs both, plus Lighthouse CI on
  mobile with performance, accessibility and SEO required at 95 or higher.

## Search

`docs/seo/README.md` is the plan: what the site already does for search,
what the owner must supply (real phone, address, Google Business Profile),
and the roadmap. `docs/seo/launch-checklist.md` is the go-live list.
Anything on DNS, Cloudflare or hosting is a work order in
`docs/seo/work-orders/` for the hosting engineer.

## Editing the design

Colors, type and spacing are CSS custom properties at the top of
`website/css/styles.css`; change a token and every page follows. Fonts are
Barlow Condensed and Source Sans 3, self-hosted as woff2 files in
`website/fonts/` and declared at the top of the stylesheet; each page
preloads the three faces used above the fold.
