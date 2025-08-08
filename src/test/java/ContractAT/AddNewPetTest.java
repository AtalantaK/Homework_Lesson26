package ContractAT;

import Models.Category;
import Models.Pet;
import Models.Tag;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import helpers.URLs;
import helpers.UsefulMethods;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Добавить питомца")
public class AddNewPetTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static MediaType mediaType;
    private static ObjectMapper objectMapper = new ObjectMapper();
    JSONObject jsonObject;
    private static long erasableID;

//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
        mediaType = MediaType.get("application/json");
    }

    @Test
    @DisplayName("Проверить статус код")
    public void addNewPetCheckStatusCode() throws IOException {

        Pet pet = UsefulMethods.createPetObject("pending");
        String jsonPet = objectMapper.writeValueAsString(pet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        erasableID = pet.getId();

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("Проверить респонс")
    public void addNewPetCheckResponseBody() throws IOException {

        Pet pet = UsefulMethods.createPetObject("pending");
        String jsonPet = objectMapper.writeValueAsString(pet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        Object json = objectMapper.readValue(response.body().string(), Object.class);
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        String actualResponseBody = writer.writeValueAsString(json);

        json = objectMapper.readValue(jsonPet, Object.class);
        writer = objectMapper.writerWithDefaultPrettyPrinter();
        String expectedResponseBody = writer.writeValueAsString(json);

        erasableID = pet.getId();

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("Добавить питомца без имени")
    @Disabled("Есть актуальный баг")
    public void addNewPetWithoutName() throws IOException {

        Category category = new Category(40820251002L, "dogs");
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://yandex.kz/");
        photoUrls.add("https://www.google.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("dog", 408202501));
        tags.add(new Tag("adult", 408202502));

        Pet pet = new Pet(60820251003L, category, photoUrls, tags, "available");

        String jsonPet = objectMapper.writeValueAsString(pet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        erasableID = pet.getId();

        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    @DisplayName("Добавить питомца без ссылки на фото")
    @Disabled("Есть актуальный баг")
    public void addNewPetWithoutPhotoUrls() throws IOException {

        Category category = new Category(40820251002L, "dogs");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("dog", 408202501));
        tags.add(new Tag("adult", 408202502));

        Pet pet = new Pet(60820251003L, category, "Bobik", tags, "available");

        String jsonPet = objectMapper.writeValueAsString(pet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        erasableID = pet.getId();

        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    @DisplayName("Добавить питомца только с мандаторными параметрами")
    public void addNewPetWithOnlyMandatoryParameters() throws IOException {

        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://yandex.kz/");
        photoUrls.add("https://www.google.com/");

        Pet pet = new Pet("Bobik", photoUrls);

        String jsonPet = objectMapper.writeValueAsString(pet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        jsonObject = new JSONObject(response.body().string());

        erasableID = jsonObject.getLong("id");

        assertThat(statusCode).isEqualTo(200);
    }

    @AfterEach
    public void deletePet() throws IOException {
        UsefulMethods.deletePetByPetID(erasableID);
    }
}
