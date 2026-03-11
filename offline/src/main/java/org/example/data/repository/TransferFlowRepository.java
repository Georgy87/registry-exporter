package org.example.data.repository;

import org.example.data.entity.TransferFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferFlowRepository extends JpaRepository<TransferFlow, Long> {
    @Query(value = "SELECT * FROM t_transfer_flow " +
            "WHERE (originator_id = :originator OR receiver_id = :receiver) " +
            "AND settlement_participant_id = :settlement", nativeQuery = true)
    List<TransferFlow> findByOriginatorOrReceiverAndSettlementParticipant(@Param("originator") Long originator,
                                                                          @Param("receiver") Long receiver,
                                                                          @Param("settlement") Long settlementParticipant);
}
