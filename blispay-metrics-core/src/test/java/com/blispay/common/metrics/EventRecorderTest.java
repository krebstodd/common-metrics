package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpHistogram;
import com.blispay.common.metrics.metric.BpMeter;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.report.BpEventReporter;
import com.blispay.common.metrics.report.BpSlf4jEventReporter;
import com.blispay.common.metrics.report.DefaultBpEventRecordingService;
import com.blispay.common.metrics.report.NoOpEventRecordingService;
import com.blispay.common.metrics.util.RecordableEvent;
import com.blispay.common.metrics.util.StopWatch;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jext.LoggerFactory;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventRecorderTest extends AbstractMetricsTest {

    @Test
    public void testServiceRecordsCounterEvents() {

        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String counterName = "testCounter";
        final BpCounter counter = service.createCounter(getClass(), counterName, "Event recording counter");
        counter.enableEventRecording(Boolean.TRUE);

        assertTrue(reporter.history().isEmpty());

        counter.increment();
        counter.increment(2L);
        counter.decrement();
        counter.decrement(3L);

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(4, events.size());
        assertThat(events.poll(), counterEventMatcher(counter.getName(), "1"));
        assertThat(events.poll(), counterEventMatcher(counter.getName(), "2"));
        assertThat(events.poll(), counterEventMatcher(counter.getName(), "-1"));
        assertThat(events.poll(), counterEventMatcher(counter.getName(), "-3"));

    }

    @Test
    public void testServiceRecordsTimerEvents() throws InterruptedException {

        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String timerName = "testTimer";
        final BpTimer timer = service.createTimer(getClass(), timerName, "Event recording timer");

        assertTrue(reporter.history().isEmpty());

        final StopWatch stopWatch = timer.time();
        Thread.sleep(100);
        stopWatch.lap("Lap 1");
        Thread.sleep(100);
        stopWatch.lap("Lap 2");
        Thread.sleep(100);
        stopWatch.stop("TimerDone");

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(3, events.size());
        assertThat(events.poll(), timerEventMatcher(timer.getName(), "Lap 1", "1"));
        assertThat(events.poll(), timerEventMatcher(timer.getName(), "Lap 2", "2"));
        assertThat(events.poll(), timerEventMatcher(timer.getName(), "TimerDone", "3"));

        thrown.expect(IllegalStateException.class);
        stopWatch.lap();
    }

    @Test
    public void testServiceRecordsHistogramEvents() {
        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String counterName = "testHistogram";
        final BpHistogram histogram = service.createHistogram(getClass(), counterName, "Event recording counter");
        histogram.enableEventRecording(Boolean.TRUE);

        assertTrue(reporter.history().isEmpty());

        histogram.update(2L);
        histogram.update(5);

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(2, events.size());
        assertThat(events.poll(), histogramEventMatcher(histogram.getName(), "2"));
        assertThat(events.poll(), histogramEventMatcher(histogram.getName(), "5"));
    }
    
    @Test
    public void testServiceRecordsMeterEvents() {
        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String counterName = "testMeter";
        final BpMeter meter = service.createMeter(getClass(), counterName, "Event recording counter");
        meter.enableEventRecording(Boolean.TRUE);

        assertTrue(reporter.history().isEmpty());

        meter.mark();
        meter.mark(2L);
        meter.mark(5);

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(3, events.size());
        assertThat(events.poll(), meterEventMatcher(meter.getName(), "1"));
        assertThat(events.poll(), meterEventMatcher(meter.getName(), "2"));
        assertThat(events.poll(), meterEventMatcher(meter.getName(), "5"));
    }

    @Test
    public void testServiceDoesNotRecordGaugeEvents() {
        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String gaugeCheck = "testGauge";
        final BpGauge gauge = service.createGauge(getClass(), gaugeCheck, "Event recording counter", () -> Boolean.TRUE);

        assertTrue(reporter.history().isEmpty());

        gauge.getValue();

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(0, events.size());
    }

    @Test
    public void testNoOpService() {
        final BpMetricService service = new BpMetricService(new NoOpEventRecordingService());

        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        service.addEventReporter(reporter);

        final String counterName = "testCounter";
        final BpCounter counter = service.createCounter(getClass(), counterName, "Event recording counter");
        counter.enableEventRecording(Boolean.TRUE);

        assertTrue(reporter.history().isEmpty());

        counter.increment();

        assertTrue(reporter.history().isEmpty());
    }

    @Test
    public void disableEventRecordingForSingleMetric() {

        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String c1Name = "testCounter1";
        final String c2Name = "testCounter2";

        final BpCounter recordingCounter1 = service.createCounter(getClass(), c1Name, "Event recording counter");
        final BpCounter recordingCounter2 = service.createCounter(getClass(), c2Name, "Event recording counter");
        final BpCounter silentCounter = service.createCounter(getClass(), "silent", "Silent recording counter");

        recordingCounter1.enableEventRecording(Boolean.TRUE);
        recordingCounter2.enableEventRecording(Boolean.TRUE);
        silentCounter.enableEventRecording(Boolean.FALSE);

        assertTrue(reporter.history().isEmpty());

        recordingCounter1.increment();
        recordingCounter2.decrement();
        silentCounter.increment();

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(2, events.size());
        assertThat(events.poll(), counterEventMatcher(recordingCounter1.getName(), "1"));
        assertThat(events.poll(), counterEventMatcher(recordingCounter2.getName(), "-1"));

    }

    @Test
    public void testMultipleLoggers() {

        final TestableBpEventReporter reporter1 = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter1);

        final TestableBpEventReporter reporter2 = new TestableBpEventReporter();
        service.addEventReporter(reporter2);

        final String c1Name = "testCounter1";

        final BpCounter counter1 = service.createCounter(getClass(), c1Name, "Event recording counter");
        counter1.enableEventRecording(Boolean.TRUE);

        assertTrue(reporter1.history().isEmpty());
        assertTrue(reporter2.history().isEmpty());

        counter1.increment();

        final Queue<RecordableEvent> events1 = reporter1.history();

        assertEquals(1, events1.size());
        assertThat(events1.poll(), counterEventMatcher(counter1.getName(), "1"));

        final Queue<RecordableEvent> events2 = reporter2.history();

        assertEquals(1, events2.size());
        assertThat(events2.poll(), counterEventMatcher(counter1.getName(), "1"));

    }

    @Test
    public void testLoggerLevels() throws InterruptedException {
        final TestableBpEventReporter reporter = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter);

        final String timerName = "testTimer";
        final BpTimer timer = service.createTimer(getClass(), timerName, "Event recording timer");

        final Long warnTime = 100L;
        final Long errTime = 200L;

        timer.setEventRecordLevelFn((evt) -> {
                if (evt.getSampleState() < warnTime) {
                    return RecordableEvent.Level.INFO;
                } else if (evt.getSampleState() < errTime) {
                    return RecordableEvent.Level.WARN;
                } else {
                    return RecordableEvent.Level.ERROR;
                }
            });

        assertTrue(reporter.history().isEmpty());

        final StopWatch stopWatch = timer.time();
        stopWatch.lap("Information");
        Thread.sleep(warnTime);
        stopWatch.lap("Warning");
        Thread.sleep(errTime - warnTime);
        stopWatch.lap("Error");

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(3, events.size());
        assertThat(events.poll(), new RecordableEventMatcher(any(String.class), equalTo(RecordableEvent.Level.INFO)));
        assertThat(events.poll(), new RecordableEventMatcher(any(String.class), equalTo(RecordableEvent.Level.WARN)));
        assertThat(events.poll(), new RecordableEventMatcher(any(String.class), equalTo(RecordableEvent.Level.ERROR)));
    }

    @Test
    public void testSlf4jEventRecording() throws InterruptedException {
        final BpMetricService service = new BpMetricService();
        service.addEventReporter(new BpSlf4jEventReporter(LoggerFactory.getLogger(EventRecorderTest.class)));

        final TestLogger testLogger = TestLoggerFactory.getTestLogger(EventRecorderTest.class);

        final String timerName = "testTimer";
        final BpTimer timer = service.createTimer(getClass(), timerName, "Event recording timer");

        final Long warnTime = 200L;
        final Long errTime = 300L;

        timer.setEventRecordLevelFn(evt -> {
                if (evt.getSampleState() < warnTime) {
                    return RecordableEvent.Level.INFO;
                } else if (evt.getSampleState() < errTime) {
                    return RecordableEvent.Level.WARN;
                } else {
                    return RecordableEvent.Level.ERROR;
                }
            });

        final StopWatch stopWatch = timer.time();
        stopWatch.lap("Information");
        Thread.sleep(warnTime);
        stopWatch.lap("Warning");
        Thread.sleep(errTime - warnTime);
        stopWatch.lap("Error");

        final List<LoggingEvent> event = testLogger.getLoggingEvents();

        assertEquals(3, event.size());
        assertThat(event.get(0), new Slf4jLogMessageMatcher(Level.INFO, keyValueMatcher("event", "Information")));
        assertThat(event.get(1), new Slf4jLogMessageMatcher(Level.WARN, keyValueMatcher("event", "Warning")));
        assertThat(event.get(2), new Slf4jLogMessageMatcher(Level.ERROR, keyValueMatcher("event", "Error")));

    }

    @Test
    public void testMessageFilters() {
        final TestableBpEventReporter reporter1 = new TestableBpEventReporter();
        final BpMetricService service = defaultService(reporter1);

        final TestableBpEventReporter reporter2 = new TestableBpEventReporter();
        reporter2.addFilter(event -> event.getMessage().contains("amount=2"));
        service.addEventReporter(reporter2);

        final String c1Name = "testCounter1";
        final BpCounter counter1 = service.createCounter(getClass(), c1Name, "Event recording counter");
        counter1.enableEventRecording(Boolean.TRUE);

        assertTrue(reporter1.history().isEmpty());
        assertTrue(reporter2.history().isEmpty());

        counter1.increment();
        counter1.increment(2L);

        final Queue<RecordableEvent> events1 = reporter1.history();

        assertEquals(2, events1.size());
        assertThat(events1.poll(), counterEventMatcher(counter1.getName(), "1"));
        assertThat(events1.poll(), counterEventMatcher(counter1.getName(), "2"));

        final Queue<RecordableEvent> events2 = reporter2.history();

        assertEquals(1, events2.size());
        assertThat(events2.poll(), counterEventMatcher(counter1.getName(), "2"));
    }

    @Test
    public void testLevelFilter() {
        final TestableBpEventReporter reporter = new TestableBpEventReporter();

        reporter.addFilter(evt -> evt.getLevel() == RecordableEvent.Level.ERROR);

        final BpMetricService service = defaultService(reporter);

        final String histoName = "testHistogram";
        final BpHistogram histogram = service.createHistogram(getClass(), histoName, "Event recording counter");
        histogram.enableEventRecording(Boolean.TRUE);
        histogram.setEventRecordLevelFn(sample -> {
                if (sample.getSampleState() > 3L) {
                    return RecordableEvent.Level.ERROR;
                } else {
                    return RecordableEvent.Level.INFO;
                }
            });

        assertTrue(reporter.history().isEmpty());

        histogram.update(2L);
        histogram.update(5);

        final Queue<RecordableEvent> events = reporter.history();

        assertEquals(1, events.size());
        assertThat(events.poll(), new RecordableEventMatcher(keyValueMatcher("update", "5"), equalTo(RecordableEvent.Level.ERROR)));
    }

    private static BpMetricService defaultService(final TestableBpEventReporter testReporter) {
        final BpMetricService service = new BpMetricService(new DefaultBpEventRecordingService());
        service.addEventReporter(testReporter);
        return service;
    }

    private static RecordableEventMatcher counterEventMatcher(final String name, final String amount) {
        final Matcher<String> messageMatcher = allOf(
                keyValueMatcher("name", name),
                keyValueMatcher("amount", amount));

        return new RecordableEventMatcher(messageMatcher, equalTo(RecordableEvent.Level.INFO));
    }

    private static RecordableEventMatcher timerEventMatcher(final String name, final String event, final String firstDigitMillis) {
        final Matcher<String> messageMatcher = allOf(
                keyValueMatcher("name", name),
                keyValueMatcher("event", event),
                keyValueMatcher("elapsedMillis", firstDigitMillis));

        return new RecordableEventMatcher(messageMatcher, equalTo(RecordableEvent.Level.INFO));
    }

    private static RecordableEventMatcher histogramEventMatcher(final String name, final String update) {
        final Matcher<String> messageMatcher = allOf(
                keyValueMatcher("name", name),
                keyValueMatcher("update", update));

        return new RecordableEventMatcher(messageMatcher, equalTo(RecordableEvent.Level.INFO));
    }

    private static RecordableEventMatcher meterEventMatcher(final String name, final String update) {
        final Matcher<String> messageMatcher = allOf(
                keyValueMatcher("name", name),
                keyValueMatcher("numOccurrences", update));

        return new RecordableEventMatcher(messageMatcher, equalTo(RecordableEvent.Level.INFO));
    }

    private static Matcher<String> keyValueMatcher(final String key, final String value) {
        return containsString(key + "=" + value);
    }

    private static class RecordableEventMatcher extends TypeSafeMatcher<RecordableEvent> {

        private final Matcher<String> messageMatcher;

        private final Matcher<RecordableEvent.Level> levelMatcher;

        public RecordableEventMatcher(final Matcher<String> messageMatcher, final Matcher<RecordableEvent.Level> levelMatcher) {
            this.messageMatcher = messageMatcher;
            this.levelMatcher = levelMatcher;
        }

        @Override
        protected boolean matchesSafely(final RecordableEvent recordableEvent) {
            return messageMatcher.matches(recordableEvent.getMessage())
                    && levelMatcher.matches(recordableEvent.getLevel());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("[message=")
                    .appendDescriptionOf(messageMatcher)
                    .appendText(", level=")
                    .appendDescriptionOf(levelMatcher);
        }
    }

    private static class Slf4jLogMessageMatcher extends TypeSafeMatcher<LoggingEvent> {

        private final Matcher<Level> logLevelMatcher;

        private final Matcher<String> logMsgMatcher;

        public Slf4jLogMessageMatcher(final Level logLevel, final Matcher<String> message) {
            this.logLevelMatcher = equalTo(logLevel);
            this.logMsgMatcher = message;
        }

        @Override
        protected boolean matchesSafely(final LoggingEvent loggingEvent) {
            return logLevelMatcher.matches(loggingEvent.getLevel())
                    && logMsgMatcher.matches(loggingEvent.getMessage());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("[level=")
                    .appendDescriptionOf(logLevelMatcher)
                    .appendText(", message=")
                    .appendDescriptionOf(logMsgMatcher);
        }
    }

    private static class TestableBpEventReporter extends BpEventReporter {

        private final LinkedList<RecordableEvent> events = new LinkedList<>();

        @Override
        public void reportEvent(final RecordableEvent event) {
            events.add(event);
        }

        public LinkedList<RecordableEvent> history() {
            return events;
        }
    }

}
