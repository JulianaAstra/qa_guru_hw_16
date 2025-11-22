package tests;

import com.github.javafaker.Faker;

public class TestData {
    private final String login = new Faker().name().username();
    private final String password = "Blue$ky77" + new Faker().number().numberBetween(100000, 1000000);
    public String authData = "{\"userName\":\"" + login + "\",\"password\":\"" + password + "\"}";

    //        String password = "Blue$ky77";
    //        String login = "julianaastra";

}
