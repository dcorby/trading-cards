package com.example.tradingcards;

import android.util.Pair;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAll {

    public static ArrayList<ArrayList<String>> get(String regex, String content) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        //ArrayList<Pair<String, String>> matches = new ArrayList<Pair<String, String>>();
        //while (matcher.find()) {
        //    Pair<String, String> pair = new Pair<String, String>(matcher.group(1), matcher.group(2));
        //    matches.add(pair);
        //}
        //return matches;

        ArrayList<ArrayList<String>> matches = new ArrayList<ArrayList<String>>();

        while (matcher.find()) {
            ArrayList<String> tmp = new ArrayList<String>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                tmp.add(matcher.group(i));
            }
            matches.add(tmp);
        }
        return matches;
    }
}
