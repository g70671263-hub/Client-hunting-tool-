# SkillRadar 🎯

> **AI-Powered Global B2B Business Intelligence & Client Opportunity Radar**

SkillRadar is an advanced Android application built with Kotlin and Jetpack Compose. It allows freelancers, consultants, and agencies to input their specific skills, analyze local businesses across **126+ countries** on Google Maps, identify operational deficiencies, and generate personalized, high-converting outreach pitches. It also scans active client hiring posts, urgent contract gigs, and RFPs matching the user's skillset.

---

## 🌟 Key Features

### 1. 🗺️ Google Maps Business Radar (`Biz Radar`)
- **Global Coverage**: Supports **126+ countries** across North America, Europe, Asia, Latin America, the Middle East, Africa, and Oceania.
- **Searchable Country & City Selector**: Instant country filter with flags, top business hubs, and optional industry filtering.
- **Deep Gap & Solution Analysis**:
  - **Match Opportunity Score** (% Fit)
  - **Identified Deficiency / Gap**: Pinpoints what the business is doing wrong or missing out on (e.g., outdated reservation workflows, weak local SEO, lack of automated lead capture).
  - **How Your Skill Helps**: Formulates concrete, high-impact solutions.
  - **Deliverable Roadmap & ROI**: Projected revenue uplift and suggested pricing quotes.
  - **Personalized Cold Outreach Hook**: Pre-written pitch tailored to the business owner.
- **1-Tap Google Maps Integration**: Launches Google Maps directly to inspect the business location, reviews, and street view.

### 2. ⚡ Live Hiring, Need & Gig Radar (`Hiring Radar`)
- **Active Client Calls & RFPs**: Searches real-time freelance requests, contract gigs, urgent client needs, and project RFPs matching your skill.
- **Signal Breakdown**: Displays platform sources (LinkedIn, Twitter/X, Upwork, Direct Job Boards), budget ranges, urgency tags, and specific requirements.
- **Tactical Pitch Advice**: Actionable tips on how to stand out to each specific client.
- **Instant Proposal Draft**: One-tap pre-written winning proposals ready to copy or customize.

### 3. 📊 Lead Pipeline & CRM (`Pipeline CRM`)
- **Persistent Local Database**: Built on **Room Database** for offline reliability.
- **Deal Progression**: Move leads through pipeline stages (*Prospect* → *Pitched* → *In Discussion* → *Closed Won*).
- **Client Notes & Logs**: Save meeting notes, agreed deliverables, and follow-up dates.

### 4. ✍️ AI Outreach & Proposal Studio (`Pitch Studio`)
- **Tailored Message Crafter**: Generate high-converting cold emails, LinkedIn DMs, and proposals.
- **Selectable Tones**:
  - *Consultative & Helpful*
  - *Direct Executive ROI*
  - *Free Audit Hook*
  - *Casual & Friendly*
- **1-Tap Actions**: Copy to clipboard, launch default email app (Gmail/Outlook), or share directly to WhatsApp/Telegram.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture Repository Pattern
- **Local Persistence**: Android Room Database (Entities, DAO, Flow)
- **AI Engine**: Google Gemini API (`gemini-3.5-flash`)
- **Networking**: OkHttp 4
- **Concurrency**: Kotlin Coroutines & StateFlow / SharedFlow
- **Design & Assets**: Custom adaptive launcher icon & 3D holographic hero banner

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 36 (minSdk: 24, targetSdk: 36)
- JDK 17 or JDK 21

### Configuration
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/skillradar.git
   cd skillradar
   ```
2. Set up your Gemini API Key:
   - In Google AI Studio, add `GEMINI_API_KEY` to the **Secrets panel**.
   - Or create a `.env` file in the project root:
     ```env
     GEMINI_API_KEY=your_actual_gemini_api_key_here
     ```

### Building & Running
- **Compile Debug APK**:
  ```bash
  gradle assembleDebug
  ```
- **Run Unit & Robolectric Tests**:
  ```bash
  gradle testDebugUnitTest
  ```
- **Build Release App Bundle (for Google Play)**:
  ```bash
  gradle :app:bundleRelease
  ```

---

## 🌍 Supported Countries (126+ Nations)

SkillRadar includes verified business hubs and flag identifiers across:
- **North America**: United States, Canada, Mexico, Costa Rica, Panama, Dominican Republic, Jamaica, Guatemala, El Salvador, Honduras, Trinidad & Tobago, Bahamas, Barbados.
- **Europe**: United Kingdom, Germany, France, Italy, Spain, Netherlands, Switzerland, Sweden, Norway, Denmark, Finland, Ireland, Poland, Belgium, Austria, Portugal, Greece, Czech Republic, Hungary, Romania, Croatia, Serbia, Bulgaria, Slovakia, Slovenia, Estonia, Latvia, Lithuania, Iceland, Luxembourg, Cyprus, Malta, Albania, Ukraine, Monaco, Liechtenstein.
- **Asia & Middle East**: UAE, Saudi Arabia, Qatar, Kuwait, Bahrain, Oman, India, Singapore, Japan, South Korea, Indonesia, Malaysia, Thailand, Vietnam, Philippines, Pakistan, Bangladesh, Sri Lanka, Turkey, Israel, Jordan, Lebanon, Iraq, Kazakhstan, Uzbekistan, Taiwan, Hong Kong, Nepal, Maldives.
- **Latin America**: Brazil, Argentina, Chile, Colombia, Peru, Uruguay, Ecuador, Bolivia, Paraguay.
- **Africa**: South Africa, Nigeria, Kenya, Egypt, Morocco, Ghana, Ethiopia, Tanzania, Uganda, Rwanda, Ivory Coast, Senegal, Algeria, Tunisia, Botswana, Namibia, Zambia, Zimbabwe, Mauritius.
- **Oceania**: Australia, New Zealand, Fiji, Papua New Guinea.

---

## 📦 Publishing & Deployment

### Google Play Store
1. Generate an upload keystore (`upload.jks`).
2. Build the Android App Bundle (`app-release.aab`).
3. Create an application in [Google Play Console](https://play.google.com/console).
4. Fill in the Store Listing details, upload screenshots and feature graphics, and submit for review.

### Web Deployment
SkillRadar is web-streamed automatically via Google AI Studio's Cloud Emulator. You can share the preview link directly or embed it in any portfolio/website using an `<iframe>`.

---

## 📄 License
This project is licensed under the Apache 2.0 License.
