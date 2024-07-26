package org.pesho.aops.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class PredicateHelper {

    private static boolean isOperator(String s) {
        return s.equals("&") || s.equals("|") || s.equals("!") || s.equals("(") || s.equals(")");
    }

    private static boolean isOperator(char c) {
        return isOperator(String.valueOf(c));
    }

    public static List<String> getTokens(String s) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0 || isOperator(c) || isOperator(s.charAt(i-1))) {
                tokens.add(String.valueOf(c));
            } else {
                tokens.set(tokens.size()-1, tokens.get(tokens.size()-1)+c);
            }
        }
        return tokens;
    }

    public static int prio(String s) {
        if (s.equals("!")) return 3;
        if (s.equals("&")) return 2;
        if (s.equals("|")) return 1;
        return 0;
    }

    public static Predicate<ForumTopic> getPredicate(String s) {
        List<String> tokens = getTokens("("+s+")");
        System.out.println(tokens);

        return topic -> {
            String text = topic.getAllComments().toLowerCase();

            Stack<Boolean> result = new Stack<>();
            Stack<String> operators = new Stack<>();
            for (String token : tokens) {
                if (!isOperator(token)) {
                    result.add(text.contains(token));
                    continue;
                }

                while (!token.equals("(") && !operators.isEmpty() && prio(operators.peek()) >= prio(token)) {
                    String operator = operators.pop();
                    if (operator.equals("(")) {
                        break;
                    } else if (operator.equals("!")) {
                        result.add(!result.pop());
                    } else if (operator.equals("&")) {
                        result.add(result.pop() & result.pop());
                    } else if (operator.equals("|")) {
                        result.add(result.pop() | result.pop());
                    }
                }

                if (!token.equals(")")) operators.add(token);
            }

            if (result.size() != 1) throw new IllegalStateException("ops");
            return result.pop();
        };
    }

}
