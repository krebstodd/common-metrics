package com.blispay.common.metrics.legacy;

public class ScheduledSnapshotTest {

//    @Test
//    public void testScheduledSlf4jSnapshot() {
//        fail();
//
//    }
//
//    @Test
//    public void testGaugeLoggerLevel() throws InterruptedException {
//        final TestSnapshotReporter reporter = new TestSnapshotReporter();
//        final BpMetricService service = new BpMetricService();
//        service.addSnapshotReporter(reporter);
//
//        final MetricName gaugeName = new BusinessMetricName("application", "created");
//
//        final AtomicInteger val = new AtomicInteger(0);
//        final BpGauge<Integer> gauge = service.createGauge(gaugeName, MetricClass.businessEvent(), Measurement.Units.TOTAL, val::get);
//
//        gauge.setEventRecordLevelFn(currVal -> {
//                if (currVal < 10) {
//                    return MetricEvent.Level.INFO;
//                } else {
//                    return MetricEvent.Level.ERROR;
//                }
//            });
//
//        final Set<MetricEvent> snapshot = reporter.report();
//        assertEquals(1, snapshot.size());
//
//        final MetricEvent evt1 = snapshot.iterator().next();
//        assertEquals(MetricEvent.Level.INFO, evt1.getLevel());
//    }
//
//    private static class TestSnapshotReporter extends SnapshotReporter {
//
//        private Supplier<Set<SnapshotProvider>> snapshotSupplier;
//
//        @Override
//        public Set<MetricEvent> report() {
//            return snapshotSupplier.get().stream().map(SnapshotProvider::snapshot).collect(Collectors.toSet());
//        }
//
//        @Override
//        public void setSnapshotProviders(final Supplier<Set<SnapshotProvider>> providers) {
//            this.snapshotSupplier = providers;
//        }
//
//    }
}
