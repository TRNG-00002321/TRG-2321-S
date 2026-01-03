# API Testing Interview Questions & Answers

## Beginner Level

**Q1: What is an API and why is API testing important?**

> **Answer:** An API (Application Programming Interface) is a set of protocols that allows different software applications to communicate with each other.
>
> **Why API testing matters:**
> - **Faster than UI testing:** No browser overhead
> - **More stable:** UI changes don't break tests
> - **Earlier detection:** Tests business logic directly
> - **Better coverage:** Can test error scenarios easily
> - **CI/CD friendly:** Quick feedback
>
> **API testing validates:**
> - Correct responses for valid requests
> - Proper error handling for invalid requests
> - Performance under load
> - Security (authentication, authorization)

---

**Q2: Explain HTTP methods and when to use each.**

> **Answer:**
>
> | Method | Purpose | Has Body | Idempotent |
> |--------|---------|----------|------------|
> | GET | Retrieve resource | No | Yes |
> | POST | Create resource | Yes | No |
> | PUT | Replace resource | Yes | Yes |
> | PATCH | Partial update | Yes | No |
> | DELETE | Remove resource | Optional | Yes |
>
> **Examples:**
> ```
> GET    /users/123        # Get user 123
> POST   /users            # Create new user
> PUT    /users/123        # Replace user 123
> PATCH  /users/123        # Update user 123 partially
> DELETE /users/123        # Delete user 123
> ```
>
> **Idempotent:** Same request multiple times = same result
> - GET same user 10 times → same user returned
> - DELETE same user → first succeeds, rest are no-ops
> - POST create user 10 times → 10 users created (not idempotent)

---

**Q3: What are HTTP status codes? Explain common ones.**

> **Answer:**
>
> **2xx Success:**
> | Code | Meaning | When |
> |------|---------|------|
> | 200 | OK | Successful GET/PUT |
> | 201 | Created | Successful POST |
> | 204 | No Content | Successful DELETE |
>
> **3xx Redirect:**
> | Code | Meaning |
> |------|---------|
> | 301 | Moved Permanently |
> | 302 | Found (Temporary) |
> | 304 | Not Modified |
>
> **4xx Client Errors:**
> | Code | Meaning | Example |
> |------|---------|---------|
> | 400 | Bad Request | Invalid JSON |
> | 401 | Unauthorized | Missing auth |
> | 403 | Forbidden | No permission |
> | 404 | Not Found | Resource doesn't exist |
> | 409 | Conflict | Duplicate entry |
> | 422 | Unprocessable | Validation failed |
>
> **5xx Server Errors:**
> | Code | Meaning |
> |------|---------|
> | 500 | Internal Server Error |
> | 502 | Bad Gateway |
> | 503 | Service Unavailable |

---

**Q4: What is REST and what makes an API RESTful?**

> **Answer:** REST (Representational State Transfer) is an architectural style for designing networked applications.
>
> **RESTful principles:**
>
> 1. **Stateless:** Each request contains all needed information
> 2. **Client-Server:** Separation of concerns
> 3. **Uniform Interface:** Consistent endpoints
> 4. **Resource-based:** URLs identify resources
> 5. **HTTP methods:** Standard verbs for actions
>
> **RESTful URL design:**
> ```
> Good:
> GET    /users            # List users
> GET    /users/123        # Get user 123
> POST   /users            # Create user
> PUT    /users/123        # Update user 123
> DELETE /users/123        # Delete user 123
> GET    /users/123/orders # Get user's orders
> 
> Bad:
> GET    /getUsers
> POST   /createUser
> GET    /deleteUser/123
> ```
>
> **Response format (JSON):**
> ```json
> {
>   "id": 123,
>   "name": "Alice",
>   "email": "alice@example.com",
>   "created_at": "2024-01-15T10:30:00Z"
> }
> ```

---

**Q5: How do you use Postman for API testing?**

> **Answer:**
>
> **Request components:**
> - Method (GET, POST, etc.)
> - URL
> - Headers (Content-Type, Authorization)
> - Body (for POST/PUT)
>
> **Writing tests in Postman:**
> ```javascript
> // Status code check
> pm.test("Status code is 200", function() {
>     pm.response.to.have.status(200);
> });
> 
> // Response time
> pm.test("Response time < 500ms", function() {
>     pm.expect(pm.response.responseTime).to.be.below(500);
> });
> 
> // JSON body
> pm.test("User name is correct", function() {
>     var json = pm.response.json();
>     pm.expect(json.name).to.eql("Alice");
>     pm.expect(json.email).to.include("@");
> });
> 
> // Save for next request
> var json = pm.response.json();
> pm.environment.set("userId", json.id);
> ```
>
> **Using variables:**
> ```
> {{baseUrl}}/users/{{userId}}
> 
> Scopes: Local > Data > Environment > Collection > Global
> ```

