/**
 *
 */
package mantisbtsync.core.jobs.projects;

import java.math.BigInteger;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.common.readers.AxisAuthItemsArrayReader;

import org.apache.axis.client.Stub;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
