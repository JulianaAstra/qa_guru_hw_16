package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;

public class PetStoreSpec {
    public static final String API_KEY = "special-key";

    public static RequestSpecification requestSpec = with()
            .filter(withCustomTemplates())
            .header("x-api-key", API_KEY)
            .log()
            .all();

    public static ResponseSpecification responseSpec(int expectedStatusCode) {
        return new ResponseSpecBuilder()
            .expectStatusCode(expectedStatusCode)
            .log(ALL)
            .build();
    }
}