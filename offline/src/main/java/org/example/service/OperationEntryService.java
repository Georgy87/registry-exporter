package org.example.service;

import org.example.data.entity.Entry;
import org.example.data.entity.Transfer;
import org.example.data.repository.SchedulerEntryRepository;
import org.example.dto.enums.EntryState;
import org.example.dto.enums.PaymentFlowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OperationEntryService {

    private static final Logger log = LoggerFactory.getLogger(OperationEntryService.class);
    private final SchedulerEntryRepository repo;
    private final Map<PaymentFlowType, AbstractEntryService> entryServicesMap;

    public OperationEntryService(final SchedulerEntryRepository repo, final Map<PaymentFlowType, AbstractEntryService> entryServicesMap) {
        this.repo = repo;
        this.entryServicesMap = entryServicesMap;
    }

    public void invalidateEntriesByTransfer(List<Transfer> transfers) {
        List<Long> transferIds = transfers.stream().map(Transfer::getId).toList();
        this.repo.updateStateByTransferIds(transferIds, EntryState.INVALID);
    }

    public List<Entry> getEntriesByState(EntryState state) {
        return this.repo.findByState(state);
    }

    public List<Entry> createEntries(Transfer transfer, List<Entry> savedEntries, ZonedDateTime time) {
        AbstractEntryService abstractEntryService = (AbstractEntryService) this.entryServicesMap.get(PaymentFlowType.INCOMING_V1);
        log.info("Получаем трансфер: {}", transfer);
        return abstractEntryService.createAndSaveEntries(transfer, time);
    }

}
