package api;

import models.AuthResponseModel;
import models.UserResponseModel;
import tests.TestData;
import java.util.Objects;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static specs.Spec.requestSpec;
import static specs.Spec.responseSpec;

public class AccountApiSteps {
    TestData testData = new TestData();

    public UserResponseModel registerUser() {
        return step("Make register new user request", () ->
                given()
                        .spec(requestSpec)
                        .body(testData.authData)
                        .when()
                        .post("/Account/v1/User")
                        .then()
                        .spec(responseSpec(201))
                        .extract().as(UserResponseModel.class));
    }

    public AuthResponseModel authUser() {
        return step("Make auth user request", () ->
                await().atMost(20, SECONDS)
                        .pollInterval(1, SECONDS)
                        .until(() -> {
                                    AuthResponseModel response = given()
                                            .spec(requestSpec)
                                            .body(testData.authData)
                                            .when()
                                            .post("/Account/v1/GenerateToken")
                                            .then()
                                            .spec(responseSpec(200))
                                            .extract().as(AuthResponseModel.class);

                                    return "Success".equals(response.status()) && response.token() != null ? response : null;
                                },
                                Objects::nonNull));
    }
}
