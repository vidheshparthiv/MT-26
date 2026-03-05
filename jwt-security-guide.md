# JWT Security - Complete Guide & Postman Testing

## 🔐 How JWT Security Works in Your Application

### JWT Overview
JWT (JSON Web Token) is a stateless authentication mechanism:
- **No sessions** stored on server
- **Token-based** security
- **Stateless** = scalable
- **Self-contained** = includes user info inside token

### JWT Structure
A token looks like: `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTY4OTAwMDAwMCwiZXhwIjoxNjg5MDg2NDAwfQ.signature`

Three parts separated by `.`:
- **Header** - Algorithm (HS256)
- **Payload** - User data and expiration
- **Signature** - Security verification

---

## 🏗️ Your Application Architecture

### 1. **Authentication Flow**
```
User → Register/Login → JwtService generates token → User gets JWT
User → Request + JWT → JwtAuthenticationFilter validates → Access granted
```

### 2. **Key Components**

#### `JwtService.java` (Security Token Manager)
```java
✅ generateToken() - Creates JWT token
✅ extractUsername() - Reads username from token
✅ isTokenValid() - Validates token signature & expiration
✅ isTokenExpired() - Checks if token time is up
```

**Configuration:**
- `jwt.secret` = 256-bit key for signing
- `jwt.expiration` = 86400000ms (24 hours)

#### `JwtAuthenticationFilter.java` (Request Interceptor)
```java
Flow:
1. Intercepts every HTTP request
2. Extracts Authorization header (Bearer token)
3. Validates token using JwtService
4. Sets authentication in SecurityContext if valid
5. Passes to next filter
```

#### `CustomUserDetailsService.java` (User Loader)
```java
Flow:
1. Loads user from database by username
2. Sets user roles as authorities
3. Returns UserDetails object to Spring Security
```

#### `SecurityConfig.java` (Security Rules)
```java
✅ Disables CSRF (JWT is stateless, no CSRF needed)
✅ Sets stateless session (no session cookies)
✅ Permits: /api/auth/login, /api/auth/register
✅ Requires authentication for all other endpoints
✅ Configures JWT filter
```

#### `AuthController.java` (API Endpoints)
```java
endpoints:
- POST /api/auth/register - Create new user
- POST /api/auth/login - Authenticate & get token
```

---

## 📊 Complete Request/Response Flow

### User Registration
```
Client: POST /api/auth/register
Body: { "username": "john", "password": "pass123" }
         ↓
AuthController.register()
         ↓
1. Check if user exists
2. Hash password with BCrypt
3. Save to database with ROLE_USER
         ↓
Server: 201 CREATED
Body: { "message": "User registered successfully", "username": "john", "userId": 1 }
```

### User Login
```
Client: POST /api/auth/login
Body: { "username": "john", "password": "pass123" }
         ↓
AuthController.login()
         ↓
1. AuthenticationManager validates username/password
2. JwtService generates token from username
3. Returns token with user info
         ↓
Server: 200 OK
Body: { 
    "token": "eyJhbGciOiJIUzI1NiJ9....",
    "username": "john",
    "userId": 1
}
```

### Protected Request with Token
```
Client: GET /api/protected
Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....
         ↓
JwtAuthenticationFilter
         ↓
1. Extract token from Authorization header
2. Extract username from token
3. Load user from database
4. Validate token signature & expiration
5. Set authentication in SecurityContext
         ↓
Server: 200 OK (if user authenticated)
```

---

## 🗄️ Database Schema

The app auto-creates this table:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt hashed
    role VARCHAR(50) NOT NULL         -- ROLE_USER or ROLE_ADMIN
);
```

**Example Data:**
```sql
INSERT INTO users (username, password, role) VALUES
('john', '$2a$10$...[bcrypt_hash]...', 'ROLE_USER'),
('admin', '$2a$10$...[bcrypt_hash]...', 'ROLE_ADMIN');
```

---

## 🧪 Postman Setup & Testing

### Step 1: Create a Postman Collection

1. Open Postman
2. Click **Collections** → **+ New Collection**
3. Name it: `JWT Auth API`
4. Save it

---

### Step 2: Create Environment Variables

1. Click **Environments** → **+ Create New Environment**
2. Name it: `Local Development`
3. Add variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `token` | `` | `` |
| `username` | `` | `` |
| `userId` | `` | `` |

4. Click **Save**
5. Select this environment in the top-right dropdown

---

### Step 3: Register New User

1. Click **+** to create new request
2. **Name:** `Register User`
3. **Method:** `POST`
4. **URL:** `{{base_url}}/api/auth/register`
5. **Tab: Body** → Select `raw` → Choose `JSON`
6. **Body:**
```json
{
    "username": "john_doe",
    "password": "secure_password_123"
}
```
7. Click **Send**

**Expected Response (201):**
```json
{
    "message": "User registered successfully",
    "username": "john_doe",
    "userId": "1"
}
```

---

### Step 4: Login & Get Token

1. Click **+** to create new request
2. **Name:** `Login User`
3. **Method:** `POST`
4. **URL:** `{{base_url}}/api/auth/login`
5. **Tab: Body** → Select `raw` → Choose `JSON`
6. **Body:**
```json
{
    "username": "john_doe",
    "password": "secure_password_123"
}
```
7. Click **Send**

**Expected Response (200):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTY0MzcxMjAwMCwiZXhwIjoxNjQzNzk4NDAwfQ.signature",
    "username": "john_doe",
    "userId": 1
}
```

