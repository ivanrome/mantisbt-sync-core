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
package com.github.jrrdev.mantisbtsync.core.jobs.enums;

import org.apache.axis.client.Stub;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.common.readers.AxisAuthItemsArrayReader;

/**
 * Configuration for the readers used to sync MantisBT enumerations.
 * Beware of the fact that access to the webservice operations used for this sync
 * may be denied by the server depending on user privilege.
 *
 * MantisBT enumerations are system values used to defined status, priorities...
 *
 * @author jrrdev
 *
 */
@Configuration
public class EnumsReadersConfiguration {

	/**
	 * Reader for the custom fields types. Use mc_enum_custom_field_types
	 * ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the custom fields types
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> customFieldTypesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_custom_field_types", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the etas. Use mc_enum_etas ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the etas
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> etasReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_etas", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the priorities. Use mc_enum_priorities ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the priorities
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> prioritiesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_priorities", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the projections. Use mc_enum_projections ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the projections
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> projectionsReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_projections", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the project status. Use mc_enum_project_status ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the project status
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> projectStatusReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_project_status", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the project view states. Use mc_enum_project_view_states ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the project view states
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> projectViewStatesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_project_view_states", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the reprocibilites. Use mc_enum_reproducibilities ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the reprocibilites
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> reproducibilitiesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {
		return getEnumReader("mc_enum_reproducibilities", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the resolutions states. Use mc_enum_resolutions ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the resolutions states
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> resolutionsReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_resolutions", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the severities. Use mc_enum_severities ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the severities
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> severitiesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_severities", authManager,
				clientStub, userName, password);
	}

	/**
	 * Reader for the issues status. Use mc_enum_status ws operation.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader for the issues status
	 */
	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> statusReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_status", authManager,
				clientStub, userName, password);
	}

	/**
	 * Build the reader for the given ws operation.
	 *
	 * @param operation
	 * @param authManager
	 * 			The portal auth manager
	 * @param clientStub
	 * 			Axis client stub
	 * @param userName
	 * 			MantisBT username. If anonymous access is used, should be an empty string.
	 * @param password
	 * 			MantisBT password. If anonymous access is used, should be an empty string.
	 * @return the reader
	 */
	private AxisAuthItemsArrayReader<ObjectRef> getEnumReader(final String operation,
			final PortalAuthManager authManager, final Stub clientStub,
			final String userName, final String password) {

		final AxisAuthItemsArrayReader<ObjectRef> reader = new AxisAuthItemsArrayReader<ObjectRef>();
		reader.setTargetMethod(operation);
		reader.setAuthManager(authManager);
		reader.setClientStub(clientStub);
		reader.setArguments(new Object[]{userName, password});

		return reader;
	}
}
