/**
 *
 */
package mantisbtsync.core.jobs.enums;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.common.readers.AxisAuthItemsArrayReader;

import org.apache.axis.client.Stub;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * Configuration for the readers used by the job of
 * Mantis enumerations syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class EnumsReadersConfiguration {

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> customFieldTypesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_custom_field_types", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> etasReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_etas", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> prioritiesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_priorities", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> projectionsReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_projections", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> projectStatusReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_project_status", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> projectViewStatesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_project_view_states", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> reproducibilitiesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {
		return getEnumReader("mc_enum_reproducibilities", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> resolutionsReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_resolutions", authManager,
				clientStub, userName, password);
	}

	@Bean
	@StepScope
	public AxisAuthItemsArrayReader<ObjectRef> severitiesReader(final PortalAuthManager authManager,
			final Stub clientStub,
			@Value("#{jobParameters['mantis.username']}") final String userName,
			@Value("#{jobParameters['mantis.password']}") final String password) {

		return getEnumReader("mc_enum_severities", authManager,
				clientStub, userName, password);
	}

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
