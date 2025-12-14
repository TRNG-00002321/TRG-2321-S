# Lab: First API Tests with Postman

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Beginner

In this lab, you'll build your first API test collection using Postman. You'll work with a REST API to perform CRUD (Create, Read, Update, Delete) operations and understand how different HTTP methods work in practice.

---

## Learning Objectives

By completing this lab, you will:
- Send GET, POST, PUT, and DELETE requests using Postman
- Understand HTTP status codes and their meanings
- Work with request headers and JSON bodies
- Organize requests into a Postman collection
- Interpret API responses correctly

---

## Prerequisites

- Postman Desktop App installed and configured
- Basic understanding of HTTP methods (covered in `api-testing.md`)
- Familiarity with JSON format

---

## The Scenario

You've joined the QA team at **BookHaven**, an online bookstore. Your task is to test the Books API that powers the catalog system. The development team has provided you with access to a test API at JSONPlaceholder (simulating the real API).

---

## Core Tasks

### Task 1: Create Your Collection (5 minutes)

1. Open Postman
2. Create a new Collection named **"BookHaven API Tests"**
3. Add a description: "API tests for BookHaven catalog system - Week 7 Exercise"

### Task 2: GET All Resources (10 minutes)

Create a request to retrieve all posts (simulating books):

**Request Details:**
- Method: `GET`
- URL: `https://jsonplaceholder.typicode.com/posts`
- Name: `GET All Posts`

**Expected Results:**
- Status: `200 OK`
- Response: Array of 100 post objects
- Response time: Under 2 seconds

**Record your observations:**
1. What fields does each post object contain?
2. How many items are returned?
3. What headers are in the response?

### Task 3: GET Single Resource (10 minutes)

Create a request to retrieve a specific post:

**Request Details:**
- Method: `GET`
- URL: `https://jsonplaceholder.typicode.com/posts/1`
- Name: `GET Post by ID`

**Expected Results:**
- Status: `200 OK`
- Response: Single post object with id: 1

**Additional Challenge:**
- What happens if you request `/posts/999`? (non-existent ID)
- What status code do you receive?

### Task 4: GET with Query Parameters (10 minutes)

Create a request to filter posts by user:

**Request Details:**
- Method: `GET`
- URL: `https://jsonplaceholder.typicode.com/posts?userId=1`
- Name: `GET Posts by User`

**Try two approaches:**
1. Add the query parameter directly in the URL
2. Use the Params tab to add `userId` with value `1`

**Expected Results:**
- Status: `200 OK`
- Response: Array of posts where `userId` equals 1
- Count should be 10 posts

### Task 5: POST - Create Resource (15 minutes)

Create a request to add a new post:

**Request Details:**
- Method: `POST`
- URL: `https://jsonplaceholder.typicode.com/posts`
- Name: `CREATE New Post`
- Headers: `Content-Type: application/json`
- Body (raw JSON):

```json
{
    "title": "API Testing is Essential",
    "body": "Every QA engineer should master API testing as it provides fast, reliable validation of backend services.",
    "userId": 1
}
```

**Expected Results:**
- Status: `201 Created`
- Response: Your post data plus a new `id` field

**Questions to answer:**
1. What `id` was assigned to your new post?
2. How does 201 differ from 200?

### Task 6: PUT - Update Resource (10 minutes)

Create a request to update an existing post:

**Request Details:**
- Method: `PUT`
- URL: `https://jsonplaceholder.typicode.com/posts/1`
- Name: `UPDATE Post`
- Headers: `Content-Type: application/json`
- Body (raw JSON):

```json
{
    "id": 1,
    "title": "Updated Title - API Testing",
    "body": "This post has been updated via PUT request",
    "userId": 1
}
```

**Expected Results:**
- Status: `200 OK`
- Response: Updated post data

### Task 7: DELETE - Remove Resource (5 minutes)

Create a request to delete a post:

**Request Details:**
- Method: `DELETE`
- URL: `https://jsonplaceholder.typicode.com/posts/1`
- Name: `DELETE Post`

**Expected Results:**
- Status: `200 OK`
- Response: Empty object `{}`

---

## Definition of Done

Your lab is complete when you have:

- [ ] Created a collection with at least 6 requests
- [ ] Successfully executed GET (list and single), POST, PUT, DELETE
- [ ] Documented the expected status code for each request
- [ ] Answered all questions in the lab
- [ ] Saved all requests to your collection

---

## Stretch Goals (Optional)

1. **Explore Related Resources:**
   - GET comments for post 1: `/posts/1/comments`
   - GET users: `/users`
   - GET albums: `/albums`

2. **Test Error Scenarios:**
   - What happens with an invalid HTTP method?
   - Try POST without Content-Type header
   - Send invalid JSON in request body

3. **Organize Your Collection:**
   - Create folders: "Read Operations", "Write Operations"
   - Move requests into appropriate folders

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Collection "BookHaven API Tests" created | ☐ |
| GET All Posts - 200 OK | ☐ |
| GET Post by ID - 200 OK | ☐ |
| GET Posts by User - Filtered correctly | ☐ |
| CREATE New Post - 201 Created | ☐ |
| UPDATE Post - 200 OK | ☐ |
| DELETE Post - 200 OK | ☐ |
| All requests saved to collection | ☐ |

---

## Common Mistakes to Avoid

1. **Missing Content-Type header** for POST/PUT requests
2. **Incorrect JSON syntax** (missing quotes, trailing commas)
3. **Using wrong HTTP method** (GET instead of POST)
4. **Not saving requests** before moving to next task

---

## Additional Resources

- [JSONPlaceholder Guide](https://jsonplaceholder.typicode.com/guide/)
- [HTTP Status Codes Reference](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- Written Content: `api-testing.md`, `postman-overview.md`, `test-requests.md`

