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

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugIdBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.processors.IssuesIdProcessor;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.processors.IssuesProcessor;

/**
 * Configuration for the processors used to sync issues.
 *
 * @author jrrdev
 *
 */
@Configuration
public class IssuesProcessorsConfiguration {

	/**
	 * Processor that transform the raw data retrieved from mc_issue_get
	 * to a bean usable for insertion in the DB.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the processor
	 */
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

	/**
	 * Processor used to retrieve the data related to the issue by calling
	 * mc_issue_get ws operation by using the given issue id.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the processor
	 */
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

	/**
	 * Composite item processor that chains an IssuesIdProcessor and
	 * an IssuesProcessor.
	 *
	 * @param issuesIdProcessor
	 * 			the IssuesIdProcessor
	 * @param issuesProcessor
	 * 			then IssuesProcessor
	 * @return the chained composite processor
	 */
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
