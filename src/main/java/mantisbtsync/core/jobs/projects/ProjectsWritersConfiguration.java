/**
 *
 */
package mantisbtsync.core.jobs.projects;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import mantisbtsync.core.jobs.projects.beans.ProjectCategoryBean;
import mantisbtsync.core.jobs.projects.beans.ProjectCustomFieldBean;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ProjectVersionData;

/**
 * Configuration for the writers used by the job of
 * Mantis projects syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class ProjectsWritersConfiguration {

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ProjectCategoryBean> projectCategoriesWriter(final DataSource dataSource) {

		final JdbcBatchItemWriter<ProjectCategoryBean> writer = new JdbcBatchItemWriter<ProjectCategoryBean>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ProjectCategoryBean>());
		writer.setSql("MERGE INTO mantis_category_table dest\n"
				+ " USING (SELECT :name as name, :projectId as project_id FROM dual) src\n"
				+ " ON (dest.name = src.name AND dest.project_id = src.project_id)\n"
				+ " WHEN NOT MATCHED THEN INSERT (name, project_id) VALUES (src.name, src.project_id)");
		writer.setDataSource(dataSource);
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public CompositeItemWriter<ProjectCustomFieldBean> projectCustomFieldsWriter(final DataSource dataSource) {

		final JdbcBatchItemWriter<ProjectCustomFieldBean> writer1 = new JdbcBatchItemWriter<ProjectCustomFieldBean>();
		writer1.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ProjectCustomFieldBean>());
		writer1.setSql("MERGE INTO mantis_custom_field_table dest\n"
				+ " USING (SELECT :id as id, :name as name, :typeId as type_id,"
				+ "	:possibleValues as possible_values, :defaultValue as default_value,"
				+ "	:validRegexp as valid_regexp\n"
				+ "	FROM dual) src\n"
				+ " ON (dest.id = src.id)\n"
				+ " WHEN NOT MATCHED THEN\n"
				+ " INSERT (id, name, type_id, possible_values, default_value, valid_regexp)\n"
				+ " VALUES (src.id, src.name, src.type_id, src.possible_values, src.default_value, src.valid_regexp)\n"
				+ " WHEN MATCHED THEN UPDATE SET dest.name = src.name, dest.type_id = src.type_id,\n"
				+ "		dest.possible_values = src.possible_values, dest.default_value = src.default_value,\n"
				+ "		dest.valid_regexp = src.valid_regexp");
		writer1.setDataSource(dataSource);
		writer1.afterPropertiesSet();

		final JdbcBatchItemWriter<ProjectCustomFieldBean> writer2 = new JdbcBatchItemWriter<ProjectCustomFieldBean>();
		writer2.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ProjectCustomFieldBean>());
		writer2.setSql("MERGE INTO mantis_custom_field_project_table dest\n"
				+ " USING (SELECT :id as field_id, :projectId as project_id FROM dual) src\n"
				+ " ON (dest.field_id = src.field_id AND dest.project_id = src.project_id)\n"
				+ " WHEN NOT MATCHED THEN\n"
				+ " INSERT (field_id, project_id) VALUES (src.field_id, src.project_id)");
		writer2.setDataSource(dataSource);
		writer2.setAssertUpdates(false);
		writer2.afterPropertiesSet();

		final CompositeItemWriter<ProjectCustomFieldBean> compositeWriter = new CompositeItemWriter<ProjectCustomFieldBean>();
		final List<ItemWriter<? super ProjectCustomFieldBean>> writerList = new ArrayList<ItemWriter<? super ProjectCustomFieldBean>>();
		writerList.add(writer1);
		writerList.add(writer2);
		compositeWriter.setDelegates(writerList);


		return compositeWriter;
	}

	@Bean
	@StepScope
	public CompositeItemWriter<AccountData> projectUsersWriter(final DataSource dataSource,
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final JdbcBatchItemWriter<AccountData> writer1 = new JdbcBatchItemWriter<AccountData>();
		writer1.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccountData>());
		writer1.setSql("MERGE INTO mantis_user_table dest\n"
				+ " USING (SELECT :id as id, :name as name FROM dual) src\n"
				+ " ON (dest.id = src.id)\n"
				+ " WHEN NOT MATCHED THEN INSERT (id, name) VALUES (src.id, src.name)\n"
				+ " WHEN MATCHED THEN UPDATE SET dest.name = src.name");
		writer1.setDataSource(dataSource);
		writer1.afterPropertiesSet();

		final JdbcBatchItemWriter<AccountData> writer2 = new JdbcBatchItemWriter<AccountData>();
		writer2.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccountData>());
		writer2.setSql("MERGE INTO mantis_project_user_list_table dest\n"
				+ " USING (SELECT :id as user_id, " + projectId + " as project_id FROM dual) src\n"
				+ " ON (dest.user_id = src.user_id AND dest.project_id = src.project_id)\n"
				+ " WHEN NOT MATCHED THEN\n"
				+ " INSERT (user_id, project_id) VALUES (src.user_id, src.project_id)");
		writer2.setDataSource(dataSource);
		writer2.setAssertUpdates(false);
		writer2.afterPropertiesSet();

		final CompositeItemWriter<AccountData> compositeWriter = new CompositeItemWriter<AccountData>();
		final List<ItemWriter<? super AccountData>> writerList = new ArrayList<ItemWriter<? super AccountData>>();
		writerList.add(writer1);
		writerList.add(writer2);
		compositeWriter.setDelegates(writerList);

		return compositeWriter;
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ProjectVersionData> projectVersionsWriter(final DataSource dataSource) {

		final JdbcBatchItemWriter<ProjectVersionData> writer = new JdbcBatchItemWriter<ProjectVersionData>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ProjectVersionData>());
		writer.setSql("MERGE INTO mantis_project_version_table dest\n"
				+ " USING (SELECT :id as id, :name as version, :project_id as project_id,\n"
				+ " 			:description as description, :released as released, :obsolete as obsolete\n"
				+ " 	   FROM dual) src\n"
				+ " ON (dest.id = src.id)\n"
				+ " WHEN NOT MATCHED THEN\n"
				+ " INSERT (id, version, project_id, description, released, obsolete)\n"
				+ " VALUES (src.id, src.version, src.project_id, src.description, src.released, src.obsolete)\n"
				+ " WHEN MATCHED THEN UPDATE SET dest.version = src.version, dest.project_id = src.project_id,\n"
				+ "		dest.description = src.description, dest.released = src.released, dest.obsolete = src.obsolete");
		writer.setDataSource(dataSource);
		return writer;
	}
}
