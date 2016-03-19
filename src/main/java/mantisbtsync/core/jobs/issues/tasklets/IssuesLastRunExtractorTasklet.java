/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jérard Devarulrajah
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package mantisbtsync.core.jobs.issues.tasklets;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author jdevarulrajah
 *
 */
public class IssuesLastRunExtractorTasklet implements Tasklet {

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
		final Map<String, JobParameter> currParams = new HashMap<String, JobParameter>(jobParams.getParameters());
		currParams.remove("run.id");
		currParams.remove("job.completeSync");

		Date lastJobRun = null;

		final List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1000);
		for (final JobInstance jobInstance : jobInstances) {
			final List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
			for (final JobExecution jobExecution : jobExecutions) {

				final JobParameters oldJobParams = jobExecution.getJobParameters();
				final Map<String, JobParameter> oldParams = new HashMap<String, JobParameter>(oldJobParams.getParameters());
				oldParams.remove("run.id");
				oldParams.remove("job.completeSync");

				if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())
						&& oldParams.equals(currParams)) {

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

		stepContext.getStepExecution().getExecutionContext()
		.put("mantis.update.current_job_run", Calendar.getInstance());

		return RepeatStatus.FINISHED;
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
