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
			final BugHistoryWriter bugHistoryWriter) {

		bugsWriter.afterPropertiesSet();
		bugNotesWriter.afterPropertiesSet();
		bugCustomFieldsWriter.afterPropertiesSet();
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
	public JdbcBatchItemWriter<BugBean> bugsWriter(final DataSource dataSource) {

		final JdbcBatchItemWriter<BugBean> writer = new JdbcBatchItemWriter<BugBean>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BugBean>());
		writer.setSql("MERGE INTO mantis_bug_table dest\n"
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
				+ " 		      VALUES (src.id, src.project_id, src.reporter_id, src.handler_id, src.priority_id,\n"
				+ " 				src.severity_id, src.status_id, src.resolution_id, src.description, \n"
				+ " 				src.steps_to_reproduce, src.additional_information, src.platform, src.version,\n"
				+ " 				src.fixed_in_version, src.target_version, src.summary, src.category, \n"
				+ " 				src.date_submitted, src.last_updated)\n"
				+ " WHEN MATCHED THEN UPDATE SET dest.project_id = src.project_id, dest.reporter_id = src.reporter_id, \n"
				+ " 	dest.handler_id = src.handler_id, dest.priority_id = src.priority_id, dest.severity_id = src.severity_id,\n"
				+ " 	dest.status_id = src.status_id, dest.resolution_id = src.resolution_id, dest.description = src.description,\n"
				+ " 	dest.steps_to_reproduce = src.steps_to_reproduce, dest.additional_information = src.additional_information,\n"
				+ " 	dest.platform = src.platform, dest.version = src.version, dest.fixed_in_version = src.fixed_in_version,\n"
				+ " 	dest.target_version = src.target_version, dest.summary = src.summary, dest.category = src.category,\n"
				+ " 	dest.date_submitted = src.date_submitted, dest.last_updated = src.last_updated");
		writer.setDataSource(dataSource);
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public BugNotesWriter bugNotesWriter(final DataSource dataSource) {
		final BugNotesWriter writer = new BugNotesWriter();
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	@StepScope
	public BugCustomFieldsWriter bugCustomFieldsWriter(final DataSource dataSource) {
		final BugCustomFieldsWriter writer = new BugCustomFieldsWriter();
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	@StepScope
	public BugHistoryWriter bugHistoryWriter(final DataSource dataSource) {
		final BugHistoryWriter writer = new BugHistoryWriter();
		writer.setDataSource(dataSource);
		return writer;
	}
}
