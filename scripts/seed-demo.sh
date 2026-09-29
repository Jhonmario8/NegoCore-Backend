#!/usr/bin/env bash
#
# Seeds a NegoCore backend with a realistic demo dataset for recruiters,
# using only the public HTTP API (no direct SQL, no database access).
#
# Requirements: curl, jq
#
# Configuration (env vars, all optional):
#   API_BASE_URL   Base URL of the running backend (default: http://localhost:8080)
#   DEMO_NAME      Demo user's display name   (default: Demo Recruiter)
#   DEMO_EMAIL     Demo user's login email    (default: demo@negocore.dev)
#   DEMO_PASSWORD  Demo user's login password (default: Demo12345)
#   DEMO_PHONE     Demo user's phone number   (default: 3001234567)
#
# Usage:
#   API_BASE_URL=http://localhost:8080 ./scripts/seed-demo.sh
#
# See scripts/README-seed-demo.md for what this creates and its limitations.

set -euo pipefail

API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
DEMO_NAME="${DEMO_NAME:-Demo Recruiter}"
DEMO_EMAIL="${DEMO_EMAIL:-demo@negocore.dev}"
DEMO_PASSWORD="${DEMO_PASSWORD:-Demo12345}"
DEMO_PHONE="${DEMO_PHONE:-3001234567}"

TOKEN=""

log() {
    echo "[seed-demo] $*"
}

# api METHOD PATH [JSON_BODY]
# Prints the response body on stdout. Exits the script on any non-2xx status.
api() {
    local method="$1" path="$2" body="${3:-}"
    local url="${API_BASE_URL}${path}"
    local response status http_body
    local -a curl_args=(-sS -X "$method" "$url" -H "Content-Type: application/json")

    if [[ -n "$TOKEN" ]]; then
        curl_args+=(-H "Authorization: Bearer ${TOKEN}")
    fi
    if [[ -n "$body" ]]; then
        curl_args+=(-d "$body")
    fi

    response="$(curl "${curl_args[@]}" -w $'\n%{http_code}')"
    status="${response##*$'\n'}"
    http_body="${response%$'\n'*}"

    if [[ "$status" -lt 200 || "$status" -ge 300 ]]; then
        echo "[seed-demo] ERROR: $method $path -> HTTP $status" >&2
        echo "[seed-demo] response body: $http_body" >&2
        exit 1
    fi

    echo "$http_body"
}

require_tools() {
    for tool in curl jq; do
        if ! command -v "$tool" >/dev/null 2>&1; then
            echo "[seed-demo] ERROR: '$tool' is required but not installed." >&2
            exit 1
        fi
    done
}

register_or_login_demo_user() {
    log "Registering demo user ${DEMO_EMAIL}..."
    local register_body register_status
    register_body="$(jq -n \
        --arg name "$DEMO_NAME" \
        --arg email "$DEMO_EMAIL" \
        --arg phoneNumber "$DEMO_PHONE" \
        --arg password "$DEMO_PASSWORD" \
        '{name: $name, email: $email, phoneNumber: $phoneNumber, password: $password}')"

    register_status="$(curl -sS -o /dev/null -w '%{http_code}' -X POST "${API_BASE_URL}/auth/register" \
        -H "Content-Type: application/json" -d "$register_body")"

    if [[ "$register_status" == "201" ]]; then
        log "Demo user created."
    elif [[ "$register_status" == "409" ]]; then
        log "Demo user already exists, will log in instead."
    else
        echo "[seed-demo] ERROR: POST /auth/register -> HTTP $register_status" >&2
        exit 1
    fi

    log "Logging in as ${DEMO_EMAIL}..."
    local login_body login_response
    login_body="$(jq -n --arg email "$DEMO_EMAIL" --arg password "$DEMO_PASSWORD" \
        '{email: $email, password: $password}')"
    login_response="$(api POST /auth/login "$login_body")"
    TOKEN="$(echo "$login_response" | jq -r '.token')"
    log "Logged in."
}

create_business() {
    log "Creating business 'Tienda Demo NegoCore'..."
    local body response
    body='{"name": "Tienda Demo NegoCore", "currency": "COP"}'
    response="$(api POST /businesses "$body")"
    echo "$response" | jq -r '.id'
}

