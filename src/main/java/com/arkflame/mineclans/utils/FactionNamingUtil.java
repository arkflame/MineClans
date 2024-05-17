package com.arkflame.mineclans.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class FactionNamingUtil {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 12;
    private static final Set<String> BLACKLIST = new HashSet<>();
    private static final Pattern BLACKLIST_PATTERN;

    static {
        // Add blacklisted names to the set
        BLACKLIST.add("pene");
        BLACKLIST.add("forro");
        BLACKLIST.add("forra");
        BLACKLIST.add("puto");
        BLACKLIST.add("puta");
        BLACKLIST.add("owner");
        BLACKLIST.add("admi");
        BLACKLIST.add("mod");
        BLACKLIST.add("helpe");
        BLACKLIST.add("builde");
        BLACKLIST.add("mitico");
        BLACKLIST.add("titan");
        BLACKLIST.add("mega");
        BLACKLIST.add("ultra");

        // Generate regex pattern for blacklisted words and their repeated characters
        StringBuilder patternBuilder = new StringBuilder();
        for (String word : BLACKLIST) {
            if (patternBuilder.length() > 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append(word.charAt(0));
            for (int i = 1; i < word.length(); i++) {
                patternBuilder.append('+').append(word.charAt(i));
            }
        }
        BLACKLIST_PATTERN = Pattern.compile(".*(" + patternBuilder.toString() + ").*");
    }

    public static void checkName(String name) {
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH || !name.matches("[a-zA-Z0-9]*")) {
            throw new IllegalArgumentException("Invalid faction name");
        }
        String lowerName = name.toLowerCase();
        if (BLACKLIST_PATTERN.matcher(lowerName).matches()) {
            throw new IllegalArgumentException("Blacklisted name");
        }
    }
}
