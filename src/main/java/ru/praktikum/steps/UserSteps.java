package ru.praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.praktikum.constants.ApiConstants;
import ru.praktikum.models.UserCreateRequest;
import ru.praktikum.models.UserLoginRequest;
import ru.praktikum.models.UserLoginResponse;

import static io.restassured.RestAssured.given;
import static ru.praktikum.constants.ApiConstants.*;

public class UserSteps {
    public static RequestSpecification spec() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(ApiConstants.BASE_URL);
    }

    @Step("Создание нового пользователя")
    public ValidatableResponse userCreate(UserCreateRequest userCreateRequest) {
        return spec()
                .body(userCreateRequest)
                .post(USER_CREATE)
                .then();
    }
    @Step("Авторизация пользователя")
    public ValidatableResponse userLogin(UserLoginRequest userLoginRequest) {
        return spec()
                .body(userLoginRequest)
                .post(USER_LOGIN)
                .then();
    }
    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse userEdit(UserCreateRequest userCreateRequest) {
        return spec()
                .body(userCreateRequest)
                .patch(USER)
                .then();
    }
    @Step("Изменение данных пользователя после авторизации")
    public ValidatableResponse userEditAfterLogin(UserLoginRequest userLoginRequest, UserCreateRequest userCreateRequest) {
        Response response = userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return spec()
                .header("Authorization", accessToken)
                .body(userCreateRequest)
                .patch(USER)
                .then();
    }

    @Step("Удаление пользователя по токену")
    public ValidatableResponse userDelete(String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .delete(USER)
                .then();
    }

    @Step("Удаление пользователя после авторизации")
    public ValidatableResponse userDeleteAfterLogin(UserLoginRequest userLoginRequest) {
        Response response = userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return userDelete(accessToken);
    }

}
