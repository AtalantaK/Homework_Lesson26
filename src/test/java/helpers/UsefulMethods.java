package helpers;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UsefulMethods {

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static Request request;
    private static Response response;
    private static MediaType mediaType;

    public static Response addNewPet() throws IOException {
        String URL = "https://petstore.swagger.io/v2/pet";
        Path FILEPATH = Path.of("src/test/java/JSONfiles/NewPetRequestBody.json");

        mediaType = MediaType.get("application/json");

        String stringBody = Files.readString(FILEPATH);

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        return response;
    }

    public static Response findPetsByStatus(String status) throws IOException {
        String URL = "https://petstore.swagger.io/v2/pet/findByStatus?status=" + status;

        request = new Request.Builder().url(URL).get().build();
        response = httpClient.newCall(request).execute();

        return response;
    }

    public static Response findPetByID(long id) throws IOException {
        String URL = "https://petstore.swagger.io/v2/pet/";

        request = new Request.Builder().url(URL + id).get().build();
        response = httpClient.newCall(request).execute();

        return response;
    }
}
