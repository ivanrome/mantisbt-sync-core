# mantisbt-sync-core
## Introduction
Batch to synchronize a local database from a remote instance of Mantis Bug Tracker with Soap calls.
This batch is aimed to retrieve informations from a distant Mantis Bug Tracker when the database is not directly accessible. This informations are inserted in a local database and so can be used for further usages (calculating indicators, sync with another bug tracker...).

The batch is compatible with MantisBT 1.2.19 and higher (successfully tested with 1.2.19 and 1.3.0-rc.1).

It also supports authentication through an intranet portal before accessing the MantisBT instance.

## Dependencies
1. Java 7 or higher
2. Maven
3. Spring Boot 1.3.2
4. [Codecentric's spring-boot-starter-batch-web 1.3.7](https://github.com/codecentric/spring-boot-starter-batch-web)
5. mantis-axis-soap-client 1.2.19
6. Apache HTTP Client 4.5.1
7. JSoup 1.8.3
8. DbSetup 1.6.0

Database :

* HSQLDB 2.3.3
* MySQL 5.7

## Getting started

_Installation in production : docker image coming soon..._

For development :

1. Checkout the project
2. In application-dev.properties, change mantis.endpoint to match the targeted MantisBT

If authentication through an intranet portal is needed :

3. Add a XML file describing the requests which must be sent
4. In application-dev.properties, add the parameter mantis.auth.filepath to indicate the XML file path
5. Launch the application

In development, in-memory database (HSQLDB) is used.

## Jobs

Jobs can be launch through REST calls thank to [Codecentric's spring-boot-starter-batch-web](https://github.com/codecentric/spring-boot-starter-batch-web).

### Syncing MantisBT enumerations

Enumerations are system values used to define status, priorities, severities...
This sync should be launch at least once at first use.
Job parameters (all mandatory) are :

* mantis.username : MantisBT user name. If anonymous access is used, should be an empty string.
* mantis.password : MantisBT password. If anonymous access is used, should be an empty string.

To launch the job with curl :

```Shell
curl -X POST 'http://localhost:8080/batch/operations/jobs/syncEnumsJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd"
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
curl -X POST 'http://localhost:8080/batch/operations/jobs/syncProjectsJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.project_id=1"
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
curl -X POST 'http://localhost:8080/batch/operations/jobs/fileSyncIssuesJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.filepath=file:/path/to/file/filename.csv"
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
curl -X POST 'http://localhost:8080/batch/operations/jobs/syncIssuesJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.project_id=1"
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
curl -X POST 'http://localhost:8080/batch/operations/jobs/forceSyncIssuesJob' --data "jobParameters=mantis.username=foo,mantis.password=foopasswd,mantis.issues_id=1;2"
```

## Roadmap

* Build a docker image for installation in production
* Use flyway to manage database migration
* Change portal authentication from HTTP Client to headless Selenium
* Better logging for easier debug

## Related projects

* Docker image for MySQL and core batch : _work in progress_
* Simple cron for periodic sync scheduling : _work in progress_
* Batch to calculate indicators : _not started_
* Web UI for batch administration and scheduling : _not started_
* Sync to JIRA : _not started_