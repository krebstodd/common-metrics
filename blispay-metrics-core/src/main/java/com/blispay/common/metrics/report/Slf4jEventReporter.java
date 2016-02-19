package com.blispay.common.metrics.report;

import com.blispay.common.metrics.event.MetricEvent;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Slf4jEventReporter extends EventReporter {

    private final Map<MetricEvent.Level, Consumer<String>> eventLoggers;

    public Slf4jEventReporter(final Logger eventLogger) {
        this.eventLoggers = loggerMap(eventLogger);
    }

    private static Map<MetricEvent.Level, Consumer<String>> loggerMap(final Logger logger) {

        final Map<MetricEvent.Level, Consumer<String>> temp = new HashMap<>();

        temp.put(MetricEvent.Level.INFO, logger::info);
        temp.put(MetricEvent.Level.WARNING, logger::warn);
        temp.put(MetricEvent.Level.ERROR, logger::error);

        return Collections.unmodifiableMap(temp);

    }

    @Override
    public void acceptEvent(final MetricEvent event) {
        eventLoggers.get(event.getLevel()).accept(event.printJson());
    }

}
