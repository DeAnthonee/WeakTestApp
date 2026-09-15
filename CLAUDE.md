# King Septic Services — project notes for Claude

A static website and installable web app (PWA) for a septic service
business. No framework, no build step. Everything deployable lives in
`website/`; see README.md for the full layout and how to customize it.

## Working on the site

- Business details come from `website/js/config.js`; page copy is in the
  `.html` files; brand colors are CSS variables at the top of
  `website/css/styles.css`. Keep the seven pages visually consistent.
- Every page must stay mobile-friendly (test at ~390px) and installable:
  after changing any file listed in `website/sw.js` `SHELL`, bump its
  `VERSION`.
- Verify changes in a real browser. Chromium and Playwright are available;
  serve the folder with `python3 -m http.server` and screenshot pages on
  desktop and mobile widths. Do not use `pkill -f` patterns that could
  match your own shell.
- Quick checks before pushing: `node --check` on every JS file, and make
  sure every `href`/`src` points at a file that exists (the Website CI
  workflow does both).

## Skills in `.claude/skills/`

- `frontend-design` (Anthropic): use whenever building or restyling UI, so
  pages look designed for this business rather than templated.
- `web-design-guidelines` (Vercel): run to audit a page for accessibility
  and UX best practices before finishing UI work.
- `writing-guidelines` (Vercel): use when writing or editing page copy.
- `webapp-testing` (Anthropic): Playwright patterns for verifying pages and
  capturing screenshots and console errors.
