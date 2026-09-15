window.SITE = {
  name: "Northbank Septic",
  legalName: "Northbank Septic LLC",
  url: "https://northbankseptic.com",
  FORM_ACCESS_KEY: "YOUR-WEB3FORMS-ACCESS-KEY",
  tokens: {
    PHONE: "(360) 555-0100",
    EMAIL: "office@northbankseptic.com",
    STREET: "1234 NE Example Rd, Vancouver, WA 98662",
    CERT: "WA DOH Pumper Certification #00000",
    GOOGLE_BUSINESS_PROFILE_URL: "https://g.page/northbank-septic",
    HOURS_SHORT: "Mon–Fri 7am–5pm, Sat 8am–12pm"
  },
  address: { city: "Vancouver", region: "WA", country: "US" },
  hours: [
    { days: "Monday – Friday", time: "7:00am – 5:00pm" },
    { days: "Saturday", time: "8:00am – 12:00pm" },
    { days: "Sunday", time: "Emergency calls only" }
  ],
  openingHours: [
    { "@type": "OpeningHoursSpecification", dayOfWeek: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"], opens: "07:00", closes: "17:00" },
    { "@type": "OpeningHoursSpecification", dayOfWeek: ["Saturday"], opens: "08:00", closes: "12:00" }
  ],
  sameAs: ["[[FACEBOOK_URL]]", "[[YELP_URL]]"],
  cities: [
    { slug: "vancouver", name: "Vancouver", county: "Clark" },
    { slug: "battle-ground", name: "Battle Ground", county: "Clark" },
    { slug: "camas", name: "Camas", county: "Clark" },
    { slug: "washougal", name: "Washougal", county: "Clark" },
    { slug: "ridgefield", name: "Ridgefield", county: "Clark" },
    { slug: "la-center", name: "La Center", county: "Clark" },
    { slug: "brush-prairie", name: "Brush Prairie", county: "Clark" },
    { slug: "hockinson", name: "Hockinson", county: "Clark" },
    { slug: "yacolt", name: "Yacolt", county: "Clark" },
    { slug: "amboy", name: "Amboy", county: "Clark" },
    { slug: "woodland", name: "Woodland", county: "Cowlitz" },
    { slug: "kalama", name: "Kalama", county: "Cowlitz" },
    { slug: "longview", name: "Longview", county: "Cowlitz" },
    { slug: "kelso", name: "Kelso", county: "Cowlitz" }
  ]
};
