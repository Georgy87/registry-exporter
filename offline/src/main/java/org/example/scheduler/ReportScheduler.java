package org.example.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.service.OperationReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportScheduler {
    private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);
    private final OperationReportService operationReportService;

    @Scheduled(fixedRate = 50000)
    public void startReportGeneration() {
        log.info("🕒Запуск процесса создания операционных реестров");
        System.out.println("=== SCHEDULER TRIGGERED ===");
        byte a = 120;
        a = (byte) (a + 1000000000);
        System.out.println(a);
//        operationReportService.createAndSendReport();
    }
}
