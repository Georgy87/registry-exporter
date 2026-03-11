package org.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.example.dto.enums.State;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originatorReferenceNumber;

    private String platformReferenceNumber;

    private ZonedDateTime transferDate;

    @Enumerated(EnumType.STRING)
    private State transferState;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date settelmentDate;

    @Column(name = "originator_id", updatable = false, insertable = false)
    private Long originatorId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Bank originator;

    @Column(name = "receiver_id", updatable = false, insertable = false)
    private Long receiverId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Bank receiver;
}