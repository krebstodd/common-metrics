package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.model.MetricGroup;
import com.blispay.common.metrics.model.MetricType;
import com.blispay.common.metrics.model.business.EventMetric;
import jlibs.core.lang.RuntimeUtil;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GarbageCollectionMetricsTest {

    @Test
    public void testGarbageCollectionEventsPublished() throws InterruptedException {

        final MetricService serv = new MetricService(MetricTestUtil.randomAppId());
        serv.start();

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        serv.addEventSubscriber(subscriber);

        final JvmProbe probe = new JvmProbe(serv);
        serv.addProbe(probe);

        RuntimeUtil.gc();
        Thread.sleep(500);
        assertEquals(2, subscriber.count());

        assertThat((EventMetric<GcEventData>) subscriber.poll(),
                new GcEventMatcher(MetricGroup.RESOURCE_UTILIZATION_GC, "gc", MetricType.EVENT));
    }

    @Test
    public void testStopUnregistersGcListeners() {

    }

    private static class GcEventMatcher extends EventMatcher<GcEventData> {

        public GcEventMatcher(final MetricGroup group, final String name, final MetricType type) {

            super(group, name, type, Matchers.notNullValue(GcEventData.class));
        }

        @Override
        public boolean matchesSafely(final EventMetric<GcEventData> metric) {
            final GcEventData data = metric.eventData();
            
            return super.matchesSafely(metric)
                    && data.getAction() != null
                    && data.getName() != null
                    && data.getDuration() != null
                    && data.getStartTime() != null
                    && data.getEndTime() != null
                    && data.getPreGcNewGen() != null
                    && data.getPostGcNewGen() != null
                    && data.getPreGcSurvivor() != null
                    && data.getPostGcSurvivor() != null
                    && data.getPreGcOldGen() != null
                    && data.getPostGcOldGen() != null;
        }


    }
}
