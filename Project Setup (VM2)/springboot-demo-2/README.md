# App 2 - payment-service (distributed tracing demo)

App 1 (`springboot-demo`) calls this service over a **Feign client**. The trace
context propagates across the HTTP call, so in Tempo you see ONE trace that
spans BOTH services.

## Trace shape

```
springboot-demo   GET /api/checkout-remote
  checkout-remote
    reserve-inventory                 (local, App 1)
    GET /api/payment/charge           (Feign client span, App 1)
payment-service   GET /api/payment/charge   (server span, App 2)
  process-payment                     (App 2 - PASSES or FAILS here)
```

## Deploy (on VM-2)

The shared `observability` network and Alloy are already running. You must
rebuild BOTH apps (App 1 gained the Feign client; App 2 is new).

```bash
# App 2 (this folder)
cd ~/docker-file/springboot-demo-2
docker compose up -d --build

# App 1 (rebuild for the new Feign endpoint)
cd ~/docker-file/springboot-demo
docker compose up -d --build
```

No Alloy change needed - it auto-discovers `springboot-demo-2` (label
`metrics.scrape=true` + `observability` network) for metrics, tails its logs,
and receives its traces on `alloy:4318`.

Quick checks:
```bash
docker compose ps
curl "http://localhost:8381/api/payment/charge?customer=alice&amount=250"   # App 2 direct
```

## The two demo requests (hit App 1)

```bash
# PASSES — App 1 -> App 2, payment approved
curl "http://localhost:8380/api/checkout-remote?customer=alice&amount=250"

# FAILS — App 1 -> App 2, payment DECLINED in App 2 (amount over 5000)
curl "http://localhost:8380/api/checkout-remote?customer=bob&amount=9999"
```

alice returns `CONFIRMED` with a nested payment block from `payment-service`.
bob returns `402` (App 1 surfaces App 2's decline).

## See it in Tempo (VM-1, http://192.168.10.110:3000)

Explore -> Tempo -> TraceQL:

```
{ name = "process-payment" && status = error }     # bob — failed in payment-service
```
```
{ name = "process-payment" && status != error }     # alice — passed in payment-service
```

Open a trace. You will see two services in the waterfall:
**springboot-demo** (top spans) and **payment-service** (bottom span
`process-payment`). For bob, the `process-payment` span (App 2) is red.

Also try the **Service Graph** tab in Tempo - it draws
`springboot-demo -> payment-service` as a node diagram.

## Config notes

- Service name in Tempo = `payment-service` (env `SPRING_APPLICATION_NAME`).
- App 1 finds App 2 at `http://springboot-demo-2:8080` (env `PAYMENT_SERVICE_URL`).
- Container: `springboot-demo-2`, host port `8381` -> internal `8080`.
- The two original App 1 endpoints (`/api/orders`, `/api/checkout`) are unchanged.
