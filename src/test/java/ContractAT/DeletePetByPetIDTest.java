package ContractAT;

import Models.Pet;
import Models.ResponseBody;
import com.google.gson.Gson;
import helpers.URLs;
import helpers.UsefulMethods;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Удалить животное по айди")
public class DeletePetByPetIDTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static final String apiKey = "special-key";
    JSONObject jsonObject;

//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
    }

    @Test
    @DisplayName("Проверить статус код")
    public void deletePetByPetIDCheckStatusCode() throws IOException {

        Pet pet = UsefulMethods.createPetObject("available");
        response = UsefulMethods.addNewPet(pet);
        jsonObject = new JSONObject(response.body().string());

        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.URL + actualID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("Проверить респонс")
    public void deletePetByPetIDCheckResponse() throws IOException {

        Pet pet = UsefulMethods.createPetObject("available");
        response = UsefulMethods.addNewPet(pet);
        jsonObject = new JSONObject(response.body().string());

        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.URL + actualID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();
        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(200, "unknown", "60820251003");

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("Удалить животное с несуществующим айди")
    @Disabled("Есть актуальный баг")
    public void deletePetByNonExistentID() throws IOException {
        long actualID = 122222222;
        request = new Request.Builder().url(URLs.URL + actualID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();
        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(404, "unknown", "Pet not found");

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }
}
