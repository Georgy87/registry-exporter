package org.example.service.entry.P1;

import org.example.data.entity.Entry;
import org.example.data.entity.Transfer;
import org.example.data.repository.SchedulerEntryRepository;
import org.example.dto.enums.EntryState;
import org.example.dto.enums.PaymentFlowType;
import org.example.service.AbstractEntryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class IncomingEntryServiceP1 extends AbstractEntryService {
    private final SchedulerEntryRepository schedulerEntryRepository;

    public IncomingEntryServiceP1(SchedulerEntryRepository schedulerEntryRepository) {
        this.schedulerEntryRepository = schedulerEntryRepository;
    }

    @Override
    public PaymentFlowType getSuitablePaymentFlow() {
        return PaymentFlowType.INCOMING_V1;
    }

    @Override
    public List<Entry> createAndSaveEntries(Transfer transfer, ZonedDateTime time) {
        Entry entry = new Entry();

        entry.setTransfer(transfer);
        entry.setBusinessDay(Date.from(time.toInstant()));
        entry.setState(EntryState.WAITING);
        // Случайное число от 100.00 до 1000.00 с двумя знаками после запятой
        double randomValue = ThreadLocalRandom.current().nextDouble(100.00, 1000.00);
        BigDecimal randomAmount = BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);

        entry.setAmount(randomAmount);

        entry.setBank(transfer.getOriginator());

        return List.of(entry);
    }
}
