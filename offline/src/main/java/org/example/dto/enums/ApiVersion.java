package org.example.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiVersion {

    /**
     * Первая версия API.
     */
    V1("1"),

    /**
     * Вторая версия API с поддержкой СБП.
     */
    V2("2"),

    /**
     * Третья версия API с защитой от MITM.
     */
    V3("3");

    private final String strNumber;

}