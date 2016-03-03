package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventType;
import com.blispay.common.metrics.report.Slf4jEventReporter;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class Slf4jEventReporterTest extends AbstractMetricsTest {

    @Test
    public void testSlf4jEventReporter() {
        final Logger log = mock(Logger.class);

        final MetricService metricService = new MetricService("appId");
        metricService.addEventSubscriber(new Slf4jEventReporter(log));
        metricService.start();

        final EventRepository<PiiBusinessEventData> repo = metricService
                .eventRepository(PiiBusinessEventData.class)
                .ofType(EventType.BUSINESS_EVT)
                .inGroup(EventGroup.MERCHANT_DOMAIN)
                .withName("business-event");

        repo.save(defaultPiiBusinessEventData());

        // Note that userName should be filtered out by the pii jackson filter.
        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("notes", "Some notes");
        expectedData.put("count", 1);

        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(log).info(argument.capture());

        assertEquals(1, argument.getAllValues().size());
    }


}
