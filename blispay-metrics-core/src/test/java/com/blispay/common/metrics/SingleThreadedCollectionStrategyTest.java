package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.report.SingleThreadedCollectionStrategy;
import com.blispay.common.metrics.report.SnapshotProvider;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleThreadedCollectionStrategyTest {

    @Test
    public void testCollectsAllSequentially() {

        final Long provider1Delay = 250L;
        final EventModel e1 = new EventModel(null, null, null);

        final Long provider2Delay = 500L;
        final EventModel e2 = new EventModel(null, null, null);

        final SnapshotProvider provider1 = mock(SnapshotProvider.class);
        final SnapshotProvider provider2 = mock(SnapshotProvider.class);

        when(provider1.snapshot()).thenAnswer(invocation -> {
                try {
                    Thread.sleep(provider1Delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return e1;
            });

        when(provider2.snapshot()).thenAnswer(invocation -> {
                try {
                    Thread.sleep(provider2Delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return e2;
            });

        final SingleThreadedCollectionStrategy strategy = new SingleThreadedCollectionStrategy();

        final long start = System.currentTimeMillis();
        final Set<EventModel> results = strategy.performCollection(Arrays.asList(provider1, provider2));
        final long totalMillis = System.currentTimeMillis() - start;

        assertTrue(results.contains(e1));
        assertTrue(results.contains(e2));

        assertTrue(approximatelyEqual(provider1Delay + provider2Delay, totalMillis, 100L));

    }

    @Test
    public void testProvidesMaxDuration() {
        assertEquals(Duration.ofMillis(Integer.MAX_VALUE), new SingleThreadedCollectionStrategy().getTimeout());
    }

    protected Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
