package models;

public record AuthResponseModel(
        String token,
        String expires,
        String status,
        String result) {}