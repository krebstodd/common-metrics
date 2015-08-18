# Java Application Metrics

Collect metrics on your Java applications. Based on [Dropwizard Metrics](https://dropwizard.github.io/metrics/3.1.0/)

## Core Library

### BpMetricService

The metric service will allow you to create and use new metrics. The service will provide hooks to start up and shut down any reporters configured for the active registry.

This service acts as a singleton to prevent users from creating multiple registries in their application. All metrics created app-wide will be reported in the same place for convenience.

*Note: The metics reporter service is started upon the first instantiation of the metrics service so all reporter system properties must be configured at this time. See [BpMetricReportingService](#BpMetricReportingService) for more information*

#### Instantiation

```Java
public class MyClass {
    private static final BpMetricService metricService = BpMetricService.getInstance();
}
```

### BpGauge

Gauges are the simplest metric type. Gauges allow users to implement custom behavior to be sampled by periodic or asynchronous events.

```Java
final BpMetricService metricService = BpMetricService.getInstance();

final Integer myVar = 1;

final BpGauge timer = metricService.createTimer(MyClass.class, "current-myvar-value", "Time how long my custom event takes to complete.", () -> myVar);
```

### BpCounter

A counter is just a gauge for an AtomicLong instance. You can increment or decrement its value. For example, we may want a more efficient way of measuring the pending job in a queue:

```Java
final BpMetricService metricService = BpMetricService.getInstance();
final BpCounter pendingJobs = metricService.createCounter(MyQueue.class, "pending-jobs", "Jobs currently in queue.");

public void addJob(Job job) {
    pendingJobs.increment();
    queue.offer(job);
}

public Job takeJob() {
    pendingJobs.decrement();
    return queue.take();
}
```

### BpMeter

A meter measures the rate of events over time (e.g., “requests per second”). In addition to the mean rate, meters also track 1-, 5-, and 15-minute moving averages.

```Java
final BpMetricService metricService = BpMetricService.getInstance();
private final BpMeter requests = metricService.createMeter(Handler.class, "requests", "Rate of incoming requests.");

public void handleRequest(Request request, Response response) {
    requests.mark();
}
```

### BpTimer

A timer measures both the rate that a particular piece of code is called and the distribution of its duration.

```Java
final BpMetricService metricService = BpMetricService.getInstance();
final BpTimer timer = metricService.createTimer(MyClass.class, "sleep-timer", "Time how long my app spends sleeping.");

final BpTimer.Resolver resovler1 = timer.time();
Thread.sleep(1000);
resovler1.done();

final BpTimer.Resolver resovler2 = timer.time();
Thread.sleep(2000);
resovler2.done();
```

### BpHistogram

A histogram measures the statistical distribution of values in a stream of data. In addition to minimum, maximum, mean, etc., it also measures median, 75th, 90th, 95th, 98th, 99th, and 99.9th percentiles.

```Java
private final BpMetricService metricService = BpMetricService.getInstance();
private final BpHistogram responseSizes = metricService.createHistogram(RequestHandler.class, "response-size", "Size of responses from this request handler.");

public void handleRequest(Request request, Response response) {
    responseSizes.update(response.getContent().length);
}
```

### BpMetricReportingService

BpMetricService will allow users to select a pre-existing reporting mechanism or implement their own via a
[Custom Consumer](####Custom Consumer)

#### Jmx reporter

To instantiate Jmx reporting, set the following system properties:

```Java
System.setProperty("metrics.jmx.enabled", "true");
```

#### Slf4j reporter

To instantiate Slf4j reporting, set the following system properties:

```Java
System.setProperty("metrics.slf4j.enabled", "true");
System.setProperty("metrics.slf4j.logger", "METRICS-LOGGER"); // Slf4j logger name to use (info level)
System.setProperty("metrics.slf4j.period", "60");
System.setProperty("metrics.slf4j.unit", "SECONDS");
```

This reporter will dump a full snapshot of all currently active metrics to the provided appender at
the provided rate.

#### Custom consumer

By implementing a custom consumer (BpMetricConsumer) you can get access to all currently active metrics and be notified when new metrics are created/removed elsewhere in your application.

```Java
metricService.addConsumer(new BpMetricConsumer() {
    @Override
    public void registerMetric(final BpMetric metric) {
        // A new metric has been registered. When you first provide your consumer,
        // all currently active metrics will be added.
    }

    @Override
    public void unregisterMetric(final String metric) {
        // The metric with name {metric} has been removed from the service and
        // is no longer considered active.
    }

    @Override
    public void start() {
        // Start hook for metric service to call when started.
    }

    @Override
    public void stop() {
        // Stop hook for metric service to call when it's killed.
    }
});
```

## Probes

Probes are collections of metrics wrapped around various third party library objects.

Custom probes can be added to the service by implementing BpMetricProbe.java and adding it to
an existing metric service.
