package ru.praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.praktikum.constants.ApiConstants;
import ru.praktikum.models.OrderCreateRequest;
import ru.praktikum.models.UserLoginRequest;
import ru.praktikum.models.UserLoginResponse;

import static io.restassured.RestAssured.given;
import static ru.praktikum.constants.ApiConstants.ORDERS;

public class OrderSteps {

    public static RequestSpecification spec() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(ApiConstants.BASE_URL);
    }

    @Step("Создание нового заказа без авторизации")
    public ValidatableResponse orderCreate(OrderCreateRequest orderCreateRequest) {
        return spec()
                .body(orderCreateRequest)
                .post(ORDERS)
                .then();
    }

    @Step("Создание нового заказа после авторизации")
    public ValidatableResponse orderCreateAfterLogin(UserLoginRequest userLoginRequest, OrderCreateRequest orderCreateRequest) {
        UserSteps userSteps = new UserSteps();
        Response response = userSteps.userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return spec()
                .header("Authorization", accessToken)
                .body(orderCreateRequest)
                .post(ORDERS)
                .then();
    }

    @Step("Получение заказов без авторизации")
    public ValidatableResponse orderList() {
        return spec()
                .get(ORDERS)
                .then();
    }

    @Step("Получение заказов после авторизации")
    public ValidatableResponse orderListAfterLogin(UserLoginRequest userLoginRequest) {
        UserSteps userSteps = new UserSteps();
        Response response = userSteps.userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        return spec()
                .header("Authorization", accessToken)
                .get(ORDERS)
                .then();
    }
}
