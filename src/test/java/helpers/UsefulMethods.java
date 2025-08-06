package helpers;

import Models.Category;
import Models.Pet;
import Models.Tag;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsefulMethods {

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static Request request;
    private static Response response;
    private static MediaType mediaType = MediaType.get("application/json");
    private static final String apiKey = "special-key";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Response addNewPet(Pet pet) throws IOException {

        // Сериализуем объект в JSON
        String jsonPet = objectMapper.writeValueAsString(pet);

        RequestBody requestBody = RequestBody.create(jsonPet, mediaType);
        request = new Request.Builder().url(URLs.URL).post(requestBody).build();
        response = httpClient.newCall(request).execute();

        System.out.println(requestBody);

        return response;
    }

    public static Response findPetsByStatus(String status) throws IOException {

        request = new Request.Builder().url(URLs.URL + URLs.FINDPETBYSTATUS + status).get().build();
        response = httpClient.newCall(request).execute();

        return response;
    }

    public static Response findPetByID(long id) throws IOException {

        request = new Request.Builder().url(URLs.URL + id).get().build();
        response = httpClient.newCall(request).execute();

        return response;
    }

    public static void deletePetByPetID(long petID) throws IOException {

        request = new Request.Builder().url(URLs.URL + petID).header("api_key", apiKey).delete().build();
        response = httpClient.newCall(request).execute();
    }

    public static Pet createPetObject(String status) {
        Category category = new Category(40820251002L, "dogs");
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://yandex.kz/");
        photoUrls.add("https://www.google.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("dog", 408202501));
        tags.add(new Tag("adult", 408202502));

        Pet pet = new Pet(60820251003L, category, "Bobik", photoUrls, tags, status);

        return pet;
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
