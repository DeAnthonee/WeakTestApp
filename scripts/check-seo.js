const fs = require("fs");
const path = require("path");
const root = path.resolve(process.argv[2] || "website");
const site = "https://northbankseptic.com";
let failures = 0;

function fail(msg) { failures++; console.log(msg); }

function pages(dir) {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap(function (e) {
    const p = path.join(dir, e.name);
    return e.isDirectory() ? pages(p) : e.name.endsWith(".html") ? [p] : [];
  });
}

function attr(html, re) { const m = html.match(re); return m ? m[1].trim() : ""; }

const sitemap = fs.readFileSync(path.join(root, "sitemap.xml"), "utf8");
const inSitemap = new Set([...sitemap.matchAll(/<loc>([^<]+)<\/loc>/g)].map((m) => m[1]));
const robots = fs.readFileSync(path.join(root, "robots.txt"), "utf8");
if (!robots.includes("Sitemap: " + site + "/sitemap.xml")) fail("robots.txt does not point at " + site + "/sitemap.xml");

const titles = new Map();
const descriptions = new Map();
const indexable = new Set();

pages(root).forEach(function (page) {
  const rel = path.relative(root, page).split(path.sep).join("/");
  const html = fs.readFileSync(page, "utf8");
  const title = attr(html, /<title>([^<]*)<\/title>/);
  const description = attr(html, /<meta name="description" content="([^"]*)"/);
  const canonical = attr(html, /<link rel="canonical" href="([^"]*)"/);
  const noindex = /<meta name="robots" content="[^"]*noindex/.test(html);
  const h1s = (html.match(/<h1[\s>]/g) || []).length;

  if (!title || title.includes("[[")) fail(rel + ": missing title");
  if (title.length > 70) fail(rel + ": title is " + title.length + " characters, keep it under 70");
  if (!description) fail(rel + ": missing meta description");
  if (description.length < 50 || description.length > 160) fail(rel + ": description is " + description.length + " characters, aim for 50 to 160");
  if (h1s !== 1) fail(rel + ": has " + h1s + " h1 elements, needs exactly one");
  if (title && titles.has(title)) fail(rel + ": title duplicates " + titles.get(title));
  if (description && descriptions.has(description)) fail(rel + ": description duplicates " + descriptions.get(description));
  titles.set(title, rel);
  descriptions.set(description, rel);

  if (noindex) {
    if (canonical && inSitemap.has(canonical)) fail(rel + ": is noindex but listed in sitemap.xml");
    return;
  }
  const expected = site + "/" + rel.replace(/index\.html$/, "");
  if (canonical !== expected) fail(rel + ": canonical is " + (canonical || "missing") + ", expected " + expected);
  if (!inSitemap.has(expected)) fail(rel + ": missing from sitemap.xml");
  indexable.add(expected);
  if (!/property="og:title"/.test(html) || !/property="og:image"/.test(html)) fail(rel + ": missing Open Graph tags");
  if (!/<body[^>]*data-schema=/.test(html)) fail(rel + ": body needs a data-schema attribute so main.js emits structured data");
});

inSitemap.forEach(function (loc) {
  if (!indexable.has(loc)) fail("sitemap.xml lists " + loc + " which is not an indexable page");
});

console.log(failures ? failures + " search problem(s)" : "Search essentials look good on " + indexable.size + " pages");
process.exit(failures ? 1 : 0);
