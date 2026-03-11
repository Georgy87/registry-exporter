package org.example.configuration;

import org.example.dto.enums.PaymentFlowType;
import org.example.service.AbstractEntryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
class EntryServiceIdsConfiguration {

    @Bean
    public Map<PaymentFlowType, AbstractEntryService> entryServicesMap(List<AbstractEntryService> entryServices) {
        return entryServices.stream()
                .collect(Collectors.toMap(AbstractEntryService::getSuitablePaymentFlow, Function.identity()));
    }
}
