/**
 *
 */
package mantisbtsync.core.jobs.projects;

import java.math.BigInteger;

import mantisbtsync.core.jobs.projects.processors.ProjectCategoryProcessor;
import mantisbtsync.core.jobs.projects.processors.ProjectCustomFieldProcessor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the processors used by the job of
 * Mantis projects syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class ProjectsProcessorsConfiguration {

	@Bean
	@StepScope
	public ProjectCategoryProcessor projectCategoriesProcessor(
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final ProjectCategoryProcessor proc = new ProjectCategoryProcessor();
		if (projectId != null) {
			proc.setProjectId(projectId.intValue());
		}

		return proc;
	}

	@Bean
	@StepScope
	public ProjectCustomFieldProcessor projectCustomFieldProcessor(
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final ProjectCustomFieldProcessor proc = new ProjectCustomFieldProcessor();
		if (projectId != null) {
			proc.setProjectId(projectId.intValue());
		}

		return proc;
	}
}
