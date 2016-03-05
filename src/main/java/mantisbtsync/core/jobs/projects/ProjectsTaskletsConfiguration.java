package mantisbtsync.core.jobs.projects;

import java.math.BigInteger;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.jobs.projects.tasklets.MantisLoginTasklet;
import mantisbtsync.core.jobs.projects.tasklets.ProjectsExtractorTasklet;
import mantisbtsync.core.jobs.projects.tasklets.ProjectsListTasklet;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * Configuration for the taskets used by the job of
 * Mantis projects syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class ProjectsTaskletsConfiguration {

	// tag::tasklet[]

	@Bean
	@StepScope
	public MantisLoginTasklet mantisLoginTasklet(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		final MantisLoginTasklet tasklet = new MantisLoginTasklet();
		tasklet.setAuthManager(authManager);
		tasklet.setClientStub(clientStub);
		tasklet.setUserName(userName);
		tasklet.setPassword(password);

		return tasklet;
	}

	@Bean
	@StepScope
	public ProjectsListTasklet mantisProjectsListTasklet(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub, final JdbcTemplate jdbcTemplate,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobParameters['mantis.project_id']}") final Long projectId) {

		final ProjectsListTasklet tasklet = new ProjectsListTasklet();
		tasklet.setAuthManager(authManager);
		tasklet.setClientStub(clientStub);
		tasklet.setUserName(userName);
		tasklet.setPassword(password);
		tasklet.setJdbcTemplate(jdbcTemplate);
		tasklet.setProjectId(BigInteger.valueOf(projectId));

		return tasklet;
	}

	@Bean
	@StepScope
	public ProjectsExtractorTasklet mantisProjectExtractorTasklet() {

		return new ProjectsExtractorTasklet();
	}

	// end::tasklet[]

	// tag::listener[]

	@Bean
	@StepScope
	public ExecutionContextPromotionListener mantisLoginPromotionListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"mantis.acess_level"});

		return listener;
	}

	@Bean
	@StepScope
	public ExecutionContextPromotionListener mantisProjectsListListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"mantis.loop.projects_to_process"});

		return listener;
	}

	@Bean
	@StepScope
	public ExecutionContextPromotionListener mantisProjectExtractorListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"mantis.loop.project_id"});

		return listener;
	}

	// end::listener[]
}
