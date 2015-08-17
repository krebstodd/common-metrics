package com.blispay.common.metrics.probe;

import com.blispay.common.metrics.BpTimer;
import com.codahale.metrics.jdbi.strategies.SmartNameStrategy;
import com.codahale.metrics.jdbi.strategies.StatementNameStrategy;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.TimingCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class JdbProbe extends BpMetricProbe implements TimingCollector {

    private static final Logger LOG = LoggerFactory.getLogger(JdbProbe.class);

    private static final StatementNameStrategy statementNameStrategy = new SmartNameStrategy();

    private static final String DESCRIPTION = "Jdb metrics for statement executing time in nanoseconds.";

    private ConcurrentHashMap<String, BpTimer> timers = new ConcurrentHashMap<>();

    public JdbProbe(final DataSource ds) {
        final DBI dbi = new DBI(ds);
        dbi.setTimingCollector(this);
    }

    @Override
    public void collect(final long executionTime, final StatementContext statementContext) {

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>DOING DBI>>>>>>>>>>>");
        BpTimer timer;
        final String timername = statementNameStrategy.getStatementName(statementContext);

        try {
            timer = metricService.createTimer(JdbProbe.class, timername, DESCRIPTION);
            timers.put(timername, timer);
        } catch (IllegalArgumentException ex) {
            timer = timers.get(timername);
        }

        if (timer == null) {
            LOG.warn("Unable to create or locate jdb timer {}", timername);
            return;
        }

        timer.update(executionTime, TimeUnit.NANOSECONDS);
    }


    @Override
    protected void startProbe() {}

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
