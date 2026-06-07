package com.project.api;

import com.project.utils.DbUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * UserApiTest — REST Assured API tests against JSONPlaceholder (free public API).
 * Demonstrates: status code, schema, field validation, and DB cross-validation pattern.
 *
 * JSONPlaceholder: https://jsonplaceholder.typicode.com — no auth needed, runs anywhere.
 *
 * Interview answer: "I tested REST APIs using REST Assured, validating status codes,
 * response schema, and then cross-validating with SQL queries against the backend DB."
 */
public class UserApiTest {

    private static final Logger log = LogManager.getLogger(UserApiTest.class);
    private static final String BASE_URI = "https://jsonplaceholder.typicode.com";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        log.info("API Base URI set: {}", BASE_URI);
    }

    @Test(description = "GET /users - verify 200 and list is not empty")
    public void testGetAllUsers() {
        log.info("Testing GET /users");

        given()
            .header("Content-Type", "application/json")
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].id",    notNullValue())
            .body("[0].name",  notNullValue())
            .body("[0].email", notNullValue());

        log.info("GET /users - PASSED");
    }

    @Test(description = "GET /users/{id} - verify specific user fields")
    public void testGetUserById() {
        log.info("Testing GET /users/1");

        Response response = given()
            .pathParam("id", 1)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200)
            .extract().response();

        // Extract and validate fields from response
        String name  = response.jsonPath().getString("name");
        String email = response.jsonPath().getString("email");
        int    id    = response.jsonPath().getInt("id");

        Assert.assertEquals(id, 1, "User ID should be 1");
        Assert.assertNotNull(name,  "Name should not be null");
        Assert.assertTrue(email.contains("@"), "Email should contain @");

        log.info("User fetched — name: {}, email: {}", name, email);

        // ============================================================
        // DB CROSS-VALIDATION PATTERN (commented — no real DB here)
        // In real project: after API returns user data, verify DB matches
        // ============================================================
        // String dbEmail = DbUtils.getSingleValue(
        //     "SELECT email FROM users WHERE id = 1", "email");
        // Assert.assertEquals(email, dbEmail,
        //     "API response email should match DB record");
        // log.info("API vs DB validation PASSED for user id=1");
    }

    @Test(description = "POST /posts - verify 201 and response body")
    public void testCreatePost() {
        log.info("Testing POST /posts");

        String requestBody = "{"
                + "\"title\": \"Automation Test Post\","
                + "\"body\": \"Created by Selenium framework\","
                + "\"userId\": 1"
                + "}";

        Response response = given()
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body("title",  equalTo("Automation Test Post"))
            .body("userId", equalTo(1))
            .body("id",     notNullValue())
            .extract().response();

        int createdId = response.jsonPath().getInt("id");
        log.info("POST /posts - PASSED. Created post id: {}", createdId);
    }

    @Test(description = "GET /users/999 - verify 404 for non-existent resource")
    public void testGetNonExistentUser() {
        log.info("Testing GET /users/999 — expect 404");

        given()
            .pathParam("id", 999)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(404);

        log.info("404 test - PASSED");
    }
}
