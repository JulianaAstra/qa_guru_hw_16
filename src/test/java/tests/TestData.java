package tests;

import com.github.javafaker.Faker;
import models.AuthBodyModel;

public class TestData {
    Faker faker = new Faker();
    private final String login = faker.name().username();
    private final String password = "Blue$ky77" + faker.number().numberBetween(100000, 1000000);
    public AuthBodyModel authData = new AuthBodyModel(login, password);
}
