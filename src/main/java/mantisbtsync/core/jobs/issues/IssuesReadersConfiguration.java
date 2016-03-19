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
package mantisbtsync.core.jobs.issues;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.jobs.issues.readers.NewIssuesReader;
import mantisbtsync.core.jobs.issues.readers.OpenIssuesReader;
import mantisbtsync.core.jobs.issues.readers.OtherIssuesReader;

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
	public OpenIssuesReader openIssuesReader(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobParameters['mantis.project_id']}") final BigInteger projectId,
			@Value("#{jobExecutionContext['mantis.update.last_job_run']}") final Date lastJobRun) {

		final OpenIssuesReader reader = new OpenIssuesReader();
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setLastJobRun(lastJobRun);
		reader.setPassword(password);
		reader.setProjectId(projectId);
		reader.setUserName(userName);

		return reader;
	}

	@Bean
	@StepScope
	public NewIssuesReader newIssuesReader(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobParameters['mantis.project_id']}") final BigInteger projectId) {

		final NewIssuesReader reader = new NewIssuesReader();
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setPassword(password);
		reader.setProjectId(projectId);
		reader.setUserName(userName);

		return reader;
	}

	@Bean
	@StepScope
	public OtherIssuesReader otherIssuesReader(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobParameters['mantis.project_id']}") final BigInteger projectId,
			@Value("#{jobExecutionContext['mantis.update.current_job_run']}") final Calendar jobRunTime) {

		final OtherIssuesReader reader = new OtherIssuesReader();
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setJobStartTime(jobRunTime);
		reader.setPassword(password);
		reader.setProjectId(projectId);
		reader.setUserName(userName);

		return reader;
	}
}
