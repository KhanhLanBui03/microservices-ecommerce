#!/bin/bash
# SpringDoc & API Testing Examples
# Demonstrates how to use SpringDoc and test endpoints

echo "=== SpringDoc OpenAPI - Testing Guide ==="
echo ""

# ============================================
# 1. ACCESS SWAGGER UI
# ============================================
echo "1️⃣  ACCESSING SWAGGER UI"
echo "---"
echo "Open in browser:"
echo "  • Auth Service:      http://localhost:8001/swagger-ui.html"
echo "  • User Service:      http://localhost:8002/swagger-ui.html"
echo "  • Product Service:   http://localhost:8003/swagger-ui.html"
echo "  • Order Service:     http://localhost:8004/swagger-ui.html"
echo "  • Payment Service:   http://localhost:8005/swagger-ui.html"
echo "  • Inventory Service: http://localhost:8006/swagger-ui.html"
echo ""

# ============================================
# 2. GET OPENAPI SPECS
# ============================================
echo "2️⃣  GET OPENAPI SPECIFICATIONS"
echo "---"
echo ""
echo "# Get Product Service OpenAPI (JSON)"
echo "curl -X GET http://localhost:8003/v3/api-docs"
echo ""
echo "# Get Product Service OpenAPI (YAML)"
echo "curl -X GET http://localhost:8003/v3/api-docs.yaml"
echo ""

# ============================================
# 3. AUTHENTICATION FLOW
# ============================================
echo "3️⃣  AUTHENTICATION - GET JWT TOKEN"
echo "---"
echo ""
echo "# Register new user"
curl_cmd="curl -X POST http://localhost:8001/api/auth/register \\
  -H \"Content-Type: application/json\" \\
  -d '{
    \"username\": \"testuser\",
    \"password\": \"password123\",
    \"email\": \"test@example.com\"
  }'"
echo "$curl_cmd"
echo ""

echo "# Login and get JWT token"
curl_cmd="curl -X POST http://localhost:8001/api/auth/login \\
  -H \"Content-Type: application/json\" \\
  -d '{
    \"username\": \"admin\",
    \"password\": \"password\"
  }'"
echo "$curl_cmd"
echo ""
echo "# Response example:"
echo '{
  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",
  \"type\": \"Bearer\",
  \"expiresIn\": 3600
}'
echo ""
echo "⚠️  COPY THE TOKEN FOR NEXT REQUESTS"
echo ""

# ============================================
# 4. PRODUCT API - WITHOUT AUTH
# ============================================
echo "4️⃣  PRODUCT API - PUBLIC ENDPOINTS"
echo "---"
echo ""
echo "# Get all products (public, no auth needed)"
curl_cmd="curl -X GET http://localhost:8003/api/product \\
  -H \"Content-Type: application/json\""
echo "$curl_cmd"
echo ""

# ============================================
# 5. PRODUCT API - WITH AUTH (ADMIN ONLY)
# ============================================
echo "5️⃣  PRODUCT API - PROTECTED ENDPOINTS (ADMIN ONLY)"
echo "---"
echo ""
echo "# Create product (Admin only - requires JWT token)"
curl_cmd="curl -X POST http://localhost:8003/api/product \\
  -H \"Content-Type: application/json\" \\
  -H \"Authorization: Bearer YOUR_JWT_TOKEN\" \\
  -d '{
    \"name\": \"Laptop\",
    \"price\": 999.99,
    \"quantity\": 50,
    \"description\": \"High-performance laptop\"
  }'"
echo "$curl_cmd"
echo ""

echo "# Update product (Admin only)"
curl_cmd="curl -X PUT http://localhost:8003/api/product/1 \\
  -H \"Content-Type: application/json\" \\
  -H \"Authorization: Bearer YOUR_JWT_TOKEN\" \\
  -d '{
    \"name\": \"Gaming Laptop\",
    \"price\": 1499.99,
    \"quantity\": 30,
    \"description\": \"High-end gaming laptop\"
  }'"
echo "$curl_cmd"
echo ""

echo "# Delete product (Admin only)"
curl_cmd="curl -X DELETE http://localhost:8003/api/product/1 \\
  -H \"Authorization: Bearer YOUR_JWT_TOKEN\""
echo "$curl_cmd"
echo ""

# ============================================
# 6. USER API
# ============================================
echo "6️⃣  USER API - ENDPOINTS"
echo "---"
echo ""
echo "# Get user by ID (public)"
curl_cmd="curl -X GET http://localhost:8002/api/user/1 \\
  -H \"Content-Type: application/json\""
echo "$curl_cmd"
echo ""

echo "# Get current user (requires JWT token)"
curl_cmd="curl -X GET http://localhost:8002/api/user/me \\
  -H \"Authorization: Bearer YOUR_JWT_TOKEN\" \\
  -H \"X-User-Email: user@example.com\""
echo "$curl_cmd"
echo ""

echo "# Create new user"
curl_cmd="curl -X POST http://localhost:8002/api/user \\
  -H \"Content-Type: application/json\" \\
  -d '{
    \"fullName\": \"John Doe\",
    \"email\": \"john@example.com\",
    \"phone\": \"0123456789\",
    \"address\": \"123 Main St\"
  }'"
