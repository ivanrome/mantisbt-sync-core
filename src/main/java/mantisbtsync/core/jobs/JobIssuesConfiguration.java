/**
 *
 */
package mantisbtsync.core.jobs;

import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.readers.IssuesReader;
import mantisbtsync.core.jobs.issues.tasklets.IssuesLastRunExtractorTasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the job of Mantis issues syncing.
 * Parameters for this job are :
 * 	- mantis.username
 *  - mantis.password
 *  - mantis.project_id
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class JobIssuesConfiguration {

	@Bean
	public Job syncEnumsJob(final JobBuilderFactory jobs, final Step issuesLastSuccessExtractorStep,
			final Step issuesSyncStep) {

		return jobs.get("syncIssuesJob")
				.incrementer(new RunIdIncrementer())
				.flow(issuesLastSuccessExtractorStep)
				.next(issuesSyncStep)
				.end()
				.build();
	}

	@Bean
	public Step issuesLastSuccessExtractorStep(final StepBuilderFactory stepBuilderFactory,
			final IssuesLastRunExtractorTasklet mantisLastRunExtractorTasklet,
			final StepExecutionListener mantisLastRunExtractorPromotionListener) {

		return stepBuilderFactory.get("issuesLastSuccessExtractorStep")
				.tasklet(mantisLastRunExtractorTasklet)
				.listener(mantisLastRunExtractorPromotionListener)
				.build();
	}

	@Bean
	public Step issuesSyncStep(final StepBuilderFactory stepBuilderFactory,
			final IssuesReader issuesReader,
			final CompositeItemWriter<BugBean> compositeIssuesWriter) {

		return stepBuilderFactory.get("issuesSyncStep")
				.<BugBean, BugBean> chunk(20)
				.reader(issuesReader)
				.writer(compositeIssuesWriter)
				.build();
	}
}