### Step 5: Save Token to Environment

Add a test script to automatically save the token:

1. Click request → **Tab: Tests**
2. Paste:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
    pm.environment.set("username", jsonData.username);
    pm.environment.set("userId", jsonData.userId);
    console.log("Token saved: " + jsonData.token);
}
```
3. Click **Send** again

✅ Your token is now saved in `{{token}}` variable!

---

### Step 6: Create Protected Request

1. Click **+** to create new request
2. **Name:** `Get User Profile (Protected)`
3. **Method:** `GET`
4. **URL:** `{{base_url}}/api/user/profile` (example protected endpoint)
5. **Tab: Headers**
6. Add header:
   - **Key:** `Authorization`
   - **Value:** `Bearer {{token}}`
7. Click **Send**

**Expected Response (200):**
```json
{
    "message": "Welcome user_john_doe"
}
```

---

### Step 7: Test Without Token (Should Fail)

1. Create new request
2. **Name:** `Unauthorized Request`
3. **Method:** `GET`
4. **URL:** `{{base_url}}/api/user/profile`
5. **Don't add Authorization header**
6. Click **Send**

**Expected Response (401):**
```json
{
    "error": "Unauthorized"
}
```

---

### Step 8: Test With Invalid Token

1. Create new request
2. **Name:** `Invalid Token Test`
3. **Method:** `GET`
4. **URL:** `{{base_url}}/api/user/profile`
5. **Tab: Headers**
6. Add header:
   - **Key:** `Authorization`
   - **Value:** `Bearer invalid_token_12345`
7. Click **Send**

**Expected Response (401):**
```json
{
    "error": "Unauthorized"
}
```

---

## 🔄 Complete Postman Workflow

**Sequence for testing:**

1. **Register User** → Get userId
2. **Login User** → Get JWT token (auto-saved to `{{token}}`)
3. **Protected Request** → Use `Bearer {{token}}` header
4. **Multiple Protected Requests** → All use same token
5. **Token Expires** → Login again to get new token

---

## 🔐 Security Features Explained

### 1. **Password Security**
```java
// PasswordEncoder (BCryptPasswordEncoder)
"secure_password_123" → "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3PkcpHnnnC2..." 
// Never stores plain text!
```

### 2. **JWT Signature Verification**
```java
// Token is signed with secret key
// Server can verify no one tampered with token
// If someone changes payload, signature becomes invalid
```

### 3. **Token Expiration**
```java
// Created at: 10:00 AM
// Expires at: 10:00 AM next day (24 hours)
// After expiration, token is rejected - must login again
```

### 4. **Stateless Authentication**
```java
// No session stored on server
// Token contains all info needed
// Scales horizontally - any server can validate token
```

### 5. **Authorization (Roles)**
```java
// User has role: ROLE_USER or ROLE_ADMIN
// Can restrict endpoints by role using @PreAuthorize
@PreAuthorize("hasRole('ROLE_ADMIN')")
public void adminOnly() { ... }
```

---

## 📝 Token Payload Example (Decoded)

If you decode the token at [jwt.io](https://jwt.io), you'll see:

```json
{
  "sub": "john_doe",
  "iat": 1643712000,
  "exp": 1643798400
}
```

- `sub` = Subject (username)
- `iat` = Issued at (timestamp)
- `exp` = Expiration (timestamp)

---

## 🚨 Error Responses

| Scenario | Status | Response |
|----------|--------|----------|
| User already exists | 400 | `{"error": "Username already exists"}` |
| Invalid credentials | 401 | `{"error": "Invalid username or password"}` |
| Missing token | 401 | `{"error": "Unauthorized"}` |
| Invalid token | 401 | `{"error": "Unauthorized"}` |
| Expired token | 401 | `{"error": "Unauthorized"}` |
| Token tampered | 401 | `{"error": "Unauthorized"}` |

---

## 🔧 Adding Protected Endpoints

To protect additional endpoints, add this to a controller:

```java
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Get user role
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        
        return ResponseEntity.ok(new UserProfile(username, roles));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout() {
        // Stateless auth - just discard token on client
        return ResponseEntity.ok(new Message("Logged out successfully"));
    }
}
```

---

## 📱 Full Testing Checklist

- [ ] Register new user
- [ ] Login and get token
- [ ] Use token in protected request
- [ ] Verify token is saved in Postman environment
- [ ] Test without token (should get 401)
- [ ] Test with invalid token (should get 401)
- [ ] Test with expired token (wait 24 hours or modify jwt.expiration)
- [ ] Register duplicate user (should fail)
- [ ] Login with wrong password (should fail)

---

## 💡 Key Takeaways

1. **JWT is stateless** - No server-side session storage
2. **Token contains user info** - Username is inside token
3. **Token is signed** - Can't be forged or modified
4. **Token expires** - Must login again after expiration
5. **Roles control access** - Different endpoints for different users
6. **Bearer format** - Always use `Authorization: Bearer {token}`

Ready to test? Open Postman and follow the steps above! 🚀
