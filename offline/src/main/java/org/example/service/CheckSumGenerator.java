package org.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CheckSumGenerator {

    private static final DecimalFormat DECIMAL_FORMATTER;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DECIMAL_FORMATTER = new DecimalFormat("0.00", symbols);
    }

    public static String formatForSignature(BigDecimal value) {
        if (value == null) return "null";
        return DECIMAL_FORMATTER.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    public static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    public static void main(String[] args) {
        BigDecimal amount1 = new BigDecimal("530.50");
//        BigDecimal amount2 = new BigDecimal("1061.1");

        String str1 = formatForSignature(amount1); // → "530.50"
//        String str2 = formatForSignature(amount2); // → "1061.00"
        String combined = str1;            // → "530.501061.00"

        System.out.println("Formatted string: " + combined);
        System.out.println("SHA-256 hash: " + sha256(combined));
    }
}