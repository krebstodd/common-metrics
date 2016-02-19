package com.blispay.common.metrics;

import com.blispay.common.metrics.metric.BpTimer;
import com.blispay.common.metrics.util.StopWatch;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public class InstrumentedConnectionFactory extends ContainerLifeCycle implements ConnectionFactory {

    private final ConnectionFactory connectionFactory;
    private final BpTimer timer;

    /**
     * Create a new instrumented connection factory.
     * @param connectionFactory Connection factory to wrap.
     * @param timer Timer to collect connection metrics.
     */
    public InstrumentedConnectionFactory(final ConnectionFactory connectionFactory, final BpTimer timer) {
        this.connectionFactory = connectionFactory;
        this.timer = timer;
        this.addBean(connectionFactory);
    }

    public String getProtocol() {
        return this.connectionFactory.getProtocol();
    }

    /**
     * Get a new connection wrapped to collect metrics.
     *
     * @param connector Connector to create connection from.
     * @param endPoint Endpoint to connect to.
     * @return An instrumented jetty connection.
     */
    public Connection newConnection(final Connector connector, final EndPoint endPoint) {

        final Connection connection = this.connectionFactory.newConnection(connector, endPoint);
        connection.addListener(new Connection.Listener() {
            private StopWatch stopWatch;

            public void onOpened(final Connection connection) {
                this.stopWatch = InstrumentedConnectionFactory.this.timer.time();
            }

            public void onClosed(final Connection connection) {
                this.stopWatch.stop();
            }
        });

        return connection;
    }
}