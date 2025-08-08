package BusinessAT;

import Models.Category;
import Models.Pet;
import Models.ResponseBody;
import Models.Tag;
import com.google.gson.Gson;
import helpers.UsefulMethods;
import okhttp3.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Бизнес АТ")
public class BusinessAT {

    private static long erasableID;
    private static Gson gson;

    @BeforeAll
    public static void beforeAll() {
        gson = new Gson();
    }

    @Test
    @DisplayName("Добавить животное")
    public void AddNewPet() throws IOException, InterruptedException {

//        Создаем животное
        Pet expectedPet = UsefulMethods.createPetObject("available");
        Response response = UsefulMethods.addNewPet(expectedPet);

        String responseBody = response.body().string();

        System.out.println("Expected Pet ID: " + responseBody + "\n");

        JSONObject jsonObject = new JSONObject(responseBody);
        long actualPetID = jsonObject.getLong("id");

//        Ищем созданное животное по айди

        Thread.sleep(10000);
        response = UsefulMethods.findPetByID(actualPetID);

        responseBody = response.body().string();
        System.out.println("Actual Pet ID: " + responseBody + "\n");

        Pet actualPet = gson.fromJson(responseBody, Pet.class);

        erasableID = actualPetID;

        assertThat(actualPet).isEqualTo(expectedPet);
    }

    @Test
    @DisplayName("Удалить животное")
    public void DeletePet() throws IOException, InterruptedException {

//        Создаем животное
        Pet expectedPet = UsefulMethods.createPetObject("available");
        Response response = UsefulMethods.addNewPet(expectedPet);

        String responseBody = response.body().string();

        System.out.println("Респонс созданного животного: " + responseBody + "\n");

        JSONObject jsonObject = new JSONObject(responseBody);
        long actualPetID = jsonObject.getLong("id");

//        Удаляем животное

        Thread.sleep(10000);
        response = UsefulMethods.deletePetByPetID(actualPetID);

        responseBody = response.body().string();
        System.out.println("Респонс после удаления: " + responseBody + "\n");

//        Убеждаемся что животное удалено

        Thread.sleep(10000);
        response = UsefulMethods.findPetByID(actualPetID);
        responseBody = response.body().string();
        System.out.println("Что нашли гетом: " + responseBody + "\n");

        ResponseBody actualResponseBody = gson.fromJson(responseBody, ResponseBody.class);
        ResponseBody expectedResponseBody = new ResponseBody(1, "error", "Pet not found");

        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("Обновить животное")
    public void UpdateExistingPet() throws IOException, InterruptedException {

//        Создаем животное
        Pet initialPet = UsefulMethods.createPetObject("available");
        Response response = UsefulMethods.addNewPet(initialPet);

        String responseBody = response.body().string();

        System.out.println("Респонс созданного животного: " + responseBody + "\n");

        JSONObject jsonObject = new JSONObject(responseBody);
        long actualPetID = jsonObject.getLong("id");

//        Обновляем животное

        Thread.sleep(10000);
        response = UsefulMethods.updateExistingPet(actualPetID);

        responseBody = response.body().string();
        System.out.println("Респонс после обновления: " + responseBody + "\n");

//        Убеждаемся что животное обновлено

        Thread.sleep(10000);
        response = UsefulMethods.findPetByID(actualPetID);
        responseBody = response.body().string();
        System.out.println("Что нашли гетом: " + responseBody + "\n");

        Category category = new Category(80820251002L, "new_category");
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://test.kz/");
        photoUrls.add("https://www.newsite.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("new_tag", 808202501));

        Pet updatedPet = gson.fromJson(responseBody, Pet.class);
        Pet expectedPet = new Pet(actualPetID, category, "updated", photoUrls, tags, "sold");

        erasableID = actualPetID;

        assertThat(updatedPet).isEqualTo(expectedPet);
    }

    @Test
    @DisplayName("Обновить животное по айди")
    public void UpdatePetByPetID() throws IOException, InterruptedException {

//        Создаем животное
        Pet initialPet = UsefulMethods.createPetObject("available");
        Response response = UsefulMethods.addNewPet(initialPet);

        String responseBody = response.body().string();

        System.out.println("Респонс созданного животного: " + responseBody + "\n");

        JSONObject jsonObject = new JSONObject(responseBody);
        long actualPetID = jsonObject.getLong("id");

//        Обновляем животное

        Thread.sleep(10000);
        response = UsefulMethods.updatePetByPetID(actualPetID);

        responseBody = response.body().string();
        System.out.println("Респонс после обновления: " + responseBody + "\n");

//        Убеждаемся что животное обновлено

        Thread.sleep(10000);
        response = UsefulMethods.findPetByID(actualPetID);
        responseBody = response.body().string();
        System.out.println("Что нашли гетом: " + responseBody + "\n");

        Category category = new Category(40820251002L, "dogs");
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://yandex.kz/");
        photoUrls.add("https://www.google.com/");
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("dog", 408202501));
        tags.add(new Tag("adult", 408202502));

        Pet updatedPet = gson.fromJson(responseBody, Pet.class);
        Pet expectedPet = new Pet(actualPetID, category, "New_Name", photoUrls, tags, "pending");

        erasableID = actualPetID;

        assertThat(updatedPet).isEqualTo(expectedPet);
    }


    @AfterEach
    public void deletePet() throws IOException {
        UsefulMethods.deletePetByPetID(erasableID);
    }
}
