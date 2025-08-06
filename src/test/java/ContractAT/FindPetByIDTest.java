package ContractAT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FindPetByIDTest {

    //todo: переделать

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static final Path FILEPATH = Path.of("src/test/java/JSONfiles/NewPetRequestBody.json");

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
    }

    @Test
    public void FindPetByIDCheckStatusCode() throws IOException {
//        response = UsefulMethods.addNewPet();
        //todo
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.PETURL + actualID).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void FindPetByIDCheckResponse() throws IOException {
//        response = UsefulMethods.addNewPet();
        //todo
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        long actualID = jsonObject.getLong("id");

        request = new Request.Builder().url(URLs.PETURL + actualID).get().build();
        response = httpClient.newCall(request).execute();

        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(response.body().string(), Object.class);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        String actualResponseBody = writer.writeValueAsString(json);

        String stringBody = Files.readString(FILEPATH);
        json = mapper.readValue(stringBody, Object.class);
        writer = mapper.writerWithDefaultPrettyPrinter();
        String expectedResponseBody = writer.writeValueAsString(json);

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    public void FindPetByNonExistentID() throws IOException {
        long actualID = 1444444444;

        request = new Request.Builder().url(URLs.PETURL + actualID).get().build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);

        String actualResponseMessage = jsonObject.getString("message");
        String expectedResponseMessage = "Pet not found";

        int actualResponseCode = jsonObject.getInt("code");
        int expectedResponseCode = 1;

        String actualResponseError = jsonObject.getString("type");
        String expectedResponseError = "error";

        assertAll("Несколько проверок",
                () -> assertThat(statusCode).isEqualTo(404),
                () -> assertThat(actualResponseCode).isEqualTo(expectedResponseCode),
                () -> assertThat(actualResponseError).isEqualTo(expectedResponseError),
                () -> assertThat(actualResponseMessage).isEqualTo(expectedResponseMessage));
    }
}
