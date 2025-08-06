package ContractAT;

import Models.Pet;
import Models.ResponseBody;
import helpers.URLs;
import helpers.UsefulMethods;
import okhttp3.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UpdatePetByPetIDTest {
    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static final String apiKey = "special-key";
    JSONObject jsonObject;
//    private static long currentID;

//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
    }

    @BeforeEach
    public void beforeEach() throws IOException {

    }

    @ParameterizedTest
    @CsvSource({
            "available, pending",
            "available, sold",
            "pending, available",
            "pending, sold",
            "sold, available",
            "sold, pending"
    })
    public void updatePetByPetIDCheckStatusCode(String oldStatus, String newStatus) throws IOException {

        Pet pet = UsefulMethods.createPetObject(oldStatus);

        response = UsefulMethods.addNewPet(pet);
        String responseBody = response.body().string();
        jsonObject = new JSONObject(responseBody);

        long currentID = jsonObject.getLong("id");
        String currentName = jsonObject.getString("name");

        RequestBody formBody = new FormBody.Builder()
                .add("name", "New" + currentName)
                .add("status", newStatus)
                .build();

        request = new Request.Builder().url(URLs.URL + currentID).post(formBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        UsefulMethods.deletePetByPetID(pet.getId());

        assertThat(statusCode).isEqualTo(200);
    }

    @ParameterizedTest
    @CsvSource({
            "available, pending",
            "available, sold",
            "pending, available",
            "pending, sold",
            "sold, available",
            "sold, pending"
    })
    public void updatePetByPetIDCheckResponse(String oldStatus, String newStatus) throws IOException {

        Pet pet = UsefulMethods.createPetObject(oldStatus);

        response = UsefulMethods.addNewPet(pet);
        String responseBody = response.body().string();
        jsonObject = new JSONObject(responseBody);

        long currentID = jsonObject.getLong("id");
        String currentName = jsonObject.getString("name");

        RequestBody formBody = new FormBody.Builder()
                .add("name", "New" + currentName)
                .add("status", newStatus)
                .build();

        request = new Request.Builder().url(URLs.URL + currentID).post(formBody).build();
        response = httpClient.newCall(request).execute();

        jsonObject = new JSONObject(response.body().string());

        ResponseBody response = new ResponseBody(jsonObject.getInt("code"), jsonObject.getString("type"), jsonObject.getString("message"));

        int actualCode = response.getCode();
        int expectedCode = 200;

        String actualType = response.getType();
        String expectedType = "unknown";

        String actualMessage = response.getMessage();
        String expectedMessage = "" + pet.getId();

        UsefulMethods.deletePetByPetID(pet.getId());

        assertAll("Несколько проверок",
                () -> assertThat(actualCode).isEqualTo(expectedCode),
                () -> assertThat(actualType).isEqualTo(expectedType),
                () -> assertThat(actualMessage).isEqualTo(expectedMessage));
    }

    @Test
    public void updatePetByPetIDWithNonExistentID() throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("name", "test")
                .add("status", "available")
                .build();

        request = new Request.Builder().url(URLs.URL + 999999999999L).post(formBody).build();
        response = httpClient.newCall(request).execute();

        jsonObject = new JSONObject(response.body().string());

        ResponseBody response = new ResponseBody(jsonObject.getInt("code"), jsonObject.getString("type"), jsonObject.getString("message"));

        int actualCode = response.getCode();
        int expectedCode = 404;

        String actualType = response.getType();
        String expectedType = "unknown";

        String actualMessage = response.getMessage();
        String expectedMessage = "not found";

        assertAll("Несколько проверок",
                () -> assertThat(actualCode).isEqualTo(expectedCode),
                () -> assertThat(actualType).isEqualTo(expectedType),
                () -> assertThat(actualMessage).isEqualTo(expectedMessage));
    }

    @Test
    @Disabled("Есть актуальный баг")
    public void updatePetByPetIDWithNonExistenceStatus() throws IOException {

        Pet pet = UsefulMethods.createPetObject("available");

        response = UsefulMethods.addNewPet(pet);
        String responseBody = response.body().string();
        jsonObject = new JSONObject(responseBody);

        long currentID = jsonObject.getLong("id");
        String currentName = jsonObject.getString("name");

        RequestBody formBody = new FormBody.Builder()
                .add("name", "New" + currentName)
                .add("status", "newStatus")
                .build();

        request = new Request.Builder().url(URLs.URL + currentID).post(formBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        UsefulMethods.deletePetByPetID(pet.getId());

        assertThat(statusCode).isEqualTo(405);
    }
}
