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
package com.github.jrrdev.mantisbtsync.core.jobs.projects;

import java.math.BigInteger;

import org.apache.axis.client.Stub;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.common.readers.AxisAuthItemsArrayReader;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.CustomFieldDefinitionData;
import biz.futureware.mantis.rpc.soap.client.ProjectVersionData;

/**
 * Configuration for the readers used by the job of
 * Mantis projects syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class ProjectsReadersConfiguration {

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<String> projectCategoriesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final AxisAuthItemsArrayReader<String> reader = new AxisAuthItemsArrayReader<String>();
		reader.setTargetMethod("mc_project_get_categories");
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setArguments(new Object[]{userName, password, projectId});

		return reader;
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<CustomFieldDefinitionData> projectCustomFieldsReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final AxisAuthItemsArrayReader<CustomFieldDefinitionData> reader = new AxisAuthItemsArrayReader<CustomFieldDefinitionData>();
		reader.setTargetMethod("mc_project_get_custom_fields");
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setArguments(new Object[]{userName, password, projectId});

		return reader;
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<AccountData> projectUsersReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId,
			@Value("#{jobExecutionContext['mantis.acess_level']}") final BigInteger acessLevel) {

		final AxisAuthItemsArrayReader<AccountData> reader = new AxisAuthItemsArrayReader<AccountData>();
		reader.setTargetMethod("mc_project_get_users");
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setArguments(new Object[]{userName, password, projectId, acessLevel});

		return reader;
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ProjectVersionData> projectVersionsReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password,
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final AxisAuthItemsArrayReader<ProjectVersionData> reader = new AxisAuthItemsArrayReader<ProjectVersionData>();
		reader.setTargetMethod("mc_project_get_versions");
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setArguments(new Object[]{userName, password, projectId});

		return reader;
	}
}
