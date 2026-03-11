package org.example.data.repository;

import jakarta.transaction.Transactional;
import org.example.data.entity.Entry;
import org.example.dto.enums.EntryState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchedulerEntryRepository extends JpaRepository<Entry, Long> {
    /**
     * Updates the state of entries based on the given list of transfer Ids.
     *
     * @param transferIds List of transfer Ids to update the state for.
     * @param state       The new state to set.
     * @return The number of entries updated.
     */
    @Modifying
    @Transactional
    @Query("update Entry e set e.state = :state where e.transfer.id in :transferIds")
    int updateStateByTransferIds(@Param("transferIds") List<Long> transferIds,
                                 @Param("state") EntryState state);

    /**
     * Возвращает список записей {@link Entry}, находящихся в указанном состоянии.
     * <p>
     * Используется для выборки всех записей, чей статус (state) соответствует переданному значению.
     *
     * @param state состояние записей, которые необходимо вернуть
     * @return список записей с указанным состоянием
     */
    List<Entry> findByState(EntryState state);
}