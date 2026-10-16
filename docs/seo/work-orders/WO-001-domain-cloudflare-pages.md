# WO-001: Domain, DNS and Cloudflare Pages setup for search

**From:** SEO (website repo)
**To:** Hosting engineer (owns DNS and Cloudflare)
**Status:** Open
**Why:** Search engines need one stable, fast HTTPS address for the site. Each item removes a ranking or indexing problem. None changes site content.

Site facts the work order assumes: static files in the `website/` folder of this repo, no build step, deployed from the `master` branch to Cloudflare Pages, canonical hostname `https://northbankseptic.com` (apex, no www). Every canonical link, Open Graph URL and the sitemap are already written for that hostname.

## 1. Cloudflare Pages project

- Connect the repository. Production branch `master`. Framework preset none, build command empty, build output directory `website`.
- Custom domain: `northbankseptic.com` only. Do not add `www` as a second custom domain (that serves a duplicate site); redirect it instead (section 3).
- Preview deployments: fine to keep. The repo's `website/_headers` marks every `*.pages.dev` host `X-Robots-Tag: noindex`, so previews and the production `pages.dev` alias stay out of search. Optionally restrict preview access with Cloudflare Access.
- 404s: Cloudflare Pages serves `website/404.html` with status 404 automatically. Confirm with the acceptance test.

## 2. DNS

- Apex `northbankseptic.com`: CNAME to the Pages project (Cloudflare flattens it at the apex), proxied (orange cloud).
- `www`: CNAME to the Pages project, proxied, so the redirect rule in section 3 can fire.
- Mail for `office@northbankseptic.com`: MX plus SPF, DKIM and DMARC records for whichever mail provider the owner uses, so quote-request and review-request emails don't land in spam.
- Search Console verification: a `TXT` record at the apex, value supplied by the owner from Search Console (`google-site-verification=...`).

## 3. One canonical hostname

Create a Redirect Rule (Rules > Redirect Rules) with status 301 and preserve path and query:

| If | Then |
| --- | --- |
| hostname equals `www.northbankseptic.com` | `https://northbankseptic.com${path}${query}` |

- SSL/TLS: **Full (strict)**. **Always Use HTTPS** on. **Automatic HTTPS Rewrites** on.
- HSTS is sent by the site's `_headers` file; no need to enable it at the zone, but harmless if you do with the same max-age.

## 4. Cloudflare settings that affect crawlers and speed

| Setting | Value | Why |
| --- | --- | --- |
| Rocket Loader | Off | It defers scripts, which delays the structured data and token fill-in Google reads |
| Auto Minify | Off for HTML, on for CSS and JS is fine | HTML minification can break inline JSON |
| Brotli | On | |
| Early Hints | On | Fonts are preloaded on every page |
| Bot Fight Mode | On with verified bots allowed | Googlebot and Bingbot must not be challenged |
| Under Attack mode, JS challenges on normal traffic | Off | Crawlers cannot pass them |
| Browser Cache TTL | Respect existing headers | `_headers` sets font caching; Pages handles the rest |

## 5. Headers

The repo ships `website/_headers` with:

```text
https://:project.pages.dev/*
  X-Robots-Tag: noindex

/*
  Strict-Transport-Security: max-age=31536000; includeSubDomains
  X-Content-Type-Options: nosniff
  Referrer-Policy: strict-origin-when-cross-origin
  Permissions-Policy: geolocation=(), camera=(), microphone=()

/fonts/*
  Cache-Control: public, max-age=31536000, immutable
```

No zone-level header rules are needed. Do not add a `Content-Security-Policy` without checking with SEO: the quote forms post to `api.web3forms.com`, and once analytics is turned on the pages load `www.googletagmanager.com` and send to `www.google-analytics.com`.

## 6. Monitoring

- Uptime check on `https://northbankseptic.com/` every 5 minutes with an alert to the owner. Downtime during a crawl can drop pages from the index.
- Give SEO read-only access to the Cloudflare analytics for the zone.

## Acceptance

Run after DNS has propagated:

```bash
curl -sI http://northbankseptic.com/ | head -1                    # 301 to https://northbankseptic.com/
curl -sI https://www.northbankseptic.com/septic-pumping/ | grep -i location   # https://northbankseptic.com/septic-pumping/
curl -sI https://northbankseptic.com/ | grep -i strict-transport  # HSTS header present
curl -sI https://northbankseptic.com/fonts/source-sans-3-latin.woff2 | grep -i cache-control   # max-age=31536000, immutable
curl -sI https://northbankseptic.com/no-such-page | head -1       # 404
curl -sI https://<project>.pages.dev/ | grep -i x-robots-tag      # noindex
curl -s https://northbankseptic.com/robots.txt                     # ends with the sitemap line
```

Report back: the project's `pages.dev` name, confirmation of each line above, and the date the Search Console TXT record went in.