create_category() {
    local business_id="$1" name="$2"
    local body response
    body="$(jq -n --arg name "$name" '{name: $name}')"
    response="$(api POST "/businesses/${business_id}/categories" "$body")"
    echo "$response" | jq -r '.id'
}

create_product() {
    local business_id="$1" category_id="$2" name="$3" sku="$4" cost="$5" price="$6" stock="$7" min_stock="$8"
    local body response
    body="$(jq -n \
        --argjson categoryId "$category_id" \
        --arg name "$name" \
        --arg sku "$sku" \
        --argjson costPrice "$cost" \
        --argjson salePrice "$price" \
        --argjson stock "$stock" \
        --argjson minStockAlert "$min_stock" \
        '{categoryId: $categoryId, name: $name, sku: $sku, costPrice: $costPrice, salePrice: $salePrice, stock: $stock, minStockAlert: $minStockAlert}')"
    response="$(api POST "/businesses/${business_id}/products" "$body")"
    echo "$response" | jq -r '.id'
}

create_client() {
    local business_id="$1" name="$2" phone="$3" email="$4"
    local body response
    body="$(jq -n --arg name "$name" --arg phone "$phone" --arg email "$email" \
        '{name: $name, phone: $phone, email: $email}')"
    response="$(api POST "/businesses/${business_id}/clients" "$body")"
    echo "$response" | jq -r '.id'
}

create_provider() {
    local business_id="$1" name="$2" phone="$3" email="$4"
    local body response
    body="$(jq -n --arg name "$name" --arg phone "$phone" --arg email "$email" \
        '{name: $name, phone: $phone, email: $email}')"
    response="$(api POST "/businesses/${business_id}/providers" "$body")"
    echo "$response" | jq -r '.id'
}

register_purchase() {
    # items_json: JSON array of {productId, quantity, unitCost}
    local business_id="$1" provider_id="$2" items_json="$3" payment_method="$4" paid_amount="$5"
    local body response
    body="$(jq -n \
        --argjson providerId "$provider_id" \
        --argjson purchaseItems "$items_json" \
        --arg paymentMethod "$payment_method" \
        --argjson paidAmount "$paid_amount" \
        '{providerId: $providerId, purchaseItems: $purchaseItems, paymentMethod: $paymentMethod, paidAmount: $paidAmount, shippingCost: 0}')"
    response="$(api POST "/businesses/${business_id}/purchases" "$body")"
    echo "$response" | jq -r '.purchase.id'
}

register_sale() {
    # items_json: JSON array of {productId, quantity}
    local business_id="$1" items_json="$2" payment_method="$3" paid_amount="$4" client_id="${5:-}"
    local body response
    if [[ -n "$client_id" ]]; then
        body="$(jq -n \
            --argjson saleItems "$items_json" \
            --arg paymentMethod "$payment_method" \
            --argjson paidAmount "$paid_amount" \
            --argjson clientId "$client_id" \
            '{saleItems: $saleItems, paymentMethod: $paymentMethod, paidAmount: $paidAmount, clientId: $clientId}')"
    else
        body="$(jq -n \
            --argjson saleItems "$items_json" \
            --arg paymentMethod "$payment_method" \
            --argjson paidAmount "$paid_amount" \
            '{saleItems: $saleItems, paymentMethod: $paymentMethod, paidAmount: $paidAmount}')"
    fi
    response="$(api POST "/businesses/${business_id}/sales" "$body")"
    echo "$response" | jq -r '.sale.id'
}

register_expense() {
    local business_id="$1" description="$2" amount="$3"
    local body
    body="$(jq -n --arg description "$description" --argjson amount "$amount" \
        '{description: $description, amount: $amount, paid: true}')"
    api POST "/businesses/${business_id}/expenses" "$body" >/dev/null
}

pay_debt_for_client() {
    local business_id="$1" client_id="$2" amount="$3"
    local debt_id body
    debt_id="$(api GET "/businesses/${business_id}/debts?clientId=${client_id}" \
        | jq -r '[.[] | select(.status == "PARTIAL" or .status == "PENDING")][0].id')"
    if [[ -z "$debt_id" || "$debt_id" == "null" ]]; then
        log "  no pending debt found for client ${client_id}, skipping payment"
        return
    fi
    body="$(jq -n --argjson amount "$amount" '{amount: $amount, paymentMethod: "CASH"}')"
    api POST "/businesses/${business_id}/debts/${debt_id}/payments" "$body" >/dev/null
    log "  paid ${amount} towards debt ${debt_id}"
}

