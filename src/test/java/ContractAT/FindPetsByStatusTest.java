package ContractAT;

import helpers.URLs;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import org.json.JSONObject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    public void findPetsByStatusCheckStatusCode(String status) throws IOException {
        String testURL = URLs.PETURL + URLs.FINDPETBYSTATUS + status;

        request = new Request.Builder().url(testURL).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }

    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    public void findPetsByStatusCheckResponse(String status) throws IOException {
        String testURL = URLs.PETURL + URLs.FINDPETBYSTATUS + status;

        request = new Request.Builder().url(testURL).get().build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();
        JSONArray jsonArray = new JSONArray(responseBody);
        String actualStatus;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            actualStatus = jsonObject.getString("status");
            assertThat(actualStatus).isEqualTo(status);
        }
    }

    @Test
    @Disabled("Есть актуальный баг")
    public void findPetsByInvalidStatusValue() throws IOException {
        String testURL = URLs.PETURL + URLs.FINDPETBYSTATUS + "status";

        request = new Request.Builder().url(testURL).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        assertAll("Несколько проверок",
                () -> assertThat(statusCode).isEqualTo(400),
                () -> assertThat(response.body().string()).isEqualTo("Invalid status value"));
    }
}
