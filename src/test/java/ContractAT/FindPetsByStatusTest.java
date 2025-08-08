package ContractAT;

import Models.Pet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import helpers.URLs;
import okhttp3.*;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Найти животных по статусу")
public class FindPetsByStatusTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;

//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
    }

    @DisplayName("Проверить статус код")
    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    public void findPetsByStatusCheckStatusCode(String status) throws IOException {
        String testURL = URLs.URL + URLs.FINDPETBYSTATUS + status;

        request = new Request.Builder().url(testURL).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }

    @DisplayName("Проверить респонс")
    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    public void findPetsByStatusCheckResponse(String status) throws IOException {
        String testURL = URLs.URL + URLs.FINDPETBYSTATUS + status;

        request = new Request.Builder().url(testURL).get().build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();
        Pet[] pets = gson.fromJson(responseBody, Pet[].class);

        for (Pet pet : pets) {
            assertThat(pet.getStatus()).isEqualTo(status);
        }

    }

    @Test
    @DisplayName("Найти животных по несуществующему статусу")
    @Disabled("Есть актуальный баг")
    public void findPetsByInvalidStatusValue() throws IOException {
        String testURL = URLs.URL + URLs.FINDPETBYSTATUS + "status";

        request = new Request.Builder().url(testURL).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        assertThat(statusCode).isEqualTo(400);
    }
}
