/**
 *
 */
package mantisbtsync.core.jobs.issues.tasklets;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author jdevarulrajah
 *
 */
public class IssuesLastRunExtractorTasklet implements Tasklet {

	private JobRepository jobRepository;

	private JobExplorer jobExplorer;

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(final StepContribution contribution,
			final ChunkContext chunkContext) throws Exception {

		final StepContext stepContext = chunkContext.getStepContext();
		final String jobName = stepContext.getJobName();
		final JobParameters jobParams = stepContext.getStepExecution().getJobParameters();
		Date lastJobRun = null;

		final List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1000);
		for (final JobInstance jobInstance : jobInstances) {
			final List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
			for (final JobExecution jobExecution : jobExecutions) {
				if (jobExecution.getJobParameters().equals(jobParams)
						&& ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {

					if (lastJobRun == null || lastJobRun.before(jobExecution.getStartTime())) {
						lastJobRun = jobExecution.getStartTime();
					}
				}
			}
		}

		if (lastJobRun != null) {
			stepContext.getStepExecution().getExecutionContext()
			.put("mantis.update.last_job_run", lastJobRun);
		}

		return RepeatStatus.FINISHED;
	}

	/**
	 * @return the jobRepository
	 */
	public JobRepository getJobRepository() {
		return jobRepository;
	}

	/**
	 * @param jobRepository the jobRepository to set
	 */
	public void setJobRepository(final JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	/**
	 * @return the jobExplorer
	 */
	public JobExplorer getJobExplorer() {
		return jobExplorer;
	}

	/**
	 * @param jobExplorer the jobExplorer to set
	 */
	public void setJobExplorer(final JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}

}
