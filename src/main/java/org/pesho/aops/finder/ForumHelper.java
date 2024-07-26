package org.pesho.aops.finder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ForumHelper {

    @Autowired
    private Cache cache;

    public List<ForumTopic> find(List<String> contests, Predicate<ForumTopic> hasWord) throws IOException {
        Set<ForumTopic> results = new TreeSet<>();
        for (String contest: contests) {
            results.addAll(find(contest, hasWord));
        }
        System.out.println("Found: " + results.size());
        for (ForumTopic result : results) {
            System.out.println(result);
        }
        return new ArrayList<>(results);
    }

    public Set<ForumTopic> find(String contest, Predicate<ForumTopic> hasWord) throws IOException {
        Set<ForumTopic> results = new HashSet<>();

        Set<String> contestIds = getByContestId(contest);
        for (String contestId: contestIds) {
            Set<String> subContestIds = getByContestId(contestId);

            Set<ForumTopic> tmp = subContestIds.parallelStream()
                    .map(scId -> {
                        try {
                            String response = getProblemIdResponse(scId);
                            ForumTopic forumTopic = new ForumTopic(scId, response);
                            return forumTopic;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(hasWord::test)
                    .collect(Collectors.toSet());
            results.addAll(tmp);
        }

        return results;
    }

    public Set<String> getByContestId(String categoryId) throws IOException {
        String responseBody = getContestIdResponse(categoryId);

        JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
        JsonObject jsonResponse = jsonObject.get("response").getAsJsonObject();
        JsonObject jsonCategory = jsonResponse.get("category").getAsJsonObject();
        JsonArray jsonItems = jsonCategory.get("items").getAsJsonArray();

        Set<String> set = new HashSet<>();
        for (int i = 0; i < jsonItems.size(); i++) {
            JsonObject itemData = jsonItems.get(i).getAsJsonObject();

            if (itemData.get("post_data") == null) {
                long itemId = itemData.get("item_id").getAsLong();
                set.add(String.valueOf(itemId));
//                System.out.println(itemId);
                continue;
            }

            JsonObject postData = itemData.get("post_data").getAsJsonObject();
            long postId = postData.get("post_id").getAsLong();
            long topicId = postData.get("topic_id").getAsLong();
            long levelId = postData.get("category_id").getAsLong();
            if (topicId == 0) continue;

            String link = String.format("c%sh%sp%s", levelId, topicId, postId);
            set.add(link);
        }
        return set;
    }

    public boolean problemSearch(String problemId, String word) throws IOException {
        String response = getProblemIdResponse(problemId);
        return response.toLowerCase().contains(word.toLowerCase());
    }

    private String getContestIdResponse(String contestId) throws IOException {
        if (cache.has(contestId)) return cache.get(contestId);

        String url = "https://artofproblemsolving.com/m/community/ajax.php";
        String[] params = {"category_id", contestId,
                "a", "fetch_category_data",
                "aops_logged_in", "false",
                "aops_user_id", "1",
                "aops_session_id", "21d6f40cfb511982e4424e0e250a9557"};
        return cache.put(contestId, RequestHelper.postRequest(url, params));
    }

    private String getProblemIdResponse(String problemId) throws IOException {
        if (cache.has(problemId)) return cache.get(problemId);

        String responseBody = RequestHelper.getResponse("https://artofproblemsolving.com/community/" + problemId);
        return cache.put(problemId, responseBody);
    }

}
