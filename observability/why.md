## why we need observability
* Observability helps us understand the internal state of a system based on the data it produces, such as logs, metrics, and traces.
* It enables us to monitor system performance, detect anomalies, and troubleshoot issues effectively.
## why ci cd and microservices need observability
* In CI/CD pipelines, observability ensures that we can track the deployment process, identify bottlenecks, and ensure that new code changes do not introduce regressions.

## what is monitoring
* Monitoring is the process of collecting, analyzing, and using data to track the performance and health of a system.
### what metric we need to collect for monitoring
* UI layer
* service layer
* insfrastructure layer
* some methods that are common :
    * RED method (Rate, Errors, Duration) for services
    * 4 golden signals (latency, traffic, errors, saturation) for overall system
    * USE method (Utilization, Saturation (network queue length), Errors (disk errors)) for infrastructure
  
* monmitorinf is part of observability but not equal to observability
* monitoring alone is deciding what u want to keep an eye on before collecting data. Better suited for monolithic applications with well-defined metrics.
* primary tell u when an issue orrcued
  
* observability is more about why the issue occurred by providing more context and insights into the system's behavior.
* better suited for complex, distributed systems like microservices architectures.

## method of metric collection
* push vs scrape
* push: app and services send the metrics to an endpoint via TCP (aka ), UPD or HTTP
* example. an app send metrics to statsD , to be store on Graphite

* scrape: a monitoring system periodically pulls metrics from the app or services via HTTP, the app and services need to have and api for the time series metric endpoint
* example: Prometheus scrapes metrics from instrumented applications


## types of telemetry data
* melt: metric , events, logs, traces
* metric: numerical data points that represent the performance or behavior of a system over time. They are typically collected at regular intervals and can be aggregated to provide insights into system health and performance.
* events: 
* logs: a very detailed, timestamped record of discrete events that occur within a system. They provide context and information about specific actions, errors, or state changes.
* traces: show the interactions between different components or services in a distributed system to fullfill a request. They help visualize the flow of requests and identify bottlenecks or failures in the system.
## data model of prometheus
* stores  data in time series format
* every time series is identified by a metric name and a set of key-value pairs called labels.
* labbels are optional
* example : http_requests_total{method="POST", handler="/api/v1/users"}
* metric name: http_requests_total
## what is prometheus
* open-source monitoring and alerting toolkit designed for reliability and scalability.
* collects and stores metrics as time series data, allowing users to query and analyze the data using a powerful query language called PromQL.
## data types in prometheus
* scalar: single numerical value (float and string)
* prometheus_http_requests_total{code="200", job="api-server" }
* query : prometheus_http_requests_total{code="200.*", job="api-server" } => return all time series with code starting with 200