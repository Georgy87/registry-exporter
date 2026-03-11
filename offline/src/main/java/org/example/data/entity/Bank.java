package org.example.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.enums.BankName;

@Entity
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bankSequence")
    @SequenceGenerator(name = "bankSequence", sequenceName = "bank_seq", allocationSize = 1)
    @Access(AccessType.PROPERTY)
    private Long id;

    /**
     * Название банка
     */
    @Enumerated(EnumType.STRING)
    private BankName name;

    private String inn;
}

