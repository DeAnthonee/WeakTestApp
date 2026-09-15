/*
 * Site configuration. This is the ONE place to change business details.
 * (The app name shown when installed lives in manifest.webmanifest.)
 */
window.SITE_CONFIG = {
  business: {
    name: "King Septic Services",
    shortName: "King Septic",
    tagline: "Septic pumping, repair and installation you can count on",
    // Digits only; the site formats it for display and dialing.
    phone: "5550100100",
    email: "office@example.com",
    serviceArea: "Anytown County and surrounding areas",
    serviceTowns: ["Anytown", "Springfield", "Riverside", "Oak Hill", "Fairview", "Lakeside"],
    hours: [
      { label: "Monday – Friday", value: "7:00am – 6:00pm" },
      { label: "Saturday", value: "8:00am – 2:00pm" },
      { label: "Emergency service", value: "24 hours, 7 days" },
    ],
    license: "Licensed & insured · Lic. #0000000",
  },

  // Service catalog. `id` is used in URLs and stored with each request.
  services: [
    {
      id: "pumping",
      name: "Septic Tank Pumping",
      summary: "Routine pumping and cleaning to keep your system healthy.",
      details:
        "We pump the tank, remove sludge and scum, check the baffles and inlet/outlet, and leave your site clean. Most households need this every 3 to 5 years.",
      emergency: true,
    },
    {
      id: "inspection",
      name: "Septic Inspections",
      summary: "Real-estate and routine inspections with a written report.",
      details:
        "A full inspection of the tank, distribution box and drain field, including a written report suitable for home sales and refinancing.",
    },
    {
      id: "repair",
      name: "Septic Repairs",
      summary: "Baffles, pumps, lids, risers, lines and more.",
      details:
        "We diagnose and repair failed baffles, effluent pumps, float switches, cracked lids, collapsed lines and other common failures.",
      emergency: true,
    },
    {
      id: "installation",
      name: "New System Installation",
      summary: "Design, permitting and installation of new septic systems.",
      details:
        "From soil evaluation and permits to the final inspection, we handle conventional and alternative system installs for new builds and replacements.",
    },
    {
      id: "drain-field",
      name: "Drain Field Services",
      summary: "Drain field evaluation, restoration and replacement.",
      details:
        "Standing water, soggy ground or sewage odors usually point to the drain field. We evaluate the field and recommend restoration or replacement.",
    },
    {
      id: "grease-trap",
      name: "Grease Trap Cleaning",
      summary: "Scheduled grease trap service for restaurants and kitchens.",
      details:
        "Recurring grease trap pumping and cleaning for commercial kitchens, with service records for your health inspector.",
    },
    {
      id: "emergency",
      name: "24/7 Emergency Service",
      summary: "Backups, overflows and alarms, any time of day.",
      details:
        "Sewage backing up, an alarm sounding or a tank overflowing is an emergency. Call us and we will dispatch a truck as soon as possible.",
      emergency: true,
    },
  ],

  /*
   * Optional. Paste the "firebaseConfig" object from your Firebase project
   * (Project settings → Your apps → Web app) to save quote requests to the
   * `serviceRequests` Firestore collection. Leave as null and the contact form
   * opens the visitor's email app with the request filled in instead.
   *
   * firebase: {
   *   apiKey: "...",
   *   authDomain: "your-project.firebaseapp.com",
   *   projectId: "your-project",
   *   storageBucket: "your-project.appspot.com",
   *   messagingSenderId: "...",
   *   appId: "...",
   * },
   */
  firebase: null,
};
