package org.example.data.repository

import org.example.data.entity.Transfer
import org.example.dto.enums.State
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class SchedulerTransferDao(private val repo: TransferRepository) {
    fun findTransfersForReports(completed: State, dateTime: ZonedDateTime): List<Transfer> {
        return repo.findByTransferStateAndSettelmentDateIsNullAndTransferDateIsBefore(completed, dateTime);
    }
}

interface TransferRepository : JpaRepository<Transfer, Long> {
    fun findByTransferStateAndSettelmentDateIsNullAndTransferDateIsBefore(
        completed: State,
        dateTime: ZonedDateTime
    ): List<Transfer>
}