import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.models.OrderCreateRequest;
import ru.praktikum.steps.OrderSteps;
import ru.praktikum.steps.UserSteps;
import ru.praktikum.models.UserCreateRequest;
import ru.praktikum.models.UserLoginRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isA;

public class OrderCreateTest {

    private final String name = RandomStringUtils.randomAlphabetic(10);
    private final String password = RandomStringUtils.randomAlphabetic(10);
    private final String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    private final List<String> ingredients = new ArrayList<>();
    private final List<String> badIngredients = new ArrayList<>();

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

    private final String noOneIngredientsMessage = "Ingredient ids must be provided";
    private String accessToken;
    private ValidatableResponse userResponse;

    @Before
    public void createUser(){
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        userResponse = userSteps.userCreate(userCreateRequest);
        accessToken = userResponse.extract().path("accessToken");
    }

    @After
    public  void deleteUser() {
        userSteps.userDelete(accessToken);
        ingredients.clear();
        badIngredients.clear();
    }

    @Test
    @DisplayName("Создание заказа после авторизации пользователя")
    @Description("Проверка создания заказа после авторизации пользователя")
    public void orderCreateWithAuthorizationTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        ingredients.add("61c0c5a71d1f82001bdaaa6c");
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(ingredients);
        orderSteps.orderCreateAfterLogin(userLoginRequest, orderCreateRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.owner.email", equalTo(email));
    }

    @Test
    @DisplayName("Создание заказа без авторизации пользователя")
    @Description("Проверка создания заказа без авторизации пользователя")
    public void orderCreateWithoutAuthorizationTest() {
        ingredients.add("61c0c5a71d1f82001bdaaa6c");
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(ingredients);
        orderSteps.orderCreate(orderCreateRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", isA(Integer.class));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов после авторизации пользователя")
    @Description("Проверка ошибки создания заказа без ингредиентов после авторизации пользователя")
    public void orderCreateWithAuthorizationWithoutIngredientsTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(ingredients);
        orderSteps.orderCreateAfterLogin(userLoginRequest, orderCreateRequest)
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(noOneIngredientsMessage));
    }

    @Test
    @DisplayName("Создание заказа с отсутствующими ингредиентами после авторизации пользователя")
    @Description("Проверка ошибки создания заказа с отсутствующими ингредиентами после авторизации пользователя")
    public void orderCreateWithAuthorizationWithBadIngredientsTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        badIngredients.add(RandomStringUtils.randomAlphabetic(10));
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(badIngredients);
        orderSteps.orderCreateAfterLogin(userLoginRequest, orderCreateRequest)
                .assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
