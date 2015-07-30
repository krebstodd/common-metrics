package com.blispay.common.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jetty9.InstrumentedConnectionFactory;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.AsyncContextState;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class JettyMonitor extends ApplicationMonitor {

    private JettyMonitor() {}

    public static ConnectionFactory instrumentConnectionFactory(final ConnectionFactory plain) {
        final Timer connectionTimer = registry.timer(MetricRegistry.name(JETTY, CONNECTION));
        return new InstrumentedConnectionFactory(plain, connectionTimer);
    }

    /**
     * Create thread pool related JMX metrics for the provided thread pool.
     *
     * @param pool Queued thread pool to profile.
     */
    public static void instrumentThreadPool(final QueuedThreadPool pool) {

        registry.register(MetricRegistry.name(QueuedThreadPool.class, pool.getName(), "utilization"), new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getThreads());
            }
        });

        registry.register(MetricRegistry.name(QueuedThreadPool.class, pool.getName(), "utilization-max"), new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getMaxThreads());
            }
        });

        registry.register(MetricRegistry.name(QueuedThreadPool.class, pool.getName(), "size"), (Gauge<Integer>) pool::getThreads);

        registry.register(MetricRegistry.name(QueuedThreadPool.class, pool.getName(), "jobs"), (Gauge<Integer>) pool::getQueueSize);

    }

    // TODO: Make sure this is thread safe.
    public static synchronized Server getInstrumentedServer(final ThreadPool threadPool, final Consumer<HttpChannel<?>> channelHandler) {
        return new InstrumentedJettyServer(threadPool, channelHandler, registry);
    }


    private static class InstrumentedJettyServer extends Server {

        private final Consumer<HttpChannel<?>> handler;

        private final MetricRegistry metricRegistry;

        private final String prefix = JETTY;
        private Timer requests;
        private Timer dispatches;
        private Counter activeRequests;
        private Counter activeDispatches;
        private Counter activeSuspended;
        private Meter asyncDispatches;
        private Meter asyncTimeouts;
        private Meter[] responses;
        private Timer getRequests;
        private Timer postRequests;
        private Timer headRequests;
        private Timer putRequests;
        private Timer deleteRequests;
        private Timer optionsRequests;
        private Timer traceRequests;
        private Timer connectRequests;
        private Timer moveRequests;
        private Timer otherRequests;
        private AsyncListener listener;

        public InstrumentedJettyServer(final ThreadPool threadPool, final Consumer<HttpChannel<?>> channelHandler, final MetricRegistry metricRegistry) {
            super(threadPool);
            this.handler = channelHandler;
            this.metricRegistry = metricRegistry;
        }

        @Override
        public void handle(final HttpChannel<?> channel) throws IOException, ServletException {

            activeDispatches.inc();

            final long start;
            final HttpChannelState state = channel.getState();

            if (state.isInitial()) {
                activeRequests.inc();
                start = channel.getRequest().getTimeStamp();
            } else {
                start = Instant.now().toEpochMilli();
                activeSuspended.dec();
                if (state.getState() == HttpChannelState.State.DISPATCHED) {
                    asyncDispatches.mark();
                }
            }

            // Pass the channel down to our application.
            try {
                handler.accept(channel);
            } finally {
                final long now = System.currentTimeMillis();
                final long dispatched = now - start;

                activeDispatches.dec();
                dispatches.update(dispatched, TimeUnit.MILLISECONDS);

                if (state.isSuspended()) {
                    if (state.isInitial()) {
                        state.addListener(listener);
                    }
                    activeSuspended.inc();
                } else if (state.isInitial()) {
                    updateResponses(channel.getRequest(), channel.getResponse(), start);
                }
            }
        }

        @Override
        public void handleAsync(final HttpChannel<?> channel) throws IOException, ServletException {
            handler.accept(channel);
        }

        //CHECK_OFF: MagicNumber
        @Override
        public void doStart() throws Exception {
            super.doStart();

            this.requests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"requests"}));
            this.dispatches = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"dispatches"}));
            this.activeRequests = this.metricRegistry.counter(MetricRegistry.name(prefix, new String[]{"active-requests"}));
            this.activeDispatches = this.metricRegistry.counter(MetricRegistry.name(prefix, new String[]{"active-dispatches"}));
            this.activeSuspended = this.metricRegistry.counter(MetricRegistry.name(prefix, new String[]{"active-suspended"}));
            this.asyncDispatches = this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"async-dispatches"}));
            this.asyncTimeouts = this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"async-timeouts"}));

            this.responses = new Meter[] {this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"1xx-responses"})),
                    this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"2xx-responses"})),
                    this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"3xx-responses"})),
                    this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"4xx-responses"})),
                    this.metricRegistry.meter(MetricRegistry.name(prefix, new String[]{"5xx-responses"}))};

            this.getRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"get-requests"}));
            this.postRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"post-requests"}));
            this.headRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"head-requests"}));
            this.putRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"put-requests"}));
            this.deleteRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"delete-requests"}));
            this.optionsRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"options-requests"}));
            this.traceRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"trace-requests"}));
            this.connectRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"connect-requests"}));
            this.moveRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"move-requests"}));
            this.otherRequests = this.metricRegistry.timer(MetricRegistry.name(prefix, new String[]{"other-requests"}));
            this.metricRegistry.register(MetricRegistry.name(prefix, new String[]{"percent-4xx-1m"}), new RatioGauge() {
                protected Ratio getRatio() {
                    return Ratio.of(InstrumentedJettyServer.this.responses[3].getOneMinuteRate(), InstrumentedJettyServer.this.requests.getOneMinuteRate());
                }
            });

            // These calls are not create if not extist, they're just create so we want to make sure that they aren't
            // already configured.
            if (jettyServerConfigured.compareAndSet(false, true)) {
                this.metricRegistry.register(MetricRegistry.name(prefix, new String[]{"percent-4xx-5m"}), new RatioGauge() {
                    protected Ratio getRatio() {
                        return Ratio.of(InstrumentedJettyServer.this.responses[3].getFiveMinuteRate(), InstrumentedJettyServer.this.requests.getFiveMinuteRate());
                    }
                });
                this.metricRegistry.register(MetricRegistry.name(prefix, new String[]{"percent-4xx-15m"}), new RatioGauge() {
                    protected Ratio getRatio() {
                        return Ratio.of(InstrumentedJettyServer.this.responses[3].getFifteenMinuteRate(), InstrumentedJettyServer.this.requests.getFifteenMinuteRate());
                    }
                });
                this.metricRegistry.register(MetricRegistry.name(prefix, new String[]{"percent-5xx-1m"}), new RatioGauge() {
                    protected Ratio getRatio() {
                        return Ratio.of(InstrumentedJettyServer.this.responses[4].getOneMinuteRate(), InstrumentedJettyServer.this.requests.getOneMinuteRate());
                    }
                });
                this.metricRegistry.register(MetricRegistry.name(prefix, new String[]{"percent-5xx-5m"}), new RatioGauge() {
                    protected Ratio getRatio() {
                        return Ratio.of(InstrumentedJettyServer.this.responses[4].getFiveMinuteRate(), InstrumentedJettyServer.this.requests.getFiveMinuteRate());
                    }
                });
                this.metricRegistry.register(MetricRegistry.name(prefix, new String[]{"percent-5xx-15m"}), new RatioGauge() {
                    protected Ratio getRatio() {
                        return Ratio.of(InstrumentedJettyServer.this.responses[4].getFifteenMinuteRate(), InstrumentedJettyServer.this.requests.getFifteenMinuteRate());
                    }
                });
            }

            this.listener = new WrappedAsyncListener();
        }

        private Timer requestTimer(final String method) {
            final HttpMethod m = HttpMethod.fromString(method);
            if (m == null) {
                return otherRequests;
            } else {
                switch (m) {
                    case GET:
                        return getRequests;
                    case POST:
                        return postRequests;
                    case PUT:
                        return putRequests;
                    case HEAD:
                        return headRequests;
                    case DELETE:
                        return deleteRequests;
                    case OPTIONS:
                        return optionsRequests;
                    case TRACE:
                        return traceRequests;
                    case CONNECT:
                        return connectRequests;
                    case MOVE:
                        return moveRequests;
                    default:
                        return otherRequests;
                }
            }
        }

        private void updateResponses(final HttpServletRequest request, final HttpServletResponse response, final long start) {
            final int responseStatus = response.getStatus() / 100;
            if (responseStatus >= 1 && responseStatus <= 5) {
                this.responses[responseStatus - 1].mark();
            }

            this.activeRequests.dec();
            final long elapsedTime = System.currentTimeMillis() - start;
            this.requests.update(elapsedTime, TimeUnit.MILLISECONDS);
            this.requestTimer(request.getMethod()).update(elapsedTime, TimeUnit.MILLISECONDS);
        }

        private class WrappedAsyncListener implements AsyncListener {
            private long startTime;

            public void onTimeout(final AsyncEvent event) throws IOException {
                InstrumentedJettyServer.this.asyncTimeouts.mark();
            }

            public void onStartAsync(final AsyncEvent event) throws IOException {
                this.startTime = System.currentTimeMillis();
                event.getAsyncContext().addListener(this);
            }

            public void onError(final AsyncEvent event) throws IOException {
            }

            public void onComplete(final AsyncEvent event) throws IOException {
                final AsyncContextState state = (AsyncContextState) event.getAsyncContext();
                final HttpServletRequest request = (HttpServletRequest) state.getRequest();
                final HttpServletResponse response = (HttpServletResponse) state.getResponse();
                InstrumentedJettyServer.this.updateResponses(request, response, this.startTime);
                if (state.getHttpChannelState().getState() != HttpChannelState.State.DISPATCHED) {
                    InstrumentedJettyServer.this.activeSuspended.dec();
                }

            }
        }
    }

}
