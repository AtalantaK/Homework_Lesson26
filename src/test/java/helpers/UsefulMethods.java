package helpers;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class UsefulMethods {

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static Request request;
    private static Response response;
    private static MediaType mediaType = MediaType.get("application/json");
    private static final String apiKey = "special-key";

    public static Response addNewPet() throws IOException {
        Path FILEPATH = Path.of("src/test/java/JSONfiles/NewPetRequestBody.json");

        String stringBody = Files.readString(FILEPATH);

        RequestBody requestBody = RequestBody.create(stringBody, mediaType);
        request = new Request.Builder().url(URLs.PETURL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        return response;
    }

    public static Response findPetsByStatus(String status) throws IOException {

        request = new Request.Builder().url(URLs.PETURL + URLs.FINDPETBYSTATUS + status).get().build();
        response = httpClient.newCall(request).execute();

        return response;
    }

    public static Response findPetByID(long id) throws IOException {

        request = new Request.Builder().url(URLs.PETURL + id).get().build();
        response = httpClient.newCall(request).execute();

        return response;
    }

//

    public static void deletePetByPetID(long petID) throws IOException {

        request = new Request.Builder().url(URLs.PETURL + petID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();
    }

//    public static void petstoreAuth() throws IOException {
//        String username = "test";
//        String password = "abc123";
//
//        String credential = Credentials.basic(username, password);
//        System.out.println(credential);
//
//        request = new Request.Builder().url(URLs.AUTHURL).header("Authorization", credential).build();
//        response = httpClient.newCall(request).execute();
//
//        System.out.println(response.body().string());
//    }

}