pay_payable_for_provider() {
    local business_id="$1" provider_id="$2" amount="$3"
    local payable_id body
    payable_id="$(api GET "/businesses/${business_id}/payables?providerId=${provider_id}" \
        | jq -r '[.[] | select(.status == "PARTIAL" or .status == "PENDING")][0].id')"
    if [[ -z "$payable_id" || "$payable_id" == "null" ]]; then
        log "  no pending payable found for provider ${provider_id}, skipping payment"
        return
    fi
    body="$(jq -n --argjson amount "$amount" '{amount: $amount, paymentMethod: "CASH"}')"
    api POST "/businesses/${business_id}/payables/${payable_id}/payments" "$body" >/dev/null
    log "  paid ${amount} towards payable ${payable_id}"
}

seed_order_converted_to_purchase() {
    local business_id="$1" provider_id="$2" product_id="$3" unit_cost="$4" quantity="$5"
    local order_id items_json conversion_body

    log "Creating an order and converting it into a purchase..."
    order_id="$(api POST "/businesses/${business_id}/orders" | jq -r '.order.id')"

    local item_body
    item_body="$(jq -n --argjson productId "$product_id" --argjson quantity "$quantity" \
        '{productId: $productId, quantity: $quantity}')"
    api POST "/businesses/${business_id}/orders/${order_id}/items" "$item_body" >/dev/null

    items_json="$(jq -n --argjson productId "$product_id" --argjson unitCost "$unit_cost" \
        '[{productId: $productId, unitCost: $unitCost}]')"
    conversion_body="$(jq -n \
        --argjson providerId "$provider_id" \
        --argjson unitCosts "$items_json" \
        --argjson paidAmount "$((unit_cost * quantity))" \
        '{providerId: $providerId, unitCosts: $unitCosts, paymentMethod: "TRANSFER", paidAmount: $paidAmount, shippingCost: 0}')"
    api POST "/businesses/${business_id}/orders/${order_id}/convert" "$conversion_body" >/dev/null
    log "  order ${order_id} converted to a purchase"
}

seed_order_converted_to_sale() {
    local business_id="$1" client_id="$2" product_id="$3" quantity="$4" unit_sale_price="$5"
    local order_id item_id items_response sale_body

    log "Creating an order and converting one item into a sale..."
    order_id="$(api POST "/businesses/${business_id}/orders" | jq -r '.order.id')"

    local item_body
    item_body="$(jq -n --argjson productId "$product_id" --argjson quantity "$quantity" --argjson clientId "$client_id" \
        '{productId: $productId, quantity: $quantity, clientId: $clientId}')"
    items_response="$(api POST "/businesses/${business_id}/orders/${order_id}/items" "$item_body")"
    item_id="$(echo "$items_response" | jq -r '.items[-1].id')"

    sale_body="$(jq -n --argjson paidAmount "$((unit_sale_price * quantity))" \
        '{paymentMethod: "CASH", paidAmount: $paidAmount}')"
    api POST "/businesses/${business_id}/orders/${order_id}/items/${item_id}/convert-to-sale" "$sale_body" >/dev/null
    log "  order ${order_id} item ${item_id} converted to a sale (paid in full)"
}

generate_demo_quote() {
    local business_id="$1" product_id="$2"
    local body
    body="$(jq -n --argjson productId "$product_id" \
        '{clientName: "Cliente Cotizacion", validityDays: 15, items: [{productId: $productId, quantity: 3}]}')"
    api POST "/businesses/${business_id}/quotes" "$body" >/dev/null
}

