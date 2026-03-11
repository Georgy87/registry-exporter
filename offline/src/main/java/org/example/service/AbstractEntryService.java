package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.entity.Entry;
import org.example.data.entity.Transfer;
import org.example.dto.enums.PaymentFlowType;

import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEntryService {

    public abstract PaymentFlowType getSuitablePaymentFlow();

    /**
     * Формирует и сохраняет полный набор Entry для одного Transfer.
     *
     * @param transfer - перевод.
     */
    public abstract List<Entry> createAndSaveEntries(Transfer transfer, ZonedDateTime time);

}