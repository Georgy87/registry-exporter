package org.example.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.EntryState;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Slf4j
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entrySequence")
    @SequenceGenerator(name = "entrySequence", sequenceName = "entry_seq", allocationSize = 1)
    private Long id;

    @Column(name = "transfer_id", updatable = false, insertable = false)
    private Long transferId;
    @ManyToOne
    @ToString.Exclude
    private Transfer transfer;

    private Date businessDay;

    @Enumerated(EnumType.STRING)
    private EntryState state;

    private BigDecimal amount;

    @Column(name = "bank_id", updatable = false, insertable = false)
    private Long bankId;
    @ManyToOne
    private Bank bank;

}