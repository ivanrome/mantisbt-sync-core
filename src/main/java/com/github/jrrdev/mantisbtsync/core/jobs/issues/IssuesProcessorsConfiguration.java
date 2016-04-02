/**
 *
 */
package com.github.jrrdev.mantisbtsync.core.jobs.issues;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugIdBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.processors.IssuesIdProcessor;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.processors.IssuesProcessor;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * @author jrrdev
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

	@Bean
	@StepScope
	public IssuesIdProcessor issuesIdProcessor(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		final IssuesIdProcessor issuesProcessor = new IssuesIdProcessor();
		issuesProcessor.setAuthManager(authManager);
		issuesProcessor.setClientStub(clientStub);
		issuesProcessor.setUserName(userName);
		issuesProcessor.setPassword(password);

		return issuesProcessor;
	}

	@Bean
	@StepScope
	public CompositeItemProcessor<BugIdBean, BugBean> compositeIssuesProcessor(final IssuesIdProcessor issuesIdProcessor,
			final IssuesProcessor issuesProcessor) {

		final CompositeItemProcessor<BugIdBean, BugBean> compositeIssuesProcessor = new CompositeItemProcessor<BugIdBean, BugBean>();

		final List<ItemProcessor<?, ?>> delegates = new ArrayList<ItemProcessor<?,?>>();
		delegates.add(issuesIdProcessor);
		delegates.add(issuesProcessor);
		compositeIssuesProcessor.setDelegates(delegates);

		return compositeIssuesProcessor;
	}
}
