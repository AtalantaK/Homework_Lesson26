package ContractAT;

import Models.Category;
import Models.Pet;
import Models.Tag;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import helpers.URLs;
import helpers.UsefulMethods;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Обновить существующее животное")
public class UpdateExistingPetTest {
    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    JSONObject jsonObject;
    private static ObjectMapper objectMapper;
    private static MediaType mediaType;
    private static long erasableID;

//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
        mediaType = MediaType.get("application/json");
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Проверить статус код")
    public void updatePetCheckStatusCode() throws IOException {

        Pet existingPet = UsefulMethods.createPetObject("available");
        response = UsefulMethods.addNewPet(existingPet);
        jsonObject = new JSONObject(response.body().string());
        long currentID = jsonObject.getLong("id");

        Category category = new Category(80820251002L, "new_category");
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://test.kz/");
        photoUrls.add("https://www.newsite.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("new_tag", 808202501));

        Pet updatedPet = new Pet(currentID, category, "updated" + existingPet.getName(), photoUrls, tags, "sold");
        String jsonPet = objectMapper.writeValueAsString(updatedPet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).put(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        jsonObject = new JSONObject(response.body().string());

        erasableID = jsonObject.getLong("id");

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("Проверить респонс")
    public void updatePetCheckResponse() throws IOException {

        Pet existingPet = UsefulMethods.createPetObject("available");
        response = UsefulMethods.addNewPet(existingPet);
        jsonObject = new JSONObject(response.body().string());

        long currentID = jsonObject.getLong("id");

        Category category = new Category(80820251002L, "new_category");
        List<String> PhotoUrls = new ArrayList<>();
        PhotoUrls.add("https://test.kz/");
        PhotoUrls.add("https://www.newsite.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("new_tag", 808202501));

        Pet updatedPet = new Pet(currentID, category, "updated" + existingPet.getName(), PhotoUrls, tags, "sold");
        String jsonPet = objectMapper.writeValueAsString(updatedPet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).put(requestBody).build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();
        Pet actualPet = gson.fromJson(responseBody, Pet.class);

        erasableID = jsonObject.getLong("id");

        assertAll("Несколько проверок",
                () -> assertThat(actualPet).isEqualTo(updatedPet),
                () -> assertThat(actualPet).isNotEqualTo(existingPet));
    }

    @Test
    @DisplayName("Обновить животное с несуществующим айди")
    @Disabled("Есть актуальный баг")
    public void updatePetNonExistentID() throws IOException {

        long nonExistentID = 999999999L;

        Category category = new Category(80820251002L, "new_category");
        List<String> expectedPhotoUrls = new ArrayList<>();
        expectedPhotoUrls.add("https://test.kz/");
        expectedPhotoUrls.add("https://www.newsite.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("new_tag", 808202501));

        Pet updatedPet = new Pet(nonExistentID, category, "updated", expectedPhotoUrls, tags, "sold");
        String jsonPet = objectMapper.writeValueAsString(updatedPet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).put(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(404);
    }

    @AfterEach
    public void deletePet() throws IOException {
        UsefulMethods.deletePetByPetID(erasableID);
    }
}
