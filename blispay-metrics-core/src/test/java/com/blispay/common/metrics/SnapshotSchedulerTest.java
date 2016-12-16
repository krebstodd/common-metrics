package com.blispay.common.metrics;

import com.blispay.common.metrics.report.SnapshotScheduler;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Class SnapshotSchedulerTest.
 */
public class SnapshotSchedulerTest {

    /**
     * Method testScheduleFixedRate.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testScheduleFixedRate() throws InterruptedException {

        final long snapshotLatency = 50L;
        final long snapshotRate = 100L;
        final int iterations = 5;

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedRate(Duration.ofMillis(snapshotRate));

        final List<Long> snapshotDelay = new LinkedList<>();
        scheduler.setListener(
            () -> {
                snapshotDelay.add(System.currentTimeMillis());

                try {
                    Thread.sleep(snapshotLatency);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });

        final long startTimeMillis = System.currentTimeMillis();
        scheduler.start();

        Thread.sleep(iterations * snapshotRate);

        scheduler.stop();

        for (int i = 0; i < snapshotDelay.size(); i++) {
            assertApproximatelyEqual((i + 1) * snapshotRate, snapshotDelay.get(i) - startTimeMillis, 200L);
        }
    }

    /**
     * Method testScheduleFixedRateWithCustomInitialDelay.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testScheduleFixedRateWithCustomInitialDelay() throws InterruptedException {

        final long snapshotLatency = 50L;
        final long snapshotRate = 100L;
        final long initialDelay = 200L;
        final int iterations = 5;

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedRateWithInitialDelay(Duration.ofMillis(snapshotRate), Duration.ofMillis(initialDelay));

        final List<Long> snapshotDelay = new LinkedList<>();
        scheduler.setListener(
            () -> {
                snapshotDelay.add(System.currentTimeMillis());

                try {
                    Thread.sleep(snapshotLatency);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });

        final long startTimeMillis = System.currentTimeMillis();
        scheduler.start();

        Thread.sleep(iterations * snapshotRate + initialDelay);

        scheduler.stop();

        for (int i = 0; i < snapshotDelay.size(); i++) {
            assertApproximatelyEqual(initialDelay + (i + 1) * snapshotRate, snapshotDelay.get(i) - startTimeMillis, 200L);
        }

    }

    /**
     * Method testScheduleFixedDelay.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testScheduleFixedDelay() throws InterruptedException {

        final long snapshotLatency = 50L;
        final long snapshotRate = 100L;
        final int iterations = 5;

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedDelay(Duration.ofMillis(snapshotRate));

        final List<Long> snapshotDelay = new LinkedList<>();
        scheduler.setListener(
            () -> {
                snapshotDelay.add(System.currentTimeMillis());

                try {
                    Thread.sleep(snapshotLatency);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });

        final long startTimeMillis = System.currentTimeMillis();
        scheduler.start();

        Thread.sleep(iterations * snapshotRate);

        scheduler.stop();

        final long initialDelay = snapshotRate;
        for (int i = 0; i < snapshotDelay.size(); i++) {
            final long expectedDelay = initialDelay + i * snapshotLatency + i * snapshotRate;
            assertApproximatelyEqual(expectedDelay, snapshotDelay.get(i) - startTimeMillis, 200L);
        }

    }

    /**
     * Method testScheduleFixedDelayWithCustomInitialDelay.
     *
     * @throws InterruptedException InterruptedException.
     */
    @Test
    public void testScheduleFixedDelayWithCustomInitialDelay() throws InterruptedException {

        final long snapshotLatency = 50L;
        final long snapshotRate = 100L;
        final long initialDelay = 200L;
        final int iterations = 5;

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedDelay(Duration.ofMillis(snapshotRate));

        final List<Long> snapshotDelay = new LinkedList<>();
        scheduler.setListener(
            () -> {
                snapshotDelay.add(System.currentTimeMillis());

                try {
                    Thread.sleep(snapshotLatency);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });

        final long startTimeMillis = System.currentTimeMillis();
        scheduler.start();

        Thread.sleep(iterations * snapshotRate);

        scheduler.stop();

        for (int i = 0; i < snapshotDelay.size(); i++) {
            final long expectedDelay = initialDelay + i * snapshotLatency + i * snapshotRate;
            assertApproximatelyEqual(expectedDelay, snapshotDelay.get(i) - startTimeMillis, 200L);
        }

    }

    /**
     * Method testNotificationListenerRequired.
     *
     */
    @Test(expected = IllegalStateException.class)
    public void testNotificationListenerRequired() {

        SnapshotScheduler.scheduleFixedRate(Duration.ofSeconds(10)).start();

    }

    /**
     * Method testCannotStartRunningScheduler.
     *
     */
    @Test
    public void testCannotStartRunningScheduler() {

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedRate(Duration.ofSeconds(10));

        scheduler.setListener(mock(SnapshotScheduler.NotificationListener.class));

        assertFalse(scheduler.isRunning());

        scheduler.start();

        assertTrue(scheduler.isRunning());

        try {
            scheduler.start();
            fail("Expected exception.");
        } catch (IllegalStateException ex) {

            assertTrue(scheduler.isRunning());

        }

    }

    /**
     * Method testStopShutsDownExecutorService.
     *
     * @throws IllegalAccessException IllegalAccessException.
     * @throws NoSuchFieldException NoSuchFieldException.
     */
    @Test
    public void testStopShutsDownExecutorService() throws NoSuchFieldException, IllegalAccessException {

        final SnapshotScheduler scheduler = SnapshotScheduler.scheduleFixedRate(Duration.ofSeconds(1));
        scheduler.setListener(mock(SnapshotScheduler.NotificationListener.class));

        final ScheduledExecutorService mockExecutor = mock(ScheduledExecutorService.class);

        final Field executorField = SnapshotScheduler.class.getDeclaredField("executorService");

        try {

            executorField.setAccessible(Boolean.TRUE);
            executorField.set(scheduler, mockExecutor);

            scheduler.start();

            assertTrue(scheduler.isRunning());

            verify(mockExecutor, never()).shutdown();
            verify(mockExecutor, never()).shutdownNow();

            scheduler.stop();

            verify(mockExecutor, times(1)).shutdown();
            verify(mockExecutor, never()).shutdownNow();

        } finally {

            if (executorField != null) {
                executorField.setAccessible(Boolean.FALSE);
            }

        }

    }

    private static void assertApproximatelyEqual(final Long expected, final Long actual, final Long acceptableDelta) {
        assertTrue(Math.abs(expected - actual) < acceptableDelta);
    }

}
