/**
 *
 */
package mantisbtsync.core.jobs.issues;

import java.math.BigInteger;
import java.util.Date;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.jobs.issues.readers.IssuesReader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * @author jdevarulrajah
 *
 */
@Configuration
public class IssuesReadersConfiguration {

	@Bean
	@StepScope
	public IssuesReader issuesReader(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobParameters['mantis.project_id']}") final BigInteger projectId,
			@Value("#{jobExecutionContext['mantis.update.last_job_run']}") final Date lastJobRun) {

		final IssuesReader reader = new IssuesReader();
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setLastJobRun(lastJobRun);
		reader.setPassword(password);
		reader.setProjectId(projectId);
		reader.setUserName(userName);

		return reader;
	}
}
