# iPhone GitHub + Render deployment — V15 FINAL

## GitHub

Upload the **contents of this ZIP** to the repository root. Do not upload the ZIP itself as the project source.

The important frontend path is:

`src/main/resources/static/index.html`

You do not need GitHub Pages for this version. The Spring Boot service serves the page itself.

## Render

Create a **Web Service** from the GitHub repository and choose **Docker** as the environment. The included `Dockerfile` builds and runs the Spring Boot app, so no laptop is required and no Maven setup is needed on the phone.

The app uses `PORT` when supplied by the hosting platform and otherwise defaults to 8080.

## Verify after deployment

Open the Render URL. The V15 FINAL backtest page should load directly.

Run the built-in **TEST RECOVERY** first. Expected chain:

`0.01 → 0.02 → 0.04 → 0.07`

Then run the historical backtest.

## Important P/L verification

For the previously reported calls:

- SELL 0.01, 62894 → 63144: Move **-250**, P/L **-2.5**
- SELL 0.04, 63081 → 63331: Move **-250**, P/L **-10**

The journal now displays both values separately so a raw price move cannot be mistaken for quantity-adjusted P/L.
