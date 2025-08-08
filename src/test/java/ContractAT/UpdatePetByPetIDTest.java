package ContractAT;

import Models.Pet;
import Models.ResponseBody;
import com.google.gson.Gson;
import helpers.URLs;
import helpers.UsefulMethods;
import okhttp3.*;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UpdatePetByPetIDTest {
    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    JSONObject jsonObject;
    private static long erasableID;

//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
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

        erasableID = pet.getId();

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

        responseBody = response.body().string();

        Gson gson = new Gson();
        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(200, "unknown", "" + pet.getId());

        erasableID = pet.getId();

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    public void updatePetByPetIDWithNonExistentID() throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("name", "test")
                .add("status", "available")
                .build();

        request = new Request.Builder().url(URLs.URL + 999999999999L).post(formBody).build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();
        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(404, "unknown", "not found");

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
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

        erasableID = pet.getId();

        assertThat(statusCode).isEqualTo(405);
    }

    @AfterEach
    public void deletePet() throws IOException {
        UsefulMethods.deletePetByPetID(erasableID);
    }
}
