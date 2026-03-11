package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.data.entity.Entry;
import org.example.data.entity.Transfer;
import org.example.data.repository.SchedulerEntryRepository;
import org.example.dto.enums.EntryState;
import org.example.util.TimestampUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OperationReportService {

    private static final Logger log = LoggerFactory.getLogger(OperationReportService.class);
    private final SchedulerEntryRepository repo;
    private final OperationEntryService operationEntryService;
    private final PerformanceOrientedTransferService transferService;

    /**
     * Первичная генерация реестров за прошлый час
     */
    @Transactional
    public void createAndSendReport() {
        createAndSendReport(TimestampUtils.atStartHour(ZonedDateTime.now()));
    }

    /**
     * Формирует и отправляет операционный отчет.
     *
     * @param time - время, для которого необходимо сформировать и отправить отчет.
     */
    private void createAndSendReport(ZonedDateTime time) {
        List<Transfer> transfers = transferService.readTransfersForOperationReports(time);
//        operationEntryService.invalidateEntriesByTransfer(transfers);
        List<Entry> savedEntries = operationEntryService.getEntriesByState(EntryState.WAITING);
        // Собираем все записи Entry, созданные по каждому Transfer
        List<Entry> allEntries = new ArrayList<>();
        for (Transfer transfer : transfers) {
            log.info("Transfers {}", transfer.getId());
            List<Entry> entries = operationEntryService.createEntries(transfer, savedEntries, time);
            allEntries.addAll(entries);
        }

//        repo.saveAll(allEntries);
        log.info("Созданные записи Entry: {}", allEntries);
    }

}
