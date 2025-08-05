package ContractAT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import helpers.URLs;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import okhttp3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class AddNewPetTest {

    private static OkHttpClient httpClient;
    private static Request request;
    private static Response response;
    private static final Path FILEPATH = Path.of("src/test/java/JSONfiles/NewPetRequestBody.json");
    private static MediaType mediaType;

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Запускаю тесты");
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Логировать всё: заголовки + тело
        httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
        mediaType = MediaType.get("application/json");
    }

    @Test
    public void addNewPetCheckStatusCode() throws IOException {
        String stringBody = Files.readString(FILEPATH);

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URLs.PETURL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void addNewPetCheckResponseBody() throws IOException {
        String stringBody = Files.readString(FILEPATH);

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URLs.PETURL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(response.body().string(), Object.class);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        String actualResponseBody = writer.writeValueAsString(json);

        json = mapper.readValue(stringBody, Object.class);
        writer = mapper.writerWithDefaultPrettyPrinter();
        String expectedResponseBody = writer.writeValueAsString(json);

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    public void addNewPetWithoutName() throws IOException {
        String stringBody = Files.readString(Path.of("src/test/java/JSONfiles/NewPetWithoutNameRequestBody.json"));

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URLs.PETURL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    public void addNewPetWithoutPhotoUrls() throws IOException {
        String stringBody = Files.readString(Path.of("src/test/java/JSONfiles/NewPetWithoutPhotoUrlsRequestBody.json"));

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URLs.PETURL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    public void addNewPetWithOnlyMandatoryParameters() throws IOException {
        String stringBody = Files.readString(Path.of("src/test/java/JSONfiles/NewPetWithOnlyMandatoryParametersRequestBody.json"));

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URLs.PETURL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        assertThat(statusCode).isEqualTo(200);
    }
}
