/**
 *
 */
package mantisbtsync.core.jobs.issues.tasklets;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
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

		final JobExecution jobExec = jobRepository.getLastJobExecution(jobName, jobParams);

		if (jobExec != null) {
			stepContext.getStepExecution().getExecutionContext()
			.put("mantis.update.last_job_run", jobExec.getStartTime());
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

}