echo "$curl_cmd"
echo ""

# ============================================
# 7. ORDER API
# ============================================
echo "7️⃣  ORDER API - ENDPOINTS"
echo "---"
echo ""
echo "# Place order (User only - requires JWT token)"
curl_cmd="curl -X POST http://localhost:8004/api/order \\
  -H \"Content-Type: application/json\" \\
  -H \"Authorization: Bearer YOUR_JWT_TOKEN\" \\
  -d '{
    \"items\": [
      {\"productId\": 1, \"quantity\": 2},
      {\"productId\": 2, \"quantity\": 1}
    ],
    \"totalAmount\": 2499.97,
    \"shippingAddress\": \"123 Main St\"
  }'"
echo "$curl_cmd"
echo ""

echo "# Get order details (Admin only)"
curl_cmd="curl -X GET http://localhost:8004/api/order/1 \\
  -H \"Authorization: Bearer YOUR_JWT_TOKEN\""
echo "$curl_cmd"
echo ""

# ============================================
# 8. TESTING IN SWAGGER UI
# ============================================
echo "8️⃣  TESTING IN SWAGGER UI (STEP-BY-STEP)"
echo "---"
echo ""
echo "Step 1: Open http://localhost:8001/swagger-ui.html"
echo ""
echo "Step 2: Find Auth API section"
echo "        → Click 'POST /api/auth/login'"
echo "        → Click 'Try it out'"
echo "        → Enter credentials"
echo "        → Click 'Execute'"
echo ""
echo "Step 3: Copy the returned JWT token"
echo ""
echo "Step 4: Click the green 'Authorize' button (top right)"
echo "        → Paste: Bearer <your-jwt-token>"
echo "        → Click 'Authorize'"
echo ""
echo "Step 5: Now test protected endpoints"
echo "        → Find Product API section"
echo "        → Try 'POST /api/product' (Create product)"
echo "        → Fill in request body"
echo "        → Click 'Execute'"
echo ""

# ============================================
# 9. RESPONSE EXAMPLES
# ============================================
echo "9️⃣  RESPONSE EXAMPLES"
echo "---"
echo ""
echo "✅ Success: GET /api/product (200 OK)"
echo "[
  {
    \"id\": 1,
    \"name\": \"Laptop\",
    \"price\": 999.99,
    \"quantity\": 50,
    \"createdAt\": \"2026-01-21T10:00:00Z\"
  }
]"
echo ""

echo "❌ Error: POST /api/product without token (401 Unauthorized)"
echo "{
  \"timestamp\": \"2026-01-21T10:30:00Z\",
  \"status\": 401,
  \"error\": \"Unauthorized\",
  \"message\": \"Missing or invalid JWT token\"
}"
echo ""

echo "❌ Error: POST /api/product with USER role (403 Forbidden)"
echo "{
  \"timestamp\": \"2026-01-21T10:30:00Z\",
  \"status\": 403,
  \"error\": \"Forbidden\",
  \"message\": \"Access denied - ADMIN role required\"
}"
echo ""

# ============================================
# 10. ADVANCED TESTING
# ============================================
echo "🔟 ADVANCED TESTING TIPS"
echo "---"
echo ""
echo "💡 Save JWT token to variable:"
echo "   TOKEN=\$(curl -s -X POST http://localhost:8001/api/auth/login \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"username\":\"admin\",\"password\":\"password\"}' | jq -r '.token')"
echo ""

echo "💡 Use token in subsequent requests:"
echo "   curl -X GET http://localhost:8003/api/product/1 \\"
echo "     -H \"Authorization: Bearer \$TOKEN\""
echo ""

echo "💡 Pretty print JSON responses:"
echo "   curl -X GET http://localhost:8003/api/product | jq ."
echo ""

echo "💡 Extract specific fields:"
echo "   curl -X GET http://localhost:8003/api/product | jq '.[] | {id, name, price}'"
echo ""

echo "💡 Export OpenAPI spec:"
echo "   curl http://localhost:8003/v3/api-docs > product-api.json"
echo "   curl http://localhost:8003/v3/api-docs.yaml > product-api.yaml"
echo ""

# ============================================
# 11. TROUBLESHOOTING
# ============================================
echo "🔧 TROUBLESHOOTING"
echo "---"
echo ""
echo "❓ Swagger UI not loading:"
echo "   → Check if service is running on correct port"
echo "   → Try: curl http://localhost:8003/swagger-ui.html"
echo ""

echo "❓ 'Unauthorized' error:"
echo "   → Get JWT token first via /api/auth/login"
echo "   → Include 'Authorization: Bearer TOKEN' header"
echo ""

echo "❓ 'Forbidden' error:"
echo "   → User doesn't have required role"
echo "   → Admin endpoints need ADMIN role"
echo "   → User endpoints need USER role"
echo ""

echo "❓ OpenAPI spec not accessible:"
echo "   → Try: http://localhost:<port>/v3/api-docs"
echo "   → Check service is running"
echo "   → Check SpringDoc dependency in pom.xml"
echo ""

echo ""
echo "✅ All endpoints are documented and ready for testing!"
echo ""
