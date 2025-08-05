package ContractAT;

import helpers.URLs;
import helpers.UsefulMethods;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DeletePetByPetIDTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static final String apiKey = "special-key";

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
    }

    @Test
    public void deletePetByPetIDCheckStatusCode() throws IOException {
        response = UsefulMethods.addNewPet();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.PETURL + actualID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void deletePetByPetIDCheckResponse() throws IOException {
        response = UsefulMethods.addNewPet();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);

        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.PETURL + actualID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();

        jsonObject = new JSONObject(response.body().string());

        System.out.println(jsonObject.toString(2));

        int actualCode = jsonObject.getInt("code");
        int expectedCode = 200;

        String actualType = jsonObject.getString("type");
        String expectedType = "unknown";

        String actualMessage = jsonObject.getString("message");
        String expectedMessage = "50820251003";

        assertAll("Несколько проверок",
                () -> assertThat(actualCode).isEqualTo(expectedCode),
                () -> assertThat(actualType).isEqualTo(expectedType),
                () -> assertThat(actualMessage).isEqualTo(expectedMessage));
    }

    @Test
    public void deletePetByNonExistentID() throws IOException {
        long actualID = 122222222;
        request = new Request.Builder().url(URLs.PETURL + actualID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        JSONObject jsonObject = new JSONObject(response.body().string());

        int actualCode = jsonObject.getInt("code");
        int expectedCode = 404;

        String actualType = jsonObject.getString("type");
        String expectedType = "unknown";

        String actualMessage = jsonObject.getString("message");
        String expectedMessage = "Pet not found";

        assertAll("Несколько проверок",
                () -> assertThat(statusCode).isEqualTo(404),
                () -> assertThat(actualCode).isEqualTo(expectedCode),
                () -> assertThat(actualType).isEqualTo(expectedType),
                () -> assertThat(actualMessage).isEqualTo(expectedMessage));
    }
}
