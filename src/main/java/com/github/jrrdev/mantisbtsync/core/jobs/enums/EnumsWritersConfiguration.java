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

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * Configuration for the writers used to sync MantisBT enumerations.
 * Those writers are used to populated tables with mantis_enum_ prefix.
 *
 * MantisBT enumerations are system values used to defined status, priorities...
 *
 * @author jrrdev
 *
 */
@Configuration
public class EnumsWritersConfiguration {

	/**
	 * Writer for the custom fields types. Write into mantis_enum_custom_field_types table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the custom fields types
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> customFieldTypesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_custom_field_types", dataSource);
	}

	/**
	 * Writer for the etas. Write into mantis_enum_etas table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the etas
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> etasWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_etas", dataSource);
	}

	/**
	 * Writer for the priorities. Write into mantis_enum_priorities table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the priorities
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> prioritiesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_priorities", dataSource);
	}

	/**
	 * Writer for the projections. Write into mantis_enum_projections table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the projections
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> projectionsWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_projections", dataSource);
	}

	/**
	 * Writer for the project status. Write into mantis_enum_project_status table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the project status
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> projectStatusWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_project_status", dataSource);
	}

	/**
	 * Writer for the project view states. Write into mantis_enum_project_view_states table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the project view states
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> projectViewStatesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_project_view_states", dataSource);
	}

	/**
	 * Writer for the reproducibilities. Write into mantis_enum_reproducibilities table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the reproducibilities
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> reproducibilitiesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_reproducibilities", dataSource);
	}

	/**
	 * Writer for the resolutions. Write into mantis_enum_resolutions table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the resolutions
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> resolutionsWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_resolutions", dataSource);
	}

	/**
	 * Writer for the severities. Write into mantis_enum_severities table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the severities
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> severitiesWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_severities", dataSource);
	}

	/**
	 * Writer for the issues status. Write into mantis_enum_status table.
	 *
	 * @param dataSource
	 * 		The datasource
	 * @return the writer for the issues status
	 */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ObjectRef> statusWriter(final DataSource dataSource) {
		return getEnumWriter("mantis_enum_status", dataSource);
	}

	/**
	 * Build the JdbcBatchItemWriter for the given enums table.
	 *
	 * @param tableName
	 * 		Name of the destination table
	 * @param dataSource
	 * 		The datasource
	 * @return
	 */
	private JdbcBatchItemWriter<ObjectRef> getEnumWriter(final String tableName, final DataSource dataSource) {
		final JdbcBatchItemWriter<ObjectRef> writer = new JdbcBatchItemWriter<ObjectRef>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ObjectRef>());
		writer.setSql(getMergeStatement(tableName));
		writer.setDataSource(dataSource);
		return writer;
	}

	/**
	 * Build the SQL statement used to update the given table.
	 * This statement performs an upsert operation.
	 *
	 * @param tableName
	 * 		Name of the destination table
	 * @return the SQL statement used to update the given table
	 */
	private String getMergeStatement(final String tableName) {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("INSERT INTO ");
		strBuilder.append(tableName);
		strBuilder.append(" (id, name) values (:id, :name)\n");
		strBuilder.append(" ON DUPLICATE KEY UPDATE name = :name");

		return strBuilder.toString();
	}
}
