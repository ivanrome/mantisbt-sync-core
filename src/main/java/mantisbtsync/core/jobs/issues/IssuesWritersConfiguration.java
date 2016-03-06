/**
 *
 */
package mantisbtsync.core.jobs.issues;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.writers.BugCustomFieldsWriter;
import mantisbtsync.core.jobs.issues.writers.BugHistoryWriter;
import mantisbtsync.core.jobs.issues.writers.BugNotesWriter;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jdevarulrajah
 *
 */
@Configuration
public class IssuesWritersConfiguration {

	@Bean
	@StepScope
	public CompositeItemWriter<BugBean> compositeIssuesWriter(final JdbcBatchItemWriter<BugBean> bugsWriter,
			final BugNotesWriter bugNotesWriter, final BugCustomFieldsWriter bugCustomFieldsWriter,
			final BugHistoryWriter bugHistoryWriter, final DataSource dataSource) {

		bugsWriter.setDataSource(dataSource);
		bugsWriter.afterPropertiesSet();

		bugNotesWriter.setDataSource(dataSource);
		bugNotesWriter.afterPropertiesSet();

		bugCustomFieldsWriter.setDataSource(dataSource);
		bugCustomFieldsWriter.afterPropertiesSet();

		bugHistoryWriter.setDataSource(dataSource);
		bugHistoryWriter.afterPropertiesSet();

		final CompositeItemWriter<BugBean> compositeWriter = new CompositeItemWriter<BugBean>();
		final List<ItemWriter<? super BugBean>> writerList = new ArrayList<ItemWriter<? super BugBean>>();
		writerList.add(bugsWriter);
		writerList.add(bugNotesWriter);
		writerList.add(bugCustomFieldsWriter);
		writerList.add(bugHistoryWriter);
		compositeWriter.setDelegates(writerList);

		return compositeWriter;
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<BugBean> bugsWriter() {

		final JdbcBatchItemWriter<BugBean> writer = new JdbcBatchItemWriter<BugBean>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BugBean>());
		writer.setSql("MERGE INTO mantis_bug_table\n"
				+ " USING (SELECT :id as id, :projectId as project_id, :reporterId as reporter_id,\n"
				+ " 		:handlerId as handler_id, :priorityId as priority_id,\n"
				+ " 		:severityId as severity_id, :statusId as status_id,\n"
				+ " 		:resolutionId as resolution_id, :description as description,\n"
				+ " 		:stepsToReproduce as steps_to_reproduce, :additionalInformation as additional_information,\n"
				+ " 		:platform as platform, :version as version,\n"
				+ " 		:fixedInVersion as fixed_in_version, :targetVersion as target_version,\n"
				+ " 		:summary as summary, :category as category, :dateSubmitted as date_submitted,\n"
				+ " 		:lastUpdated as last_updated FROM dual) src\n"
				+ " ON (dest.id = src.id)\n"
				+ " WHEN NOT MATCHED THEN INSERT (id, project_id, reporter_id, handler_id, priority_id, \n"
				+ " 		severity_id, status_id, resolution_id, description, steps_to_reproduce, \n"
				+ " 		additional_information, platform, version, fixed_in_version, target_version, \n"
				+ " 		summary, category, date_submitted, last_updated) \n"
				+ " 		      VALUES (src.id, src.projectId, src.reporterId, src.handlerId, src.priorityId, \n"
				+ " 				src.severityId, src.statusId, src.resolutionId, src.description, \n"
				+ " 				src.stepsToReproduce, src.additionalInformation, src.platform, src.version,\n"
				+ " 				src.fixedInVersion, src.targetVersion, src.summary, src.category, \n"
				+ " 				src.dateSubmitted, src.lastUpdated)\n"
				+ " WHEN MATCHED THEN UPDATE SET dest.project_id = src.project_id, dest.reporter_id = src.reporter_id, \n"
				+ " 	dest.handler_id = src.handler_id, dest.priority_id = src.priority_id, dest.severity_id = src.severity_id,\n"
				+ " 	dest.status_id = src.status_id, dest.resolution_id = src.resolution_id, dest.description = src.description,\n"
				+ " 	dest.steps_to_reproduce = src.steps_to_reproduce, dest.additional_information = src.additional_information,\n"
				+ " 	dest.platform = src.platform, dest.version = src.version, dest.fixed_in_version = src.fixed_in_version,\n"
				+ " 	dest.target_version = src.target_version, dest.summary = src.summary, dest.category = src.category,\n"
				+ " 	dest.date_submitted = src.date_submitted, dest.last_updated = src.last_updated");
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public BugNotesWriter bugNotesWriter(final DataSource dataSource) {
		final BugNotesWriter writer = new BugNotesWriter();
		return writer;
	}

	@Bean
	@StepScope
	public BugCustomFieldsWriter bugCustomFieldsWriter(final DataSource dataSource) {
		final BugCustomFieldsWriter writer = new BugCustomFieldsWriter();
		return writer;
	}

	@Bean
	@StepScope
	public BugHistoryWriter bugHistoryWriter(final DataSource dataSource) {
		final BugHistoryWriter writer = new BugHistoryWriter();
		return writer;
	}
}
