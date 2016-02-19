package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventFilter;
import com.blispay.common.metrics.event.EventListener;
import com.blispay.common.metrics.event.MetricEvent;
import com.blispay.common.metrics.metric.BpCounter;
import com.blispay.common.metrics.metric.BpGauge;
import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.metric.BusinessMetricName;
import com.blispay.common.metrics.metric.MappedMetricContext;
import com.blispay.common.metrics.metric.Measurement;
import com.blispay.common.metrics.metric.MetricType;
import com.blispay.common.metrics.metric.MetricName;
import com.blispay.common.metrics.metric.MetricClass;
import com.blispay.common.metrics.report.Slf4jEventReporter;
import com.blispay.common.metrics.util.StopWatch;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MetricEventsTest extends AbstractMetricsTest {

    @Test
    public void testServiceRecordsCounterEvents() {

        final TestableMetricEventListener reporter = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter);

        final MetricName counterName = new BusinessMetricName("application", "created");
        final BpCounter counter = service.createCounter(counterName, MetricClass.businessEvent());

        assertTrue(reporter.history().isEmpty());

        final Map<String, String> context = new HashMap<>();
        context.put("applicationState", "approved");
        context.put("creditLine", "1000");
        final MappedMetricContext ctx = new MappedMetricContext(context);

        counter.increment(2L);
        counter.decrement(ctx, 3L);

        final Queue<MetricEvent> events = reporter.history();

        assertEquals(2, events.size());

        final Map<String, String> expectedMeasurement = new HashMap<>();
        expectedMeasurement.put("count", "2");
        assertThat(events.poll(), new MetricEventMatcher(counterName, MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(2, "TOTAL")));

        expectedMeasurement.put("count", "-3");
        assertThat(events.poll(), new MetricEventMatcher(counterName, MetricType.COUNT, MetricClass.businessEvent(), context, measurementMatcher(-3, "TOTAL")));

    }

    @Test
    public void testServiceRecordsTimerEvents() throws InterruptedException {

        final TestableMetricEventListener reporter = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter);

        final MetricName timerName = new BusinessMetricName("application", "created");
        final BpTimer timer = service.createTimer(timerName, MetricClass.httpRequest());

        assertTrue(reporter.history().isEmpty());

        final Map<String, String> context = new HashMap<>();
        context.put("step1", "step1Val");
        context.put("step2", "step2Val");
        final MappedMetricContext ctx = new MappedMetricContext(context);

        final StopWatch stopWatch = timer.time();
        Thread.sleep(100);
        stopWatch.lap();
        Thread.sleep(100);
        stopWatch.lap(ctx);
        Thread.sleep(100);
        stopWatch.stop(ctx);

        final Queue<MetricEvent> events = reporter.history();

        assertEquals(3, events.size());

        final MetricEvent e1 = events.poll();
        assertThat(e1, new MetricEventMatcher(timerName, MetricType.PERFORMANCE, MetricClass.httpRequest(), new HashMap<>()));
        assertNotNull(e1.getMeasurement().getValue());
        assertNotNull(e1.getMeasurement().getUnits());

        final MetricEvent e2 = events.poll();
        assertThat(e2, new MetricEventMatcher(timerName, MetricType.PERFORMANCE, MetricClass.httpRequest(), ctx.getContextMap()));
        assertNotNull(e2.getMeasurement().getValue());
        assertNotNull(e2.getMeasurement().getUnits());

        final MetricEvent e3 = events.poll();
        assertThat(e3, new MetricEventMatcher(timerName, MetricType.PERFORMANCE, MetricClass.httpRequest(), ctx.getContextMap()));
        assertNotNull(e3.getMeasurement().getValue());
        assertNotNull(e3.getMeasurement().getUnits());

        thrown.expect(IllegalStateException.class);
        stopWatch.lap();
    }

    @Test
    public void testServiceDoesNotRecordGaugeEvents() {
        final TestableMetricEventListener reporter = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter);

        final MetricName gaugeName = new BusinessMetricName("application", "created");
        final BpGauge gauge = service.createGauge(gaugeName, MetricClass.businessEvent(), Measurement.Units.BOOL, () -> Boolean.TRUE);

        assertTrue(reporter.history().isEmpty());

        gauge.getValue();

        final Queue<MetricEvent> events = reporter.history();

        assertEquals(0, events.size());
    }

    @Test
    public void disableEventRecordingForSingleMetric() {

        final TestableMetricEventListener reporter = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter);

        final BpCounter counter1 = service.createCounter(new BusinessMetricName("application1", "created"), MetricClass.businessEvent());
        final BpCounter counter2 = service.createCounter(new BusinessMetricName("application2", "created"), MetricClass.businessEvent());
        final BpCounter silentCounter = service.createCounter(new BusinessMetricName("application", "created"), MetricClass.businessEvent());

        counter1.setEmitEvents(Boolean.TRUE);
        counter2.setEmitEvents(Boolean.TRUE);
        silentCounter.setEmitEvents(Boolean.FALSE);

        assertTrue(reporter.history().isEmpty());

        counter1.increment(1L);
        counter2.decrement(2L);
        silentCounter.increment(4L);

        final Queue<MetricEvent> events = reporter.history();

        assertEquals(2, events.size());

        final Map<String, String> expectedMeasurement = new HashMap<>();
        expectedMeasurement.put("count", "1");
        assertThat(events.poll(), new MetricEventMatcher(counter1.getName(), MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(1, "TOTAL")));
        expectedMeasurement.put("count", "-2");
        assertThat(events.poll(), new MetricEventMatcher(counter2.getName(), MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(-2, "TOTAL")));

    }

    @Test
    public void testMultipleEventListeners() {

        final TestableMetricEventListener reporter1 = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter1);

        final TestableMetricEventListener reporter2 = new TestableMetricEventListener();
        service.addEventListener(reporter2);

        final BpCounter counter = service.createCounter(new BusinessMetricName("application", "created"), MetricClass.businessEvent());

        assertTrue(reporter1.history().isEmpty());
        assertTrue(reporter2.history().isEmpty());

        counter.increment(1L);

        final Map<String, String> expectedMeasurement = new HashMap<>();
        expectedMeasurement.put("value", "1");

        final Queue<MetricEvent> events1 = reporter1.history();
        assertEquals(1, events1.size());
        assertThat(events1.poll(), new MetricEventMatcher(counter.getName(), MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(1, "TOTAL")));

        final Queue<MetricEvent> events2 = reporter2.history();
        assertEquals(1, events2.size());
        assertThat(events2.poll(), new MetricEventMatcher(counter.getName(), MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(1, "TOTAL")));

    }

    @Test
    public void testTimerLoggerLevel() throws InterruptedException {
        final TestableMetricEventListener reporter = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter);

        final MetricName timerName = new BusinessMetricName("application", "created");
        final BpTimer timer = service.createTimer(timerName, MetricClass.httpRequest());

        final Long warnTime = 100L;
        final Long errTime = 200L;

        timer.setEventRecordLevelFn((time) -> {
                if (time.toMillis() < warnTime) {
                    return MetricEvent.Level.INFO;
                } else if (time.toMillis() < errTime) {
                    return MetricEvent.Level.WARNING;
                } else {
                    return MetricEvent.Level.ERROR;
                }
            });

        assertTrue(reporter.history().isEmpty());

        final StopWatch stopWatch = timer.time();
        stopWatch.lap();
        Thread.sleep(warnTime);
        stopWatch.lap();
        Thread.sleep(errTime - warnTime);
        stopWatch.stop();

        final Queue<MetricEvent> events = reporter.history();

        assertEquals(3, events.size());
        assertEquals(MetricEvent.Level.INFO, events.poll().getLevel());
        assertEquals(MetricEvent.Level.WARNING, events.poll().getLevel());
        assertEquals(MetricEvent.Level.ERROR, events.poll().getLevel());
    }

    @Test
    public void testSlf4jEventRecording() throws InterruptedException {
        final BpMetricService service = new BpMetricService();
        service.addEventListener(new Slf4jEventReporter(LoggerFactory.getLogger(MetricEventsTest.class)));

        final TestableMetricEventListener ter = new TestableMetricEventListener();
        service.addEventListener(ter);

        final TestLogger testLogger = TestLoggerFactory.getTestLogger(MetricEventsTest.class);

        final MetricName timerName = new BusinessMetricName("application", "created");
        final BpTimer timer = service.createTimer(timerName, MetricClass.httpRequest());

        final Long warnTime = 200L;
        final Long errTime = 300L;

        timer.setEventRecordLevelFn(time -> {
                if (time.toMillis() < warnTime) {
                    return MetricEvent.Level.INFO;
                } else if (time.toMillis() < errTime) {
                    return MetricEvent.Level.WARNING;
                } else {
                    return MetricEvent.Level.ERROR;
                }
            });

        final StopWatch stopWatch = timer.time();
        stopWatch.lap();
        Thread.sleep(warnTime);
        stopWatch.lap();
        Thread.sleep(errTime - warnTime);
        stopWatch.lap();

        final List<LoggingEvent> event = testLogger.getLoggingEvents();

        assertEquals(3, event.size());
        assertThat(event.get(0), new Slf4jLogMessageMatcher(Level.INFO, CoreMatchers.equalTo(ter.history().poll().printJson())));
        assertThat(event.get(1), new Slf4jLogMessageMatcher(Level.WARN, CoreMatchers.equalTo(ter.history().poll().printJson())));
        assertThat(event.get(2), new Slf4jLogMessageMatcher(Level.ERROR, CoreMatchers.equalTo(ter.history().poll().printJson())));

    }

    @Test
    public void testMessageFilters() {
        final TestableMetricEventListener reporter1 = new TestableMetricEventListener();
        final BpMetricService service = defaultService(reporter1);

        final TestableMetricEventListener reporter2 = new TestableMetricEventListener();
        reporter2.addFilter(event -> !event.getMeasurement().getValue().toString().equals("2"));
        service.addEventListener(reporter2);

        final MetricName counterName = new BusinessMetricName("application", "created");
        final BpCounter counter = service.createCounter(counterName, MetricClass.businessEvent());

        assertTrue(reporter1.history().isEmpty());
        assertTrue(reporter2.history().isEmpty());

        counter.increment(1l);
        counter.increment(2L);

        final Queue<MetricEvent> events1 = reporter1.history();

        assertEquals(2, events1.size());
        assertThat(events1.poll(), new MetricEventMatcher(counterName, MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(1, "TOTAL")));
        assertThat(events1.poll(), new MetricEventMatcher(counterName, MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(2, "TOTAL")));

        final Queue<MetricEvent> events2 = reporter2.history();

        assertEquals(1, events2.size());
        assertThat(events2.poll(), new MetricEventMatcher(counterName, MetricType.COUNT, MetricClass.businessEvent(), new HashMap<>(), measurementMatcher(1, "TOTAL")));
    }

    private static BpMetricService defaultService(final TestableMetricEventListener testReporter) {
        final BpMetricService service = new BpMetricService();
        service.addEventListener(testReporter);
        return service;
    }

    private static <T> MeasurementMatcher measurementMatcher(final T val, final String units) {
        return new MeasurementMatcher("value", val.toString(), "units", units);
    }

    private static class MetricEventMatcher extends TypeSafeMatcher<MetricEvent> {

        private final Matcher<String> name;
        private final Matcher<String> type;
        private final Matcher<String> mClass;
        private final Map<String, String> context;
        private final MeasurementMatcher mMeasurement;

        public MetricEventMatcher(final MetricName name, final MetricType mType, final MetricClass mClass, final Map<String, String> mContext) {
            this(name, mType, mClass, mContext, new MeasurementMatcher.NotEmpty());
        }

        public MetricEventMatcher(final MetricName name, final MetricType mType, final MetricClass mClass,
                                  final Map<String, String> mContext, final MeasurementMatcher mMeasurement) {

            this(name.getValue(), mType.getValue(), mClass.getValue(), mContext, mMeasurement);
        }

        public MetricEventMatcher(final String name, final String mType, final String mClass,
                                  final Map<String, String> mContext, final MeasurementMatcher mMeasurement) {

            this.name = Matchers.equalTo(name);
            this.type = Matchers.equalTo(mType);
            this.mClass = Matchers.equalTo(mClass);
            this.context = mContext;
            this.mMeasurement = mMeasurement;

        }

        @Override
        public boolean matchesSafely(final MetricEvent evt) {
            System.out.println(">> " + evt.getName().toString());
            System.out.println(name.matches(evt.getName().getValue()));
            System.out.println(type.matches(evt.getType().getValue()));
            System.out.println(mClass.matches(evt.getMetricClass().getValue()));
            System.out.println(mMeasurement.matches(evt.getMeasurement()));
            System.out.println(context.entrySet().stream().allMatch(entry -> evt.getContext().readOnlyContext().get(entry.getKey()).equals(entry.getValue())));
            System.out.println(">>");

            return name.matches(evt.getName().getValue())
                    && type.matches(evt.getType().getValue())
                    && mClass.matches(evt.getMetricClass().getValue())
                    && mMeasurement.matches(evt.getMeasurement())
                    && context.entrySet().stream().allMatch(entry -> evt.getContext().readOnlyContext().get(entry.getKey()).equals(entry.getValue()));
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("name=[")
                    .appendDescriptionOf(name)
                    .appendText("], type=[")
                    .appendDescriptionOf(type)
                    .appendText("], class=[")
                    .appendDescriptionOf(mClass)
                    .appendText("], context=[")
                    .appendValue(context)
                    .appendText("], measurement=[")
                    .appendValue(mMeasurement)
                    .appendText("]");
        }

    }

    public static class MeasurementMatcher extends TypeSafeMatcher<Measurement> {

        private final Matcher<String> valKeyMatcher;
        private final Matcher<String> valMatcher;
        private final Matcher<String> unitKeyMatcher;
        private final Matcher<String> unitMatcher;

        public MeasurementMatcher(final String valKey, final String val, final String unitKey, final String unit) {
            this.valKeyMatcher = Matchers.equalTo(valKey);
            this.valMatcher = Matchers.equalTo(val);
            this.unitKeyMatcher = Matchers.equalTo(unitKey);
            this.unitMatcher = Matchers.equalTo(unit);
        }

        public MeasurementMatcher(final Matcher<String> valKey, final Matcher<String> val, final Matcher<String> unitKey, final Matcher<String> unit) {
            this.valKeyMatcher = valKey;
            this.valMatcher = val;
            this.unitKeyMatcher = unitKey;
            this.unitMatcher = unit;
        }

        @Override
        public boolean matchesSafely(final Measurement measurement) {

            System.out.println("MEASUREMENT =================" + measurement.getValue());
            System.out.println(valKeyMatcher.matches(measurement.getValueKey()));
            System.out.println(valMatcher.matches(measurement.getValue().toString()));
            System.out.println(unitKeyMatcher.matches(measurement.getUnitsKey()));
            System.out.println(unitMatcher.matches(measurement.getUnits()));
            System.out.println("END MEASUREMENT ================= ");

            return valKeyMatcher.matches(measurement.getValueKey())
                    && valMatcher.matches(measurement.getValue().toString())
                    && unitKeyMatcher.matches(measurement.getUnitsKey())
                    && unitMatcher.matches(measurement.getUnits());
        }

        @Override
        public void describeTo(final Description description) {

        }

        public static class NotEmpty extends MeasurementMatcher {

            public NotEmpty() {
                super(Matchers.notNullValue(String.class), Matchers.notNullValue(String.class), Matchers.notNullValue(String.class), Matchers.notNullValue(String.class));
            }
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

    private static class TestableMetricEventListener implements EventListener {

        private final Set<EventFilter> filters = new HashSet<>();
        private final LinkedList<MetricEvent> events = new LinkedList<>();

        @Override
        public void acceptEvent(final MetricEvent event) {
            events.add(event);
        }

        @Override
        public Collection<EventFilter> getFilters() {
            return filters;
        }

        public void addFilter(final EventFilter filter) {
            this.filters.add(filter);
        }

        public LinkedList<MetricEvent> history() {
            return events;
        }

    }

}