main() {
    require_tools
    log "Target API: ${API_BASE_URL}"

    register_or_login_demo_user

    local business_id
    business_id="$(create_business)"
    log "Business id: ${business_id}"

    log "Creating categories..."
    local cat_bebidas cat_abarrotes cat_limpieza
    cat_bebidas="$(create_category "$business_id" "Bebidas")"
    cat_abarrotes="$(create_category "$business_id" "Abarrotes")"
    cat_limpieza="$(create_category "$business_id" "Limpieza")"

    log "Creating products..."
    local p_gaseosa p_agua p_arroz p_aceite p_detergente
    p_gaseosa="$(create_product "$business_id" "$cat_bebidas" "Gaseosa 1.5L" "BEB-001" 2500 4000 40 10)"
    p_agua="$(create_product "$business_id" "$cat_bebidas" "Agua 600ml" "BEB-002" 800 1500 60 15)"
    p_arroz="$(create_product "$business_id" "$cat_abarrotes" "Arroz 500g" "ABR-001" 1800 2800 50 10)"
    p_aceite="$(create_product "$business_id" "$cat_abarrotes" "Aceite 1L" "ABR-002" 6000 8500 25 5)"
    p_detergente="$(create_product "$business_id" "$cat_limpieza" "Detergente 1kg" "LIM-001" 5000 7500 20 5)"

    log "Creating clients..."
    local client_maria client_carlos client_ana
    client_maria="$(create_client "$business_id" "Maria Gomez" "3011112222" "maria.gomez@example.com")"
    client_carlos="$(create_client "$business_id" "Carlos Ruiz" "3022223333" "carlos.ruiz@example.com")"
    client_ana="$(create_client "$business_id" "Ana Torres" "3033334444" "ana.torres@example.com")"

    log "Creating providers..."
    local provider_distrialimentos provider_quimicosdelvalle
    provider_distrialimentos="$(create_provider "$business_id" "Distrialimentos SAS" "3044445555" "ventas@distrialimentos.example.com")"
    provider_quimicosdelvalle="$(create_provider "$business_id" "Quimicos del Valle" "3055556666" "ventas@quimicosdelvalle.example.com")"

    log "Registering purchases..."
    local purchase_items
    purchase_items="$(jq -n --argjson p1 "$p_gaseosa" --argjson p2 "$p_agua" \
        '[{productId: $p1, quantity: 30, unitCost: 2500}, {productId: $p2, quantity: 40, unitCost: 800}]')"
    # total = 30*2500 + 40*800 = 107000, paid in full
    register_purchase "$business_id" "$provider_distrialimentos" "$purchase_items" "TRANSFER" 107000 >/dev/null

    purchase_items="$(jq -n --argjson p1 "$p_detergente" \
        '[{productId: $p1, quantity: 15, unitCost: 5000}]')"
    register_purchase "$business_id" "$provider_quimicosdelvalle" "$purchase_items" "CASH" 40000 >/dev/null
    log "  second purchase paid partially (paidAmount 40000 of 75000), creating a payable"
    pay_payable_for_provider "$business_id" "$provider_quimicosdelvalle" 20000

    log "Registering sales..."
    local sale_items
    sale_items="$(jq -n --argjson p1 "$p_gaseosa" --argjson p2 "$p_arroz" \
        '[{productId: $p1, quantity: 4}, {productId: $p2, quantity: 2}]')"
    register_sale "$business_id" "$sale_items" "CASH" 21600 >/dev/null

    sale_items="$(jq -n --argjson p1 "$p_aceite" '[{productId: $p1, quantity: 2}]')"
    register_sale "$business_id" "$sale_items" "TRANSFER" 17000 >/dev/null

    sale_items="$(jq -n --argjson p1 "$p_agua" --argjson p2 "$p_detergente" \
        '[{productId: $p1, quantity: 5}, {productId: $p2, quantity: 1}]')"
    register_sale "$business_id" "$sale_items" "CARD" 5000 "$client_maria" >/dev/null
    log "  sale to Maria Gomez paid partially, creating a debt"
    pay_debt_for_client "$business_id" "$client_maria" 5000

    log "Registering a direct loan (debt without a sale)..."
    api POST "/businesses/${business_id}/debts" \
        "$(jq -n --argjson clientId "$client_carlos" '{clientId: $clientId, amount: 50000}')" \
        >/dev/null

    log "Registering expenses..."
    register_expense "$business_id" "Arriendo local" 900000
    register_expense "$business_id" "Servicios publicos" 250000

    seed_order_converted_to_purchase "$business_id" "$provider_distrialimentos" "$p_arroz" 1800 20
    seed_order_converted_to_sale "$business_id" "$client_ana" "$p_aceite" 1 8500

    log "Generating a demo quote..."
    generate_demo_quote "$business_id" "$p_gaseosa"

    log ""
    log "Done. Demo login:"
    log "  email:    ${DEMO_EMAIL}"
    log "  password: ${DEMO_PASSWORD}"
    log "  business: Tienda Demo NegoCore (id ${business_id})"
}

main "$@"
