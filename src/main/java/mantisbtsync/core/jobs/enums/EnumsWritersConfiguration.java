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

	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> statusWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_status", dataSource);
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
