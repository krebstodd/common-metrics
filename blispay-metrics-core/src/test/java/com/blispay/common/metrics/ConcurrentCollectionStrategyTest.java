package com.blispay.common.metrics;

import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.report.ConcurrentCollectionStrategy;
import com.blispay.common.metrics.report.SnapshotProvider;
import com.blispay.common.metrics.util.FastSnapshotProvider;
import com.blispay.common.metrics.util.SlowSnapshotProvider;
import org.junit.Test;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConcurrentCollectionStrategyTest {

    @Test
    public void testUsesFixedThreads() {

        final int threadCount = 5;
        final Duration providerLatency = Duration.ofSeconds(1);

        final ConcurrentCollectionStrategy strategy = new ConcurrentCollectionStrategy(threadCount);

        final Set<SnapshotProvider> mockProviders = new LinkedHashSet<>();

        for (int i = 0; i < threadCount * 2; i++) {
            mockProviders.add(new SlowSnapshotProvider(providerLatency, mockEvent()));
        }

        final long startedTimeMillis = System.currentTimeMillis();
        final Set<EventModel> results = strategy.performCollection(mockProviders);
        final long execTimeMillis = System.currentTimeMillis() - startedTimeMillis;

        assertEquals(mockProviders.size(), results.size());

        // AvgProviderDuration = 1sec, NumThreads = 5, NumProviders = NumThreads * 2 = 10
        // totalDuration = NumProviders * AvgProviderDuration / NumThreads ~ 2 seconds
        final Duration expectedTotalLatency = Duration.ofSeconds(2);
        assertTrue(approximatelyEqual(expectedTotalLatency.toMillis(), execTimeMillis, 100L));

    }

    @Test
    public void testCatchesAndLogsSingleProvidersException() {

        final int threadCount = 5;
        final Duration providerLatency = Duration.ofSeconds(1);

        final ConcurrentCollectionStrategy strategy = new ConcurrentCollectionStrategy(threadCount);

        final Set<SnapshotProvider> mockProviders = new LinkedHashSet<>();

        mockProviders.add(new SlowSnapshotProvider(providerLatency, mockEvent()));
        mockProviders.add(new SlowSnapshotProvider(providerLatency, mockEvent()));
        mockProviders.add(() -> {
                throw new RuntimeException("Some runtime exception...");
            });
        mockProviders.add(new SlowSnapshotProvider(providerLatency, mockEvent()));
        mockProviders.add(new SlowSnapshotProvider(providerLatency, mockEvent()));

        final long startedTimeMillis = System.currentTimeMillis();
        final Set<EventModel> results = strategy.performCollection(mockProviders);
        final long execTimeMillis = System.currentTimeMillis() - startedTimeMillis;

        assertEquals(4, results.size());

        final Duration expectedTotalLatency = Duration.ofSeconds(1);
        assertTrue(approximatelyEqual(expectedTotalLatency.toMillis(), execTimeMillis, 100L));

    }

    @Test
    public void testLogsTimeoutAndReturnsSuccessfulSnapshots() {

        final int threadCount = 5;
        final Duration providerLatency = Duration.ofMillis(5000);
        final Duration strategyTimeout = Duration.ofMillis(500);

        final ConcurrentCollectionStrategy strategy = new ConcurrentCollectionStrategy(threadCount, strategyTimeout);

        final Set<SnapshotProvider> mockProviders = new LinkedHashSet<>();

        mockProviders.add(new FastSnapshotProvider(mockEvent()));
        mockProviders.add(new SlowSnapshotProvider(providerLatency, mockEvent()));
        mockProviders.add(new FastSnapshotProvider(mockEvent()));

        final long startedTimeMillis = System.currentTimeMillis();
        final Set<EventModel> results = strategy.performCollection(mockProviders);
        final long execTimeMillis = System.currentTimeMillis() - startedTimeMillis;

        assertEquals(2, results.size());

        final Duration expectedTotalLatency = Duration.ofMillis(500);
        assertTrue(approximatelyEqual(expectedTotalLatency.toMillis(), execTimeMillis, 200L));

    }

    private EventModel<Void, Void> mockEvent() {
        return new EventModel<>(null, null);
    }

    protected Boolean approximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        return Math.abs(expected - actual) < acceptableDelta;
    }

}
