# Demo data seed script

`seed-demo.sh` populates a running NegoCore backend with a realistic dataset for
recruiters/reviewers to explore, using **only the public HTTP API** — it never
touches the database directly (no SQL, no Flyway/JPA, no DB credentials).

## Requirements

- `bash`, `curl`, `jq`
- A running NegoCore backend reachable at `API_BASE_URL`

## Usage

```bash
API_BASE_URL=http://localhost:8080 ./scripts/seed-demo.sh
```

All configuration is via environment variables, all optional:

| Variable        | Default                  | Description                    |
|-----------------|---------------------------|---------------------------------|
| `API_BASE_URL`  | `http://localhost:8080`  | Base URL of the backend         |
| `DEMO_NAME`     | `Demo Recruiter`         | Demo user's display name        |
| `DEMO_EMAIL`    | `demo@negocore.dev`      | Demo user's login email         |
| `DEMO_PASSWORD` | `Demo12345`              | Demo user's login password      |
| `DEMO_PHONE`    | `3001234567`             | Demo user's phone number        |

The script fails fast (`set -euo pipefail`) and prints the response body of the
first request that returns a non-2xx status, so a failed run stops immediately
instead of leaving a half-seeded, confusing dataset silently.

## What it creates

Against one demo user/business, in this order:

1. **User**: registers `DEMO_EMAIL`; if it already exists (HTTP 409), logs in
   instead, so a re-run doesn't fail — see "Limitations" below for what a
   re-run actually does past this point.
2. **Business**: "Tienda Demo NegoCore" (COP).
3. **3 categories**: Bebidas, Abarrotes, Limpieza.
4. **5 products** spread across those categories, with realistic cost/sale
   prices and starting stock.
5. **3 clients** and **2 providers**.
6. **2 purchases**: one paid in full, one paid partially (creates a
   `Payable`), which then receives a second partial payment so the payable
   ends up `PARTIAL` rather than fully settled — a more representative demo
   state than either extreme.
7. **3 sales**: two paid in full (no client), one partial sale tied to a
   client (creates a `Debt`), which then receives a partial payment the same
   way, for the same reason.
8. **1 direct loan** (a `Debt` with no associated sale) for a second client.
9. **2 expenses** (rent, utilities).
10. **1 order converted to a purchase** (create order → add item → convert),
    exercising the order-to-purchase flow.
11. **1 order converted to a sale** (create order → add item with a client →
    convert that item to a sale), exercising the order-to-sale flow, paid in
    full.
12. **1 quote**.

At the end it prints the demo login (email/password) and the created
business id.

## Limitations

- **Not idempotent past login.** The API has no bulk-reset or cascade-delete
  endpoint for a business, so re-running the script against the same backend
  will log into the same demo user but create a **second** business with its
  own categories/products/clients/etc. If you need a clean slate, either seed
  a fresh database, or manually delete via the API the business(es) created
  by previous runs before re-seeding — there's no dedicated cleanup script.
- **Production is not for testing.** Every trial/test run of this script —
  checking that it still works, trying a change, a dry run — must target a
  local or Docker backend, never production. The only time `API_BASE_URL`
  should point at the production backend is a single, deliberate, supervised
  run to create the real recruiter-facing demo account, and only after the
  script has already been verified end-to-end locally. It is not something to
  run casually or repeatedly against production.
- **Amounts are hand-computed.** `paidAmount`/`unitCost` totals in the script
  are calculated by hand to match `quantity * unitCost`/`salePrice`, since the
  API rejects a `paidAmount` that exceeds the total. If you change a
  quantity or price in the script, update the corresponding paid amount too.
