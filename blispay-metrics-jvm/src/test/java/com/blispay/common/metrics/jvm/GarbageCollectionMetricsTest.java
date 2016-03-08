package com.blispay.common.metrics.jvm;

import com.blispay.common.metrics.MetricService;
import com.blispay.common.metrics.MetricTestUtil;
import com.blispay.common.metrics.TestEventSubscriber;
import com.blispay.common.metrics.data.SecureObjectMapper;
import com.blispay.common.metrics.matchers.EventMatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.model.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import jlibs.core.lang.RuntimeUtil;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
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

        JvmProbe.start(serv);

        RuntimeUtil.gc();
        Thread.sleep(1000);

        assertEquals(2, subscriber.count());

        assertThat((EventModel<GcEventData>) subscriber.poll(),
                new EventMatcher<>(serv.getApplicationId(), EventGroup.RESOURCE_UTILIZATION_GC, "gc", EventType.INFRA_EVT, new GcEventDataMatcher()));
    }

    private static class GcEventDataMatcher extends TypeSafeMatcher<GcEventData> {

        @Override
        public boolean matchesSafely(final GcEventData data) {

            try {
                System.out.println(new SecureObjectMapper().writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return data.getAction() != null
                    && data.getName() != null
                    && data.getDuration() != null
                    && data.getStartTime() != null
                    && data.getEndTime() != null
                    && !data.prePostFreeMemory().isEmpty();
        }


        @Override
        public void describeTo(final Description description) {

        }
    }
}
