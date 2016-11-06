# mantisbt-sync-core
## Introduction
Batch to synchronize a local database from a remote instance of Mantis Bug Tracker with Soap calls.
This batch is aimed to retrieve informations from a distant Mantis Bug Tracker when the database is not directly accessible. This informations are inserted in a local database and so can be used for further usages (calculating indicators, sync with another bug tracker...).

The batch is compatible with MantisBT 1.2.19 and higher (successfully tested with 1.2.19 and 1.3.0-rc.1).

It also supports authentication through an intranet portal before accessing the MantisBT instance.

## Dependencies
1. Java 7 or higher
2. Maven
3. Spring Boot 1.4.0
4. [Codecentric's spring-boot-starter-batch-web 1.3.7](https://github.com/codecentric/spring-boot-starter-batch-web)
5. mantis-axis-soap-client 1.2.19
6. Apache HTTP Client 4.5.1
7. JSoup 1.8.3
8. DbSetup 1.6.0
9. Flyway 3.2.1

Database :

* HSQLDB 2.3.3
* MySQL 5.7

## Getting started

**Installation in production :** see [Docker image](https://github.com/jrrdev/mantisbt-sync-docker)

For development :

1. Checkout the project
2. In application-dev.properties, change mantis.endpoint to match the targeted MantisBT

If authentication through an intranet portal is needed :

3. Add a XML file describing the requests which must be sent
4. In application-dev.properties, add the parameter mantis.auth.filepath to indicate the XML file path
5. Launch the application

In development, in-memory database (HSQLDB) is used.

### Spring properties

Properties used in this project are :

* spring.datasource.platform
* spring.datasource.url
* flyway.locations
* mantis.endpoint
* mantis.auth.filepath (optionnal)

## REST API

Jobs launching and monitoring are performed through REST calls thank to [Codecentric's spring-boot-starter-batch-web](https://github.com/codecentric/spring-boot-starter-batch-web).

### Starting jobs

`http://{host}:{port}/batch/operations/jobs/{jobName}` / POST

Optionally you may define job parameters via request param 'jobParameters'. If a JobParametersIncrementer is specified in the job, it is used to increment the parameters.
On success, it returns the JobExecution's id as a plain string.
On failure, it returns the message of the Exception as a plain string. There are different failure possibilities :

* HTTP response code 404 (NOT_FOUND): the job cannot be found, not deployed on this server.
* HTTP response code 409 (CONFLICT): the JobExecution already exists and is either running or not restartable.
* HTTP response code 422 (UNPROCESSABLE_ENTITY): the job parameters didn't pass the validator.
* HTTP response code 500 (INTERNAL\_SERVER\_ERROR): any other unexpected failure.

### Retrieving an JobExecution's ExitCode

`http://{host}:{port}/batch/operations/jobs/executions/{executionId}` / GET

On success, it returns the ExitCode of the JobExecution specified by the executionId as a plain string.
On failure, it returns the message of the Exception as a plain string. There are different failure possibilities:

* HTTP response code 404 (NOT_FOUND): the JobExecution cannot be found.
* HTTP response code 500 (INTERNAL\_SERVER\_ERROR): any other unexpected failure.

### Retrieving a log file for a specific JobExecution

`http://{host}:{port}/batch/operations/jobs/executions/{executionId}/log` / GET

On success, it returns the log file belonging to the run of the JobExecution specified by the executionId as a plain string.
On failure, it returns the message of the Exception as a plain string. There are different failure possibilities:

* HTTP response code 404 (NOT_FOUND): the log file cannot be found.
* HTTP response code 500 (INTERNAL\_SERVER\_ERROR): any other unexpected failure.

### Stopping jobs

`http://{host}:{port}/batch/operations/jobs/executions/{executionId}` / DELETE

On success, it returns true.
On failure, it returns the message of the Exception as a plain string. There are different failure possibilities:

* HTTP response code 404 (NOT_FOUND): the JobExecution cannot be found.
* HTTP response code 409 (CONFLICT): the JobExecution is not running.
* HTTP response code 500 (INTERNAL\_SERVER\_ERROR): any other unexpected failure.

### Retrieving the names of deployed jobs

`http://{host}:{port}/batch/monitoring/jobs` / GET

On success, it returns a JSON array of String containing the names of the deployed jobs.

### Retrieving the ids of JobExecutions running on this server

`http://{host}:{port}/batch/monitoring/jobs/runningexecutions` / GET

On success, it returns a JSON array containing the ids of the JobExecutions running on this server.

### Retrieving the ids of JobExecutions running on this server for a certain job name

`http://{host}:{port}/batch/monitoring/jobs/runningexecutions/{jobName}` / GET

On success, it returns a JSON array containing the ids of the JobExecutions running on this server belonging to the specified job.

### Retrieving the JobExecution

`http://{host}:{port}/batch/monitoring/jobs/executions/{executionId}` / GET

On success, it returns a JSON representation of the JobExecution specified by the id. This representation contains everything you need to know about that job, from job name and BatchStatus to the number of processed items and time used and so on.
If the JobExecution cannot be found, a HTTP response code 404 is returned.


## Jobs

### Syncing MantisBT enumerations

Enumerations are system values used to define status, priorities, severities...
This sync should be launch at least once at first use.
Job parameters (all mandatory) are :

* mantis.username : MantisBT user name. If anonymous access is used, should be an empty string.
* mantis.password : MantisBT password. If anonymous access is used, should be an empty string.

To launch the job with curl :

```Shell
curl --silent -X POST 'http://localhost:8080/batch/operations/jobs/syncEnumsJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd"; echo
```

Note : Don't worry if the job fails because of access denied by MantisBT server. In fact, this sync isn't mandatory because the issues sync jobs will insert those values if needed

### Syncing MantisBT projects

This job will sync all data related to projects :

* name
* hierarchy with sub-projects
* categories
* custom fields definition
* users
* versions

Note that syncing a project will also sync its sub-projects.
Job parameters (all mandatory) are :

* mantis.username : MantisBT user name. If anonymous access is used, should be an empty string.
* mantis.password : MantisBT password. If anonymous access is used, should be an empty string.
* mantis.project_id : The id of the project

To launch the job with curl :

```Shell
curl --silent -X POST 'http://localhost:8080/batch/operations/jobs/syncProjectsJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.project_id=1"; echo
```

### Syncing MantisBT issues

Several jobs are available for syncing issues :

* syncIssuesJob
* forceSyncIssuesJob
* fileSyncIssuesJob

#### Initial import

For initial import, the fileSyncIssuesJob job should be used.
This job reads a list of issues ids from a CSV file and retrieves all data related to those issues. The CSV file must not have a header line for columns definition. It can be exported from MantisBT web UI.

Job parameters (all mandatory) are :

* mantis.username : MantisBT user name. If anonymous access is used, should be an empty string.
* mantis.password : MantisBT password. If anonymous access is used, should be an empty string.
* mantis.filepath : File path of the CSV file

Note : the file is loaded through Spring resource loader so the file path can contains definitions like classpath: and others.

To launch the job with curl :

```Shell
curl --silent -X POST 'http://localhost:8080/batch/operations/jobs/fileSyncIssuesJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.filepath=file:/path/to/file/filename.csv"; echo
```

#### Periodic sync

For periodic sync (for instance via a cron job), the syncIssuesJob job should be used.
This job will update all modified issues which are still marked as open is MantisBT or in the local DB. The update is perform in differential mode since last successful execution of this job for the given project.
Note : all issues related to a subproject are updated to.

Job parameters (all mandatory) are :

* mantis.username : MantisBT user name. If anonymous access is used, should be an empty string.
* mantis.password : MantisBT password. If anonymous access is used, should be an empty string.
* mantis.project_id : The id of the project

To launch the job with curl :

```Shell
curl --silent -X POST 'http://localhost:8080/batch/operations/jobs/syncIssuesJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.project_id=1"; echo
```

#### Force sync

If needed, a forced sync mode is available through forceSyncIssuesJob job.
This job will update all issues for which the id is passed as job parameter, even if the issue is closed in MantisBT.

Job parameters (all mandatory) are :

* mantis.username : MantisBT user name. If anonymous access is used, should be an empty string.
* mantis.password : MantisBT password. If anonymous access is used, should be an empty string.
* mantis.issues_id : Semi-colon separated list of issues ids

To launch the job with curl :

```Shell
curl --silent -X POST 'http://localhost:8080/batch/operations/jobs/forceSyncIssuesJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.issues_id=1;2"; echo
```

#### Statistic computation

A job to compute the number of issues in a project by handler at a given time is available through handlersStatJob job.
It will add a line in the handler_stat table with :

* the date
* the project id
* the handler id
* the status id
* the number of issues

Note : if a user doesn't handle any issue, no line will be inserted.

Job parameters (all mandatory) are :

* mantis.computeDate : computation date time. Can be in the past. Must be formatted as "yyyy-MM-dd'T'HH:mm:ss"

To launch the job with curl :

```Shell
curl --silent -X POST 'http://localhost:8080/batch/operations/jobs/handlersStatJob' --data "jobParameters=mantis.computeDate=2016-11-06T23:27:11"; echo
```

## Roadmap

* Change portal authentication from HTTP Client to headless Selenium
* Better logging for easier debug

## Related projects

* [Docker image for MySQL and core batch](https://github.com/jrrdev/mantisbt-sync-docker) : done
* [Simple cron for periodic sync scheduling](https://github.com/jrrdev/mantisbt-sync-cron) : done
* Jobs to calculate issues statistics : done
* [REST API to perform issues monitoring](https://github.com/jrrdev/mantisbt-sync-REST) : done
* Web UI for batch administration and scheduling : _not started_
* Sync to JIRA : _not started_