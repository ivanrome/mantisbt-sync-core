/**
 *
 */
package mantisbtsync.core.jobs.issues;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.jobs.issues.processors.IssuesProcessor;

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
public class IssuesProcessorsConfiguration {

	@Bean
	@StepScope
	public IssuesProcessor issuesProcessor(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		final IssuesProcessor issuesProcessor = new IssuesProcessor();
		issuesProcessor.setAuthManager(authManager);
		issuesProcessor.setClientStub(clientStub);
		issuesProcessor.setUserName(userName);
		issuesProcessor.setPassword(password);

		return issuesProcessor;
	}
}
