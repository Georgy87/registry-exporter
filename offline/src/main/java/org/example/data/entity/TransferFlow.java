package org.example.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.enums.PaymentFlowType;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@Entity(name = "TransferFlow")
@Table(name = "t_transfer_flow")
@Accessors(chain = true)
@Slf4j
//@Immutable
public class TransferFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contractFlowSequence")
    @SequenceGenerator(name = "contractFlowSequence", sequenceName = "seq_contract_flow_id", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originator_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Bank originator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Bank receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_participant_id", nullable = false)
    @JsonIgnore
    private Bank settlementParticipant;

    @Enumerated(EnumType.STRING)
    private PaymentFlowType paymentFlowType;
}
