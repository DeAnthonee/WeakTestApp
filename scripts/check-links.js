const fs = require("fs");
const path = require("path");
const root = path.resolve(process.argv[2] || "website");
let failures = 0;

function pages(dir) {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap(function (e) {
    const p = path.join(dir, e.name);
    return e.isDirectory() ? pages(p) : e.name.endsWith(".html") ? [p] : [];
  });
}

function exists(target, fromDir) {
  let clean = target.split("#")[0].split("?")[0];
  if (!clean) return true;
  const abs = clean.startsWith("/") ? path.join(root, clean) : path.resolve(fromDir, clean);
  if (fs.existsSync(abs)) {
    return fs.statSync(abs).isDirectory() ? fs.existsSync(path.join(abs, "index.html")) : true;
  }
  return false;
}

pages(root).forEach(function (page) {
  const html = fs.readFileSync(page, "utf8");
  const refs = new Set();
  for (const m of html.matchAll(/\b(?:href|src)="([^"]+)"/g)) refs.add(m[1]);
  refs.forEach(function (ref) {
    if (/^(https?:|tel:|mailto:|#|data:)/.test(ref) || ref.includes("[[")) return;
    if (!exists(ref, path.dirname(page))) {
      failures++;
      console.log(path.relative(root, page) + " references missing file: " + ref);
    }
  });
});

console.log(failures ? failures + " broken reference(s)" : "All local links resolve");
process.exit(failures ? 1 : 0);
