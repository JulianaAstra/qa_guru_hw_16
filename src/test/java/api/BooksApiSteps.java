package api;

import models.AddBookBodyModel;
import models.BooksListResponseModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.Spec.requestSpec;
import static specs.Spec.responseSpec;

public class BooksApiSteps {
    public BooksListResponseModel getBooksList() {
        return step("Make get books list request", () ->
                given()
                        .spec(requestSpec)
                        .when()
                        .get("/BookStore/v1/Books")
                        .then()
                        .spec(responseSpec(200))
                        .extract().as(BooksListResponseModel.class));
    }

    public void addBookToCart(String token, AddBookBodyModel addBookData) {
        step("Make add book to user cart request", () ->
                given()
                        .spec(requestSpec)
                        .header("Authorization", "Bearer " + token)
                        .body(addBookData)
                        .when()
                        .post("/BookStore/v1/Books")
                        .then()
                        .spec(responseSpec(201))
        );
    }
}
