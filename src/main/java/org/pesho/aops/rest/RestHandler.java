package org.pesho.aops.rest;

import org.pesho.aops.finder.AOPSFinder;
import org.pesho.aops.finder.ForumTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class RestHandler {

    @Autowired
    private AOPSFinder aopsFinder;

    @GetMapping("{query}")
    public String find(@PathVariable String query) throws Exception {
        List<ForumTopic> topics = aopsFinder.find(query);

        String result = "Total: " + topics.size();
        for (int i = 0; i < topics.size(); i++) {
            String link = "https://artofproblemsolving.com/community/" + topics.get(i).getId();

            result += "<br />";
            result += (i+1) + ". ";
            result += topics.get(i).getProblemInfo();
            result += " - ";
            result += "<a href=\""+link+"\" target=\"_blank\">"+link+"</a>";
        }
        return result;
    }

}
