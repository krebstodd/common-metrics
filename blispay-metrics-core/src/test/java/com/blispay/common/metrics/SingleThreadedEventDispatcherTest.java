package com.blispay.common.metrics;

import com.blispay.common.metrics.event.EventDispatcher;
import com.blispay.common.metrics.event.EventEmitter;
import com.blispay.common.metrics.event.SingleThreadedEventDispatcher;
import com.blispay.common.metrics.model.EventGroup;
import com.blispay.common.metrics.model.EventModel;
import com.blispay.common.metrics.util.TestEventSubscriber;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class SingleThreadedEventDispatcherTest.
 */
public class SingleThreadedEventDispatcherTest extends AbstractMetricsTest {

    /**
     * Method testDispatchesEvents.
     *
     */
    @Test
    public void testDispatchesEvents() {

        final EventDispatcher ed = new SingleThreadedEventDispatcher();

        final EventEmitter emitter = ed.newEventEmitter();
        final TestEventSubscriber subscriber = new TestEventSubscriber();

        ed.subscribe(subscriber);

        ed.start();

        final EventModel<Void, PiiBusinessEventData> evt = testEvent();
        emitter.emit(evt);

        assertEquals(1, subscriber.count());
        assertEquals(evt, subscriber.poll());

    }

    /**
     * Method testEatsExceptions.
     *
     */
    @Test
    public void testEatsExceptions() {

        final EventDispatcher ed = new SingleThreadedEventDispatcher();

        final EventEmitter emitter = ed.newEventEmitter();
        final TestEventSubscriber subscriber = new TestEventSubscriber();
        subscriber.exceptionsOn(Boolean.TRUE);

        ed.subscribe(subscriber);
        ed.start();
        emitter.emit(testEvent());

        assertEquals(0, subscriber.count());

    }

    /**
     * Method testFiltersDispatching.
     *
     */
    @Test
    public void testFiltersDispatching() {

        final EventDispatcher ed = new SingleThreadedEventDispatcher();

        final EventEmitter emitter = ed.newEventEmitter();

        final TestEventSubscriber subscriber1 = new TestEventSubscriber();
        final TestEventSubscriber subscriber2 = new TestEventSubscriber();
        subscriber2.addFilter(event -> event.getHeader().getGroup() != EventGroup.ACCOUNT_DOMAIN);

        ed.subscribe(subscriber1);
        ed.subscribe(subscriber2);

        ed.start();

        final EventModel<Void, PiiBusinessEventData> evt = testEvent();
        emitter.emit(evt);

        assertEquals(1, subscriber1.count());
        assertEquals(evt, subscriber1.poll());

        assertEquals(0, subscriber2.count());

    }

    /**
     * Method testEatsFilterExceptions.
     *
     */
    @Test
    public void testEatsFilterExceptions() {

        final EventDispatcher ed = new SingleThreadedEventDispatcher();

        final EventEmitter emitter = ed.newEventEmitter();

        final TestEventSubscriber subscriber1 = new TestEventSubscriber();
        final TestEventSubscriber subscriber2 = new TestEventSubscriber();
        subscriber2.addFilter(
            event -> {
                throw new IllegalStateException("Bad filter");
            });

        ed.subscribe(subscriber1);
        ed.subscribe(subscriber2);

        ed.start();

        final EventModel<Void, PiiBusinessEventData> evt = testEvent();
        emitter.emit(evt);

        assertEquals(1, subscriber1.count());
        assertEquals(evt, subscriber1.poll());

        assertEquals(0, subscriber2.count());

    }

}
