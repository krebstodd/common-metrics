package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import jlibs.core.lang.RuntimeUtil;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Class GarbageCollectionMetricsTest.
 */
public class GarbageCollectionMetricsTest {

    /**
     * Method testGarbageCollectionEventsPublished.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testGarbageCollectionEventsPublished() throws InterruptedException {

        final MetricService serv = new MetricService(MetricTestUtil.randomAppId());
        serv.start();

        final TestEventSubscriber subscriber = new TestEventSubscriber();
        serv.addEventSubscriber(subscriber);

        JvmProbe.start(serv);

        RuntimeUtil.gc();
        Thread.sleep(1000);

        assertEquals(2, subscriber.count());

        final EventMatcher<Void, GcEventData> m1 = EventMatcher.<Void, GcEventData>builder()
                                                               .setApplication(serv.getApplicationId())
                                                               .setGroup(EventGroup.RESOURCE_UTILIZATION_GC)
                                                               .setName("gc")
                                                               .setType(EventType.EVENT)
                                                               .setUserDataMatcher(new GcEventDataMatcher())
                                                               .build();

        assertThat((EventModel<Void, GcEventData>) subscriber.poll(), m1);
    }

    /**
     * Class GcEventDataMatcher.
     */
    private static class GcEventDataMatcher extends TypeSafeMatcher<GcEventData> {

        @Override
        public boolean matchesSafely(final GcEventData data) {

            return data.getAction() != null
                   && data.getName() != null
                   && data.getDuration() != null
                   && data.getStartTime() != null
                   && data.getEndTime() != null
                   && !data.prePostFreeMemory().isEmpty();
        }

        @Override
        public void describeTo(final Description description) {}

    }

}
