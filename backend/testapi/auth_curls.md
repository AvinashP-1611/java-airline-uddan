# Auth & User API Test Commands

## 1. Signup (Register New User)
```bash
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

## 2. Login
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

## 3. Get All Users (Admin Only) - Requires Authentication
*Note: Since we are using Session/Basic auth for now (or stateless if JWT was fully implemented), testing strictly secured endpoints via simple cURL without cookie/token management is tricky. If using Postman, simple Basic Auth usually works if `httpBasic` was enabled, or form login session.*

*However, for our current config where `httpBasic` and `formLogin` are disabled, we might strictly rely on the session created during login if testing in a browser or persisted, purely for API testing standard expectation is usually a Token (JWT).*
*But assuming you wanted to just hit the endpoint:*

```bash
curl -X GET http://localhost:9090/api/admin/users
```
*(This will likely fail with 403 Forbidden unless authenticated context is passed)*

## 4. Delete User (Admin Only)
```bash
curl -X DELETE http://localhost:9090/api/admin/users/1
```
