package org.pesho.aops.finder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ForumTopic implements Comparable {

    private final String id;
    private final JsonObject jsonObject;

    public ForumTopic(String id, String s) {
        this.id = id;
        this.jsonObject = new Gson().fromJson(getJson((s)), JsonObject.class);
    }

    public String getId() {
        return id;
    }

    public String getProblemInfo() {
        JsonObject preloadData = jsonObject.getAsJsonObject("preload_cmty_data");
        JsonObject topicData = preloadData.getAsJsonObject("topic_data");
        return topicData.get("source").getAsString();
    }

    public String getAllComments() {
        return String.join("\n", getComments());
    }

    public List<String> getComments() {
        return StreamSupport.stream(getPosts().spliterator(), false)
                .map(p -> p.getAsJsonObject().get("post_canonical").getAsString())
                .collect(Collectors.toList());
    }

    private String getJson(String s) {
        s = Arrays.stream(s.split("\\r?\\n")).filter(l -> l.contains("AoPS.bootstrap_data")).findFirst().orElse("");
        s = s.trim().replace("AoPS.bootstrap_data =", "");
        return s.substring(0, s.length()-1);
    }

    private JsonArray getPosts() {
        JsonObject preloadData = jsonObject.getAsJsonObject("preload_cmty_data");
        JsonObject topicData = preloadData.getAsJsonObject("topic_data");
        return topicData.getAsJsonArray("posts_data");
    }

    @Override
    public String toString() {
        return getProblemInfo() + " https://artofproblemsolving.com/community/" + id;
//        return getProblemInfo() + " https://artofproblemsolving.com/community/" + id + "\n" + getComments().get(0)+"\n";
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }

}
