package org.example.service

import org.example.data.entity.Transfer
import org.example.data.repository.SchedulerTransferDao
import org.example.dto.enums.State
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class PerformanceOrientedTransferService(
    private val dao: SchedulerTransferDao
) {
    fun readTransfersForOperationReports(endPeriod: ZonedDateTime): List<Transfer> {
        return dao.findTransfersForReports(State.CONFIRMED, endPeriod)
    }
}