---

## Intermediate Level

**Q6: Explain authentication methods for APIs.**

> **Answer:**
>
> **1. API Key:**
> ```
> Header: X-API-Key: abc123xyz
> Query: ?api_key=abc123xyz
> ```
> - Simple but less secure
> - Good for server-to-server
>
> **2. Basic Auth:**
> ```
> Header: Authorization: Basic base64(username:password)
> ```
> - Simple but sends credentials each request
> - Always use with HTTPS
>
> **3. Bearer Token (JWT):**
> ```
> Header: Authorization: Bearer eyJhbG...
> ```
> - Token contains claims (user ID, roles)
> - Stateless authentication
> - Expires after period
>
> **4. OAuth 2.0:**
> ```
> 1. Client requests authorization
> 2. User authenticates with provider
> 3. Client receives access token
> 4. Client uses token for API calls
> ```
> - Industry standard
> - Supports different flows
>
> **Testing auth:**
> ```java
> // REST Assured with bearer token
> given()
>     .header("Authorization", "Bearer " + token)
> .when()
>     .get("/api/users")
> .then()
>     .statusCode(200);
> ```

---

**Q7: How do you test APIs with REST Assured (Java)?**

> **Answer:**
>
> ```java
> import static io.restassured.RestAssured.*;
> import static org.hamcrest.Matchers.*;
> 
> public class UserApiTest {
>     
>     @BeforeAll
>     static void setup() {
>         baseURI = "https://api.example.com";
>     }
>     
>     @Test
>     void getUser_shouldReturnUser() {
>         given()
>             .header("Authorization", "Bearer " + token)
>             .pathParam("id", 123)
>         .when()
>             .get("/users/{id}")
>         .then()
>             .statusCode(200)
>             .body("name", equalTo("Alice"))
>             .body("email", containsString("@"))
>             .body("roles", hasSize(2));
>     }
>     
>     @Test
>     void createUser_shouldReturn201() {
>         String requestBody = """
>             {
>                 "name": "Bob",
>                 "email": "bob@example.com"
>             }
>             """;
>         
>         given()
>             .contentType(ContentType.JSON)
>             .body(requestBody)
>         .when()
>             .post("/users")
>         .then()
>             .statusCode(201)
>             .body("id", notNullValue());
>     }
>     
>     @Test
>     void getUsers_withQueryParams() {
>         given()
>             .queryParam("page", 1)
>             .queryParam("limit", 10)
>             .queryParam("sort", "name")
>         .when()
>             .get("/users")
>         .then()
>             .statusCode(200)
>             .body("data", hasSize(lessThanOrEqualTo(10)));
>     }
> }
> ```

---

**Q8: How do you test APIs with Python requests?**

> **Answer:**
>
> ```python
> import requests
> import pytest
> 
> BASE_URL = "https://api.example.com"
> 
> class TestUserAPI:
>     
>     @pytest.fixture
>     def auth_headers(self):
>         return {"Authorization": f"Bearer {self.get_token()}"}
>     
>     def test_get_user(self, auth_headers):
>         response = requests.get(
>             f"{BASE_URL}/users/123",
>             headers=auth_headers
>         )
>         
>         assert response.status_code == 200
>         data = response.json()
>         assert data["name"] == "Alice"
>         assert "@" in data["email"]
>     
>     def test_create_user(self, auth_headers):
>         user_data = {
>             "name": "Bob",
>             "email": "bob@example.com"
>         }
>         
>         response = requests.post(
>             f"{BASE_URL}/users",
>             json=user_data,
>             headers=auth_headers
>         )
>         
>         assert response.status_code == 201
>         assert response.json()["id"] is not None
>     
>     def test_invalid_user_returns_404(self, auth_headers):
>         response = requests.get(
>             f"{BASE_URL}/users/99999",
>             headers=auth_headers
>         )
>         
>         assert response.status_code == 404
>     
>     def test_unauthorized_returns_401(self):
>         response = requests.get(f"{BASE_URL}/users")
>         assert response.status_code == 401
> ```

---

**Q9: What should an API test suite cover?**

> **Answer:**
>
> **Functional tests:**
> - Happy path (valid requests)
> - Invalid inputs (400 errors)
> - Missing required fields
> - Edge cases (empty, max length)
>
> **Security tests:**
> - Authentication required (401)
> - Authorization (403)
> - SQL injection
> - XSS prevention
>
> **Performance tests:**
> - Response time SLAs
> - Load handling
> - Timeout behavior
>
> **Test checklist:**
> ```
> ✓ Valid request returns correct data
> ✓ Invalid request returns appropriate error
> ✓ Missing required fields return 400
> ✓ Unauthorized request returns 401
> ✓ Forbidden request returns 403
> ✓ Non-existent resource returns 404
> ✓ Duplicate creation returns 409
> ✓ Response structure matches schema
> ✓ Response time meets SLA
> ✓ Pagination works correctly
> ✓ Filtering works correctly
> ✓ Sorting works correctly
> ```

