package com.blispay.common.metrics.report;

import com.blispay.common.metrics.util.MetricEvent;
import com.blispay.common.metrics.util.RecordableEvent;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BpSlf4jEventReporter extends BpEventReporter {

    private static final RecordableEvent.Level DEFAULT_LEVEL = RecordableEvent.Level.INFO;

    private final Map<RecordableEvent.Level, Consumer<String>> eventLoggers;

    public BpSlf4jEventReporter(final Logger eventLogger) {
        this.eventLoggers = loggerMap(eventLogger);
    }

    @Override
    public void acceptEvent(final MetricEvent event) {

        final Consumer<String> logger;

        if (event instanceof RecordableEvent) {

            logger = eventLoggers.get(((RecordableEvent) event).getLevel());

            if (logger == null) {
                throw new IllegalArgumentException("Unknown log level - " + ((RecordableEvent) event).getLevel());
            }

        } else {
            logger = eventLoggers.get(DEFAULT_LEVEL);
        }

        logger.accept(event.print());
    }

    private static Map<RecordableEvent.Level, Consumer<String>> loggerMap(final Logger logger) {

        final Map<RecordableEvent.Level, Consumer<String>> temp = new HashMap<>();

        temp.put(RecordableEvent.Level.INFO, logger::info);
        temp.put(RecordableEvent.Level.DEBUG, logger::debug);
        temp.put(RecordableEvent.Level.WARN, logger::warn);
        temp.put(RecordableEvent.Level.ERROR, logger::error);
        temp.put(RecordableEvent.Level.TRACE, logger::trace);

        return Collections.unmodifiableMap(temp);

    }

}
