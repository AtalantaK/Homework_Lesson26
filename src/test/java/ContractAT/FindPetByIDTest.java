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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Найти животное по айди")
public class FindPetByIDTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static long erasableID;

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
    }

    @Test
    @DisplayName("Проверить статус код")
    public void FindPetByIDCheckStatusCode() throws IOException {
        Pet pet = UsefulMethods.createPetObject("available");
        response = UsefulMethods.addNewPet(pet);

        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.URL + actualID).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        erasableID = actualID;

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("Проверить респонс")
    public void FindPetByIDCheckResponse() throws IOException {
        Pet expectedPet = UsefulMethods.createPetObject("available");
        response = UsefulMethods.addNewPet(expectedPet);

        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.URL + actualID).get().build();
        response = httpClient.newCall(request).execute();

        responseBody = response.body().string();
        Gson gson = new Gson();
        Pet actualResponseBody = gson.fromJson(responseBody, Pet.class);

        erasableID = actualID;

        assertThat(actualResponseBody).isEqualTo(expectedPet);
    }

    @Test
    @DisplayName("Найти животное с несуществующим айди")
    public void FindPetByNonExistentID() throws IOException {
        long actualID = 1444444444;

        request = new Request.Builder().url(URLs.URL + actualID).get().build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();
        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(1, "error", "Pet not found");

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @AfterEach
    public void deletePet() throws IOException {
        UsefulMethods.deletePetByPetID(erasableID);
    }
}
