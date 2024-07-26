package org.pesho.aops.finder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

@Component
public class AOPSFinder {

    @Autowired
    private ForumHelper forumHelper;

    public List<ForumTopic> find(String s) throws IOException {
        Predicate<ForumTopic> predicate = PredicateHelper.getPredicate(s);
        return forumHelper.find(ContestHelper.ALL, predicate);
    }

}
