package com.example.tradingcards;

import android.util.Pair;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAll {

    public static ArrayList<Pair<String, String>> get(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        ArrayList<Pair<String, String>> matches = new ArrayList<Pair<String, String>>();
        while (matcher.find()) {
            Pair<String, String> pair = new Pair<String, String>(matcher.group(0), matcher.group(1));
            matches.add(pair);
        }
        return matches;
    }
}
