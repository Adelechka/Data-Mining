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
    final static String TOKEN = "cea04ba405e458b4207e1fa4b1938835204fb2f97002691c6a153912cb20f99a3d73c9b5782d5a884911a";
    final static Integer ID = 121089850;
    final static String DOMAIN = "itis_kfu";
    final static Integer POSTS_COUNT = 200;
    static HashMap<String, Integer> counter = new HashMap<>();

    public static void main(String[] args) throws ClientException, ApiException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        UserActor actor = new UserActor(ID, TOKEN);

        for (int i = 0; i < POSTS_COUNT / 100; i++) {
            GetResponse post = vk
                    .wall()
                    .get(actor)
                    .domain(DOMAIN)
                    .count(100)
                    .offset(i)
                    .execute();

            JSONObject postJSON = (JSONObject) wrap(post);
            for (int j = 0; j < 100; j++) {
                JSONObject postJSONItems = ((JSONArray) postJSON.get("items")).getJSONObject(j);
                String[] words = stringProcessing((String) postJSONItems.get("text"));
                countWords(counter, words);
            }
        }
        printResult(counter);
    }

    private static String[] stringProcessing(String text) {
        return text.replaceAll("[^a-zA-Zа-яА-ЯёЁ ]", "")
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
