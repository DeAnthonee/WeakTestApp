# Launch checklist

Work through this in order. Items marked **hosting** go to the hosting engineer as part of [WO-001](work-orders/WO-001-domain-cloudflare-pages.md).

## 1. Business details (owner)

- [ ] `website/js/config.js`: `PHONE`, `EMAIL`, `STREET`, `CERT`, `HOURS_SHORT`, `hours`, `openingHours`.
- [ ] `GOOGLE_BUSINESS_PROFILE_URL`: the real profile link (the current value is a placeholder).
- [ ] `FORM_ACCESS_KEY`: a Web3Forms key, so quote requests reach the office. Send a test request.
- [ ] `sameAs`: Facebook and Yelp URLs, or leave the `[[ ]]` placeholders to omit them.

## 2. Domain and hosting (hosting)

- [ ] Cloudflare Pages project on the `master` branch, output directory `website`, custom domain `northbankseptic.com`.
- [ ] `www.northbankseptic.com` and `http://` 301 to `https://northbankseptic.com`.
- [ ] `https://northbankseptic.com/sitemap.xml`, `/robots.txt` and `/_headers` behavior verified (see WO-001 acceptance).

## 3. Search engine accounts (owner, with hosting for DNS records)

- [ ] Google Search Console: add the domain property `northbankseptic.com`. **hosting**: add the TXT record. Submit `sitemap.xml`. Request indexing for `/`, `/septic-pumping/`, `/septic-inspection/`, `/grease-trap-pumping/`, `/service-area/vancouver/`.
- [ ] Bing Webmaster Tools: import from Search Console. Submit the sitemap.
- [ ] Google Analytics 4: create a property, paste the `G-...` ID into `GA4_MEASUREMENT_ID` in `config.js`. Mark `phone_call` and `generate_lead` as key events.

## 4. Google Business Profile (owner)

- [ ] Claim or create the profile at business.google.com. Verify by video or postcard.
- [ ] Primary category: Septic system service. Secondary: Sanitation service, Waste management service.
- [ ] Service area: the 14 cities in `config.js`. Hide the street address if customers don't visit.
- [ ] Hours matching `config.js`; note Sunday as emergency calls only in the description.
- [ ] Services: Septic tank pumping, Septic inspection (Report of System Status), Grease trap pumping, with the per-gallon price on pumping.
- [ ] Website: `https://northbankseptic.com`. Appointment link: `https://northbankseptic.com/contact/`.
- [ ] Ten real photos: trucks, crew, pumping in progress, a photo receipt.
- [ ] Copy the "Ask for reviews" link; use it in every post-job text.
- [ ] Turn on messaging and answer within the business hour promised on the site.

## 5. Other listings (owner)

Same name, phone and address everywhere, exactly as on GBP.

- [ ] Bing Places (import from GBP).
- [ ] Apple Business Connect.
- [ ] Facebook page, Yelp, Nextdoor, BBB.
- [ ] Clark County Public Health and Cowlitz County certified pumper lists; Washington On-Site Sewage Association directory.
- [ ] Paste profile URLs into `sameAs` in `config.js`.

## 6. Verify (anyone)

- [ ] Google Rich Results Test on `/septic-pumping/`: LocalBusiness, Service, FAQPage, no errors.
- [ ] Share the home page in a text message or Facebook post: the share image and title appear.
- [ ] PageSpeed Insights on mobile: 95+ performance, no accessibility errors.
- [ ] Search Console a week later: every page indexed; search `site:northbankseptic.pages.dev` returns nothing.

## 7. First month

- [ ] Ask every customer for a review by text the same day.
- [ ] One GBP post a week: a job photo and two sentences.
- [ ] Check Search Console queries and start Phase 3 in the [SEO plan](README.md).
