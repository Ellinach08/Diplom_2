import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.models.OrderCreateRequest;
import ru.praktikum.models.UserCreateRequest;
import ru.praktikum.models.UserLoginRequest;
import ru.praktikum.steps.OrderSteps;
import ru.praktikum.steps.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

public class OrderGetListTest {

    private final String name = RandomStringUtils.randomAlphabetic(10);
    private final String password = RandomStringUtils.randomAlphabetic(10);
    private final String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());
    private final String userUnauthorizedMessage = "You should be authorised";

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private final List<String> ingredients = new ArrayList<>();
    private String accessToken;
    private ValidatableResponse userResponse;

    @Before
    public void setUp(){
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        userResponse = userSteps.userCreate(userCreateRequest);
        accessToken = userResponse.extract().path("accessToken");

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        ingredients.add("61c0c5a71d1f82001bdaaa6c");
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(ingredients);
        orderSteps.orderCreateAfterLogin(userLoginRequest, orderCreateRequest);
    }

    @After
    public void tearDown() {
        userSteps.userDelete(accessToken);
        ingredients.clear();
    }

    @Test
    @DisplayName("Получение списка заказов пользователя без авторизации")
    @Description("Проверка ошибки получения списка заказов пользователя без авторизации")
    public void orderListWithoutAuthorizationTest() {
        orderSteps.orderList()
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userUnauthorizedMessage));
    }

    @Test
    @DisplayName("Получение списка заказов пользователя после авторизации")
    @Description("Проверка получения списка заказов пользователя после авторизации")
    public void orderListWithAuthorizationTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        orderSteps.orderListAfterLogin(userLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("orders", instanceOf(List.class));
    }
}
