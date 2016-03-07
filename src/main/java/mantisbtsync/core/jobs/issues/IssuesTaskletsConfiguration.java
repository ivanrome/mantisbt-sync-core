/**
 *
 */
package mantisbtsync.core.jobs.issues;

import mantisbtsync.core.jobs.issues.tasklets.IssuesLastRunExtractorTasklet;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jdevarulrajah
 *
 */
@Configuration
public class IssuesTaskletsConfiguration {

	@Bean
	@StepScope
	public IssuesLastRunExtractorTasklet mantisLastRunExtractorTasklet(final JobExplorer jobExplorer) {
		final IssuesLastRunExtractorTasklet tasklet = new IssuesLastRunExtractorTasklet();
		tasklet.setJobExplorer(jobExplorer);
		return tasklet;
	}

	@Bean
	@StepScope
	public ExecutionContextPromotionListener mantisLastRunExtractorPromotionListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"mantis.update.last_job_run"});
		return listener;
	}

}