---

**Q10: How do you mock APIs for testing?**

> **Answer:**
>
> **Why mock APIs:**
> - External API unavailable
> - Control test conditions
> - Test error scenarios
> - Faster tests
>
> **WireMock (Java):**
> ```java
> @ExtendWith(WireMockExtension.class)
> class ExternalServiceTest {
>     
>     @BeforeEach
>     void setup() {
>         stubFor(get(urlEqualTo("/api/users/1"))
>             .willReturn(aResponse()
>                 .withStatus(200)
>                 .withHeader("Content-Type", "application/json")
>                 .withBody("{\"id\": 1, \"name\": \"Alice\"}")));
>         
>         // Simulate timeout
>         stubFor(get(urlEqualTo("/api/slow"))
>             .willReturn(aResponse()
>                 .withFixedDelay(5000)));
>         
>         // Simulate error
>         stubFor(get(urlEqualTo("/api/error"))
>             .willReturn(aResponse()
>                 .withStatus(500)));
>     }
>     
>     @Test
>     void shouldHandleUserResponse() {
>         // Test code using mocked API
>     }
> }
> ```
>
> **responses (Python):**
> ```python
> import responses
> import requests
> 
> @responses.activate
> def test_user_api():
>     responses.add(
>         responses.GET,
>         "https://api.example.com/users/1",
>         json={"id": 1, "name": "Alice"},
>         status=200
>     )
>     
>     response = requests.get("https://api.example.com/users/1")
>     assert response.json()["name"] == "Alice"
> ```

---

## Advanced Level

**Q11: How do you test API contracts and schemas?**

> **Answer:**
>
> **JSON Schema validation:**
> ```java
> // REST Assured with JSON Schema
> given()
>     .get("/users/1")
> .then()
>     .body(matchesJsonSchemaInClasspath("user-schema.json"));
> ```
>
> **Schema file (user-schema.json):**
> ```json
> {
>   "$schema": "http://json-schema.org/draft-07/schema#",
>   "type": "object",
>   "required": ["id", "name", "email"],
>   "properties": {
>     "id": {"type": "integer"},
>     "name": {"type": "string", "minLength": 1},
>     "email": {"type": "string", "format": "email"},
>     "age": {"type": "integer", "minimum": 0}
>   }
> }
> ```
>
> **Contract testing (Pact):**
> ```java
> // Consumer side - defines expectations
> @Pact(consumer = "OrderService", provider = "UserService")
> public RequestResponsePact getUserPact(PactDslWithProvider builder) {
>     return builder
>         .given("user 1 exists")
>         .uponReceiving("a request for user 1")
>         .path("/users/1")
>         .method("GET")
>         .willRespondWith()
>         .status(200)
>         .body(new PactDslJsonBody()
>             .integerType("id", 1)
>             .stringType("name", "Alice"))
>         .toPact();
> }
> 
> // Provider side - verifies contract
> @PactVerification("UserService")
> void verifyPact() {
>     // Pact verifies actual API matches expected
> }
> ```
>
> **Benefits of contract testing:**
> - Catch breaking changes early
> - Document API expectations
> - Decouple team dependencies

---

**Q12: How do you implement API performance testing?**

> **Answer:**
>
> **JMeter basics:**
> ```
> Test Plan
> └── Thread Group (users)
>     ├── HTTP Request
>     ├── Response Assertion
>     └── Summary Report
> ```
>
> **Performance metrics:**
> - **Response time:** How long requests take
> - **Throughput:** Requests per second
> - **Error rate:** Percentage of failures
> - **Percentiles:** 95th, 99th response times
>
> **Gatling (Scala):**
> ```scala
> class ApiLoadTest extends Simulation {
>   
>   val httpProtocol = http
>     .baseUrl("https://api.example.com")
>     .header("Authorization", "Bearer ${token}")
>   
>   val getUsers = scenario("Get Users")
>     .exec(
>       http("Get all users")
>         .get("/users")
>         .check(status.is(200))
>         .check(responseTimeInMillis.lte(1000))
>     )
>   
>   setUp(
>     getUsers.inject(
>       rampUsers(100).during(60.seconds),  // Ramp to 100 users over 1 min
>       constantUsersPerSec(10).during(300.seconds)  // Maintain 10 users/sec for 5 min
>     )
>   ).protocols(httpProtocol)
>    .assertions(
>      global.responseTime.max.lt(2000),
>      global.successfulRequests.percent.gt(99)
>    )
> }
> ```
>
> **Load patterns:**
> - **Ramp-up:** Gradually increase load
> - **Steady state:** Constant load for duration
> - **Spike:** Sudden traffic increase
> - **Stress:** Increase until failure
