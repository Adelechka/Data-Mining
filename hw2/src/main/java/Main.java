import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static org.json.JSONObject.wrap;

public class Main {
    final static String TOKEN = "598e40ade0078e6a2712647fcf394df4b21f989aea295fa034c1ba8995ec0f69f0758dd91e19c530af129";
    final static Integer ID = 121089850;
    final static String DOMAIN = "itis_kfu";
    final static Integer POSTS_COUNT = 200;
    static HashMap<String, Integer> counter = new HashMap<>();

    public static void main(String[] args) throws ClientException, ApiException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        UserActor actor = new UserActor(ID, TOKEN);

        for (int i = 0; i < POSTS_COUNT / 100; i++) {
            GetResponse response = vk
                    .wall()
                    .get(actor)
                    .domain(DOMAIN)
                    .count(100)
                    .offset(i)
                    .execute();

            JSONObject post = (JSONObject) wrap(response);
            for (int j = 0; j < 100; j++) {

                JSONObject items = (JSONObject) post.getJSONArray("items").get(j);
                String[] words = stringProcessing((String) items.get("text"));

                countWords(counter, words);

                if (items.has("copyHistory")) {
                    JSONObject copyHistory = (JSONObject) items.getJSONArray("copyHistory").get(0);
                    String text = copyHistory.getString("text");
                    String[] innerPostWords = stringProcessing(text);
                    countWords(counter, innerPostWords);

                }

            }
        }
        printResult(counter);
    }

    private static String[] stringProcessing(String text) {
        return text.replaceAll("[^a-zA-Zа-яА-ЯёЁ#_ ]", "")
                .replaceAll("( ){2,}", " ")
                .trim()
                .toLowerCase(Locale.ROOT)
                .split("\\s");
    }

    private static void countWords(HashMap<String, Integer> counter, String[] words) {
        for (String word : words) {
            if (!counter.containsKey(word)) {
                counter.put(word, 1);
            } else {
                counter.replace(word, counter.get(word) + 1);
            }
        }
    }

    private static void printResult(HashMap<String, Integer> counter) {
        counter.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(100)
                .forEach(System.out::println);
    }
}
