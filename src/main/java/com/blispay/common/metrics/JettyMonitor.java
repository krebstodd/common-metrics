package com.blispay.common.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jetty9.InstrumentedConnectionFactory;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import static com.codahale.metrics.MetricRegistry.name;

public final class JettyMonitor extends ApplicationMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(JettyMonitor.class);

    private static Optional<InstrumentedHandler> instrumentedHandler = Optional.empty();

    public static ConnectionFactory instrumentConnectionFactory(final ConnectionFactory plain) {
        final Timer connectionTimer = registry.timer(name(JETTY, CONNECTION));
        return new InstrumentedConnectionFactory(plain, connectionTimer);
    }

    public static void instrumentThreadPool(final QueuedThreadPool pool) {

        registry.register(name(QueuedThreadPool.class, pool.getName(), "utilization"), new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getThreads());
            }
        });

        registry.register(name(QueuedThreadPool.class, pool.getName(), "utilization-max"), new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(pool.getThreads() - pool.getIdleThreads(), pool.getMaxThreads());
            }
        });

        registry.register(name(QueuedThreadPool.class, pool.getName(), "size"), (Gauge<Integer>) pool::getThreads);

        registry.register(name(QueuedThreadPool.class, pool.getName(), "jobs"), (Gauge<Integer>) pool::getQueueSize);

    }

    // TODO: Make this thread safe.
    public static synchronized Server getInstrumentedServer(final ThreadPool threadPool, final Consumer<HttpChannel<?>> channelHandler) {
        return new InstrumentedJettyServer(threadPool, channelHandler);
    }

    private static class InstrumentedJettyServer extends Server {

        private Consumer<HttpChannel<?>> handler;

        public InstrumentedJettyServer(ThreadPool threadPool, final Consumer<HttpChannel<?>> channelHandler) {
            super(threadPool);
            this.handler = channelHandler;
        }

        @Override
        public void handle(HttpChannel<?> channel) throws IOException, ServletException {
            // Do some setup stuff

            System.out.println(">>>>>>>>AAA");

            handler.accept(channel);

            System.out.println(">>>>>>>BBBB");
            System.out.println(channel.getResponse().getStatus());
        }

        @Override
        public void handleAsync(HttpChannel<?> channel) throws IOException, ServletException {
            handler.accept(channel);
        }

        @Override
        public void doStart() throws Exception {
            super.doStart();

            // Set up all of our metrics in here.
        }
    }

    private JettyMonitor() {}
}
