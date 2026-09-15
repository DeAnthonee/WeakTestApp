# Northbank Septic — project notes for Claude

Static marketing site for a septic pumping company in Vancouver, WA. Plain
HTML, CSS and JavaScript in `website/`, no build step, no service worker.
See README.md for the layout, placeholders and deployment.

## Working on the site

- The design spec is the source of truth: square corners (4px, 6px on
  cards), no gradients, no emoji, no illustrations, no stock photos, one
  accent color (orange). Tokens live at the top of `website/css/styles.css`;
  style everything through them and do not edit their values.
- Business details are `[[TOKENS]]` filled from `website/js/config.js`;
  never hard-code a phone number, email or address in a page.
- Every page shares the same anatomy: topbar, sticky header, hero with
  hazard stripes, proof strip, alternating ground/tint sections, CTA band,
  footer, and the mobile call bar. Copy a sibling page's chrome verbatim.
- Pages are folders with `index.html`; links are root-relative.
- No code comments in shipped files.
- Verify in a browser: serve `website/` with `python3 -m http.server` and
  screenshot at 1280px and 390px. Playwright and Chromium are available. Do
  not use `pkill -f` patterns that could match your own shell.
- Before pushing: `node --check website/js/*.js`,
  `node scripts/check-links.js website`, and Lighthouse via `lhci` when
  available (CI requires 95+ mobile on performance, accessibility, SEO).

## Skills in `.claude/skills/`

- `frontend-design` (Anthropic): use whenever building or restyling UI.
- `web-design-guidelines` (Vercel): audit a page for accessibility and UX
  before finishing UI work.
- `writing-guidelines` (Vercel): use when writing or editing page copy.
- `webapp-testing` (Anthropic): Playwright patterns for verifying pages.
