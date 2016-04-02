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
package com.github.jrrdev.mantisbtsync.core.jobs.issues;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugIdBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.readers.OpenIssuesReader;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.readers.OtherIssuesReader;

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

	@Bean
	@StepScope
	public ListItemReader<BugIdBean> listIssuesReader(final PortalAuthManager authManager,
			final MantisConnectBindingStub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobParameters['mantis.issues_id']}") final String issuesIds) {

		final List<BugIdBean> itemList = new ArrayList<BugIdBean>();
		if (issuesIds != null && !issuesIds.isEmpty()) {
			final String[] strIds = issuesIds.split(";");
			for (final String strId : strIds) {
				final long idValue = Long.valueOf(strId);
				final BugIdBean bean = new BugIdBean();
				bean.setId(BigInteger.valueOf(idValue));
				itemList.add(bean);
			}
		}

		final ListItemReader<BugIdBean> reader = new ListItemReader<BugIdBean>(itemList);
		return reader;
	}

	@Bean
	@StepScope
	public FlatFileItemReader<BugIdBean> csvIssuesReader(final ResourceLoader resourceLoader,
			@Value("#{jobParameters['mantis.filepath']}") final String filePath) {

		final FlatFileItemReader<BugIdBean> reader = new FlatFileItemReader<BugIdBean>();
		reader.setResource(resourceLoader.getResource(filePath));

		final DefaultLineMapper<BugIdBean> lineMapper = new DefaultLineMapper<BugIdBean>();

		final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[]{"id"});
		lineMapper.setLineTokenizer(tokenizer);

		final BeanWrapperFieldSetMapper<BugIdBean> mapper = new BeanWrapperFieldSetMapper<BugIdBean>();
		mapper.setTargetType(BugIdBean.class);
		lineMapper.setFieldSetMapper(mapper);

		reader.setLineMapper(lineMapper);

		return reader;
	}
}
