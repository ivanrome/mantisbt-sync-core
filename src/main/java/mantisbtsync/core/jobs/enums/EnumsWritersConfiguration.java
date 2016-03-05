/**
 *
 */
package mantisbtsync.core.jobs.enums;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * Configuration for the writers used by the job of
 * Mantis enumerations syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class EnumsWritersConfiguration {

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> customFieldTypesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_custom_field_types", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> etasWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_etas", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> prioritiesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_priorities", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> projectionsWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_projections", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> projectStatusWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_project_status", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> projectViewStatesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_project_view_states", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> reproducibilitiesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_reproducibilities", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> resolutionsWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_resolutions", dataSource);
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> severitiesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_severities", dataSource);
	}

	private JdbcBatchItemWriter<ObjectRef> getEnumWriter(final String tableName, final DataSource dataSource) {
		final JdbcBatchItemWriter<ObjectRef> writer = new JdbcBatchItemWriter<ObjectRef>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ObjectRef>());
		writer.setSql(getMergeStatement(tableName));
		writer.setDataSource(dataSource);
		return writer;
	}

	private String getMergeStatement(final String tableName) {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("MERGE INTO ");
		strBuilder.append(tableName);
		strBuilder.append(" dest\n");
		strBuilder.append(" USING (SELECT :id as id, :name as name FROM dual) src\n");
		strBuilder.append(" ON (dest.id = src.id)\n");
		strBuilder.append(" WHEN MATCHED THEN UPDATE SET dest.name = src.name\n");
		strBuilder.append(" WHEN NOT MATCHED THEN INSERT (id, name) VALUES (src.id, src.name)\n");

		return strBuilder.toString();
	}
}
