package org.example.service;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JsonSignatureDataGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    private static final String[] SKIP_FIELDS = {"checkSum"};

    private static final FilterProvider SIGNATURE_FILTERS = new SimpleFilterProvider()
            .addFilter("skipFields", SimpleBeanPropertyFilter.serializeAllExcept(SKIP_FIELDS));

    public static String toSignatureString(Object object) {
        try {
            ObjectWriter writer = MAPPER.writer(SIGNATURE_FILTERS);
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 не поддерживается", e);
        }
    }

    public static void main(String[] args) {
        String inputJson = """
                
                """;

        try {
            // Применяем фильтр на все JSON-объекты
            MAPPER.addMixIn(Map.class, FilterMixin.class);

            // Преобразуем строку в объект
            Map<String, Object> jsonObject = MAPPER.readValue(inputJson, Map.class);

            // Получаем строку для подписи
            String signatureData = toSignatureString(jsonObject);

            // Вычисляем хеш
            String hash = sha256Hex(signatureData);

            // Печатаем
            System.out.println("Строка для подписи:\n" + signatureData);
            System.out.println("\nSHA-256 хеш:\n" + hash);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JsonFilter("skipFields")
    public static class FilterMixin {
    }

    public static class JsonSignatureHashGenerator {

        private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
        private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern(PATTERN);

        private static final ObjectMapper MAPPER = new ObjectMapper()
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule());

        private static final String[] SKIP_FIELDS = {"checkSum"};

        private static final FilterProvider SIGNATURE_FILTERS = new SimpleFilterProvider()
                .addFilter("skipFields", SimpleBeanPropertyFilter.serializeAllExcept(SKIP_FIELDS));

        public static String toSortedJson(Object object, FilterProvider filters) {
            try {
                ObjectWriter writer = MAPPER.writer(filters);
                return writer.writeValueAsString(object);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка сериализации JSON", e);
            }
        }

        public static String sha256Hex(String input) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder(2 * hash.length);
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 не поддерживается", e);
            }
        }

        public static void main(String[] args) {
            // Пример JSON как Java-объект
            OperationReportDtoV2 report = new OperationReportDtoV2();
            report.setCeFileId(123456L);
            report.setApiVersion("CE_V2");
            report.setCreationTime(ZonedDateTime.parse("2025-06-17T12:34:56+03:00"));
            report.setPeriodStart(ZonedDateTime.parse("2025-06-17T00:00:00+03:00"));
            report.setPeriodEnd(ZonedDateTime.parse("2025-06-17T23:59:59+03:00"));
            report.setCheckSum("this-will-be-ignored");

            MAPPER.addMixIn(OperationReportDtoV2.class, FilterMixin.class);

            String signatureString = toSortedJson(report, SIGNATURE_FILTERS);
            String hash = sha256Hex(signatureString);

            System.out.println("=== Строка для подписи ===");
            System.out.println(signatureString);
            System.out.println("\n=== SHA-256 ===");
            System.out.println(hash);
        }

        @JsonFilter("skipFields")
        public static class FilterMixin {
        }

        @Data
        @Accessors(chain = true)
        public static class OperationReportDtoV2 {
            private String apiVersion;
            private Long ceFileId;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN, timezone = "Europe/Moscow")
            private ZonedDateTime creationTime;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN, timezone = "Europe/Moscow")
            private ZonedDateTime periodStart;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN, timezone = "Europe/Moscow")
            private ZonedDateTime periodEnd;

            private String checkSum;

            @JsonIgnore
            public String signatureData() {
                return JsonSignatureHashGenerator.toSortedJson(this, SIGNATURE_FILTERS);
            }
        }
    }
}
