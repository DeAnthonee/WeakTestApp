# SEO plan for Northbank Septic

Goal: when someone in Clark or Cowlitz County searches for septic pumping, an inspection for a home sale, or grease trap service, Northbank Septic shows in the map pack and the organic results, and the page they land on gets them to call.

Local service SEO has three parts, in order of impact:

1. **Google Business Profile (GBP)**: the map listing. It drives most calls for a septic company and lives outside the website. The owner has to claim it. See the [launch checklist](launch-checklist.md).
2. **The website**: this repo. One page per service and per city, upfront pricing, and the technical signals search engines expect.
3. **Reputation and citations**: Google reviews, and the same name, address and phone listed identically on every directory.

## What the site already does

| Area | State |
| --- | --- |
| Service pages | `/septic-pumping/`, `/septic-inspection/`, `/grease-trap-pumping/`, each with FAQs and a price table or scope |
| City pages | 14 pages under `/service-area/`, one per city in Clark and Cowlitz counties, each with local detail and FAQs |
| Pricing | Public per-gallon pricing on the pumping page: the highest-volume question in the category, answered |
| Titles and descriptions | Unique and location-first on every page; `scripts/check-seo.js` fails CI if that regresses |
| Structured data | `LocalBusiness` on every page, `Service` on service and city pages, `FAQPage` wherever there is a FAQ, built from `config.js` |
| Canonical URLs, Open Graph, share image | Written for `https://northbankseptic.com` on every page |
| Crawling | `sitemap.xml`, `robots.txt`, `noindex` on 404, preview host `*.pages.dev` marked noindex via `_headers` |
| Speed | Self-hosted fonts, no third-party scripts until analytics is turned on; Lighthouse CI requires 95+ on mobile |
| Measurement | Optional GA4 with `phone_call` and `generate_lead` events, set by `GA4_MEASUREMENT_ID` in `config.js` |
| Reviews | "Read our Google reviews" link from `GOOGLE_BUSINESS_PROFILE_URL` |

## What the owner has to supply

Placeholders in `website/js/config.js` are still placeholders. Wrong values here hurt rankings, so none of them can be guessed.

- Phone number, email, street address, certification line. Name, address and phone must match the Google Business Profile character for character.
- The real Google Business Profile link (the current value is a placeholder).
- Web3Forms access key, so quote requests reach the office.
- A GA4 measurement ID once the Analytics property exists.
- Facebook and Yelp URLs for `sameAs`, if those profiles exist.
- Photos: trucks, crew, a tank being pumped, a photo receipt. Real photos outperform anything else on the map listing.

## Roadmap

Each phase builds on the last, ordered by expected calls per hour of effort.

### Phase 2: launch and listings (first two weeks after DNS is live)

- Run the launch checklist: Search Console, Bing Webmaster Tools, GBP, Bing Places, Apple Business Connect.
- Submit the sitemap and request indexing for the home page, the three service pages and the Vancouver, Battle Ground and Camas pages.
- Collect the first ten Google reviews: a text with the review link after every job.
- Ten real photos on GBP, one GBP post a week.

### Phase 3: content that answers the next question (months 1 to 3)

- Septic pumping cost guide for Clark County: the per-gallon price explained, what changes the total (buried lids, risers, pump chambers), with a worked example for a 1,000 gallon tank.
- Clark County Report of System Status guide for buyers, sellers and agents: what it is, who orders it, how long it takes, what fails.
- Seasonal pages: septic in a Pacific Northwest wet winter, drain fields after heavy rain, preparing for holiday guests.
- Add BreadcrumbList schema with visible breadcrumbs on service and city pages.
- Job stories with photos on city pages ("what we found in Hockinson this month").

### Phase 4: conversion and tracking (months 2 to 4)

- Call tracking number for the website only, so website calls and GBP calls can be told apart. The real number stays on GBP.
- Review-request automation: a text with the review link when a job is marked complete.
- Online scheduling with real arrival windows on the quote form.
- Monthly Search Console review: queries ranking in positions 5 to 15 get a content refresh to push them onto page one.

### Phase 5: authority (months 3 to 12)

- Google Local Services Ads with the Google Guaranteed badge, which sits above the map pack for septic searches.
- Citations: Yelp, Nextdoor, Angi, BBB, the Clark County and Cowlitz County health department pumper lists, the Washington On-Site Sewage Association directory.
- Referral partnerships that earn links: real estate agents and home inspectors (inspection referrals), restaurant suppliers (grease trap routes), property managers.
- A short video of a pumping visit for the home page and GBP.

## Infrastructure items (hosting engineer)

Anything that touches DNS, Cloudflare or the host is a work order rather than a change in this repo.

- [WO-001: Domain, DNS and Cloudflare Pages setup for search](work-orders/WO-001-domain-cloudflare-pages.md)

## How to check it's working

- `node scripts/check-seo.js website` passes.
- Google's Rich Results Test on `/septic-pumping/` shows LocalBusiness, Service and FAQPage with no errors.
- Search Console: all 22 pages indexed, no "duplicate without user-selected canonical" warnings, no mobile usability issues, no `pages.dev` URLs.
- GA4: `phone_call` and `generate_lead` events arriving.
- Monthly: positions for "septic pumping vancouver wa", "septic inspection clark county", "grease trap pumping vancouver", and the map pack for "septic pumping near me" from inside the service area.
