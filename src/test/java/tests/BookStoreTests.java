package tests;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import models.BookModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import static org.awaitility.Awaitility.await;

public class BookStoreTests extends TestBase {
    @Test
    @DisplayName("Удалить книгу")
    void deleteBookTest() {
        TestData testData = new TestData();

        Response registerResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(testData.authData)
                .when()
                .post("/Account/v1/User")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .extract().response();

        Response authResponse = await().atMost(20, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                            Response response = given()
                                    .log().uri()
                                    .log().method()
                                    .log().body()
                                    .contentType(JSON)
                                    .body(testData.authData)
                                    .when()
                                    .post("/Account/v1/GenerateToken")
                                    .then()
                                    .log().status()
                                    .log().body()
                                    .statusCode(200)
                                    .extract().response();

                            String status = response.path("status");
                            String token = response.path("token");
                            return "Success".equals(status) && token != null ? response : null;
                        },
                Objects::nonNull);

        String token = authResponse.path("token");
        String expires = authResponse.path("expires");

        Response booksResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        List<BookModel> books = booksResponse.jsonPath().getList("books", BookModel.class);


        if (books == null && books.isEmpty()) {
            System.out.println("error");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(books.size());
        BookModel randomBook = books.get(randomIndex);

        String bookName = randomBook.title();
        String bookIsbn = randomBook.isbn();

        String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
                registerResponse.path("userID") , bookIsbn);

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(201);

        open("favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", registerResponse.path("userID")));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));

        open("profile");
        $(".ReactTable").shouldHave(text(bookName));
    }
}
