package com.example.parkingpermitapp.data;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StateRegex {
    private static final Map<String, Pattern> statePlatePatterns = new HashMap<>();

    public static Pattern getRegex(String state) {
        // Initialize regex patterns for each state
        statePlatePatterns.put("AL", Pattern.compile("\\d{1,2}[A-Z]{2}\\d{3}"));
        statePlatePatterns.put("AK", Pattern.compile("[A-Z]{3}\\d{3}"));
        statePlatePatterns.put("AZ", Pattern.compile("[A-Z]{3}\\d{1}[A-Z]{1}\\d{2}"));
        statePlatePatterns.put("AR", Pattern.compile("[A-Z]{3}\\d{2}[A-Z]{1}"));
        statePlatePatterns.put("CA", Pattern.compile("\\d{1}[A-Z]{3}\\d{3}"));
        statePlatePatterns.put("CO", Pattern.compile("[A-Z]{3}-\\d{3}"));
        statePlatePatterns.put("CT", Pattern.compile("([A-Z]{2}•\\d{5})|(\\d{1}[A-Z]{2}•\\d{1}[A-Z]{1}\\d{1})"));
        statePlatePatterns.put("DE", Pattern.compile("\\d{1,6}"));
        statePlatePatterns.put("DC", Pattern.compile("[A-Z]{2}-\\d{4}"));
        statePlatePatterns.put("FL", Pattern.compile("([A-Z]{3}\\sD{1}\\d{2})|(\\d{3}\\s[A-Z]{3})|([A-Z]{1}\\d{2}\\s\\d{1}[A-Z]{2})"));
        statePlatePatterns.put("GA", Pattern.compile("[A-Z]{3}\\d{4}"));
        statePlatePatterns.put("GU", Pattern.compile("[A-Z]{2}\\s\\d{4}"));
        statePlatePatterns.put("HI", Pattern.compile("[A-Z]{1,3}[A-Z]?\\d{3}"));
        statePlatePatterns.put("ID", Pattern.compile("[A-Z]{1}\\s\\d{6}"));
        statePlatePatterns.put("IL", Pattern.compile("[A-Z]{2}\\s\\d{5}"));
        statePlatePatterns.put("IN", Pattern.compile("\\d{3}[A-Z]{0,3}"));
        statePlatePatterns.put("IA", Pattern.compile("[A-Z]{3}\\s\\d{3}"));
        statePlatePatterns.put("KS", Pattern.compile("\\d{3}\\s[A-Z]{3}|(\\d{4}[A-Z]{3})"));
        statePlatePatterns.put("KY", Pattern.compile("[A-Z]{3}\\d{3}"));
        statePlatePatterns.put("LA", Pattern.compile("[A-Z]{3}\\s\\d{3}"));
        statePlatePatterns.put("ME", Pattern.compile("\\d{4}\\s[A-Z]{2}"));
        statePlatePatterns.put("MD", Pattern.compile("1[A-Z]{2}\\d{4}"));
        statePlatePatterns.put("MA", Pattern.compile("(1[A-Z]{1}\\s\\d{3})|(\\d{3}\\s[A-Z]{3})"));
        statePlatePatterns.put("MI", Pattern.compile("(1[A-Z]{3}\\d{2})|([A-Z]{3}\\s\\d{4})"));
        statePlatePatterns.put("MN", Pattern.compile("(123-[A-Z]{3})|(ABC-123)"));
        statePlatePatterns.put("MS", Pattern.compile("[A-Z]{3}\\s\\d{3}"));
        statePlatePatterns.put("MO", Pattern.compile("[A-Z]{2}\\d{1}\\s[A-Z]{1}\\d{1}"));
        statePlatePatterns.put("MT", Pattern.compile("(0-\\d{5}[A-Z]{1})|(ABC\\d{3})"));
        statePlatePatterns.put("NE", Pattern.compile("(0-[A-Z]{1}\\d{4})|(0-[A-Z]{1}\\d{5})"));
        statePlatePatterns.put("NV", Pattern.compile("\\d{3}·[A-Z]{2}\\d{2}"));
        statePlatePatterns.put("NH", Pattern.compile("\\d{3}\\s\\d{3,4}"));
        statePlatePatterns.put("NJ", Pattern.compile("(D\\d{2}-[A-Z]{3})|(ABC-[A-Z]{2})"));
        statePlatePatterns.put("NM", Pattern.compile("(123-[A-Z]{3})|(ABC-123)"));
        statePlatePatterns.put("NY", Pattern.compile("[A-Z]{3}-\\d{4}"));
        statePlatePatterns.put("NC", Pattern.compile("[A-Z]{3}-\\d{4}"));
        statePlatePatterns.put("ND", Pattern.compile("\\d{3}\\s[A-Z]{3}"));
        statePlatePatterns.put("MP", Pattern.compile("[A-Z]{3}\\s\\d{3}"));
        statePlatePatterns.put("OH", Pattern.compile("[A-Z]{3}\\s\\d{4}"));
        statePlatePatterns.put("OK", Pattern.compile("[A-Z]{3}-\\d{3}"));
        statePlatePatterns.put("OR", Pattern.compile("(\\d{3}\\s[A-Z]{3})|(ABC\\s\\d{3})"));
        statePlatePatterns.put("PA", Pattern.compile("[A-Z]{3}-\\d{4}"));
        statePlatePatterns.put("PR", Pattern.compile("[A-Z]{3}-\\d{3}"));
        statePlatePatterns.put("RI", Pattern.compile("(1[A-Z]{2}-\\d{3})|(\\d{6})"));
        statePlatePatterns.put("SC", Pattern.compile("[A-Z]{3}\\s\\d{3}"));
        statePlatePatterns.put("SD", Pattern.compile("(0[A-Z]{1}\\d{1}\\s\\d{3})|(0[A-Z]{2}\\s\\d{3})"));
        statePlatePatterns.put("TN", Pattern.compile("[A-Z]{3}\\s\\d{4}"));
        statePlatePatterns.put("TX", Pattern.compile("[A-Z]{3}-\\d{4}"));
        statePlatePatterns.put("UT", Pattern.compile("(1[A-Z]{2}\\s\\d{3})|(A\\d{2}\\s\\d{3}[A-Z]{1})"));
        statePlatePatterns.put("VT", Pattern.compile("(\\d{3}\\s\\d{3})|(12[A-Z]{2}\\d{1})"));
        statePlatePatterns.put("VI", Pattern.compile("[A-Z]{3}\\s\\d{3}"));
        statePlatePatterns.put("VA", Pattern.compile("[A-Z]{3}-\\d{4}"));
        statePlatePatterns.put("WA", Pattern.compile("([A-Z]{3}\\d{4})|(\\d{3}-[A-Z]{3})"));
        statePlatePatterns.put("WV", Pattern.compile("[A-Z]{3}-\\d{4}"));
        statePlatePatterns.put("WI", Pattern.compile("([A-Z]{3}-\\d{4})|(\\d{3}-[A-Z]{3})"));
        statePlatePatterns.put("WY", Pattern.compile("(0-\\d{5})|(0-\\d{4}[A-Z]{1})"));

        return statePlatePatterns.getOrDefault(state, Pattern.compile(""));
    }

    public static boolean isPlateStandard(String plateNumber) {
        for (Pattern pattern : statePlatePatterns.values()) {
            if (pattern.matcher(plateNumber).matches()) {
                return true; // Plate matches a standard format
            }
        }
        return false; // No match found, plate does not follow a standard format
    }
}
