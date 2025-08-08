package ContractAT;

import Models.Pet;
import Models.ResponseBody;
import com.google.gson.Gson;
import helpers.URLs;
import helpers.UsefulMethods;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Загрузить изображение питомца")
public class UploadImageTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static MediaType mediaType;
    private static File file;
    private static long erasableID;


//    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().build();
        mediaType = MediaType.parse("image/png");
        file = new File("src/test/petImage.png");
    }

    @Test
    @DisplayName("Проверить статус код")
    public void UploadImageCheckStatusCode() throws IOException {
        Pet pet = UsefulMethods.createPetObject("available");

        UsefulMethods.addNewPet(pet);


        // Обёртка для файла
        RequestBody fileBody = RequestBody.create(file, mediaType);

        // Собираем multipart тело (form-data)
        MultipartBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("additionalMetadata", "testImage")
                .build();

        request = new Request.Builder().url(URLs.URL + pet.getId() + URLs.UPLOADIMAGE).post(formBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        erasableID = pet.getId();

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("Проверить респонс")
    public void UploadImageCheckResponse() throws IOException {
        Pet pet = UsefulMethods.createPetObject("available");

        UsefulMethods.addNewPet(pet);

        // Обёртка для файла
        RequestBody fileBody = RequestBody.create(file, mediaType);

        // Собираем multipart тело (form-data)
        MultipartBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("additionalMetadata", "testImage")
                .build();

        request = new Request.Builder().url(URLs.URL + pet.getId() + URLs.UPLOADIMAGE).post(formBody).build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();

        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(200, "unknown", "additionalMetadata: testImage\nFile uploaded to ./petImage.png, 65713 bytes");

        erasableID = pet.getId();

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("Загрузить изображение для несуществующего ID")
    @Disabled("Есть актуальный баг")
    public void UploadImageIDNotExist() throws IOException {

        long nonExistentID = 923456789;
        UsefulMethods.deletePetByPetID(nonExistentID);

        // Обёртка для файла
        RequestBody fileBody = RequestBody.create(file, mediaType);

        // Собираем multipart тело (form-data)
        MultipartBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("additionalMetadata", "testImage")
                .build();

        request = new Request.Builder().url(URLs.URL + nonExistentID + URLs.UPLOADIMAGE).post(formBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    @DisplayName("Загрузить изображение без файла")
    @Disabled("Есть актуальный баг")
    public void UploadImageWithoutFile() throws IOException {
        Pet pet = UsefulMethods.createPetObject("available");

        UsefulMethods.addNewPet(pet);

        // Собираем multipart тело (form-data)
        MultipartBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("additionalMetadata", "testImage")
                .build();

        request = new Request.Builder().url(URLs.URL + pet.getId() + URLs.UPLOADIMAGE).post(formBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();

        erasableID = pet.getId();

        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    @DisplayName("Загрузить изображение без метаданных")
    public void UploadImageWithoutMetadata() throws IOException {
        Pet pet = UsefulMethods.createPetObject("available");

        UsefulMethods.addNewPet(pet);

        // Обёртка для файла
        RequestBody fileBody = RequestBody.create(file, mediaType);

        // Собираем multipart тело (form-data)
        MultipartBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        request = new Request.Builder().url(URLs.URL + pet.getId() + URLs.UPLOADIMAGE).post(formBody).build();
        response = httpClient.newCall(request).execute();

        String responseBody = response.body().string();

        Gson gson = new Gson();

        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(200, "unknown", "additionalMetadata: null\nFile uploaded to ./petImage.png, 65713 bytes");

        erasableID = pet.getId();

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @AfterEach
    public void deletePet() throws IOException {
        UsefulMethods.deletePetByPetID(erasableID);
    }

}
