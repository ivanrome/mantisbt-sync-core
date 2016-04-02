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
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.jobs.projects.beans.ProjectCategoryBean;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.beans.ProjectCustomFieldBean;

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
		writer.setSql("INSERT INTO mantis_category_table (name, project_id)\n"
				+ " SELECT :name, :projectId FROM dual\n"
				+ " WHERE NOT EXISTS (SELECT 1 FROM mantis_category_table dest\n"
				+ "			WHERE dest.name = :name AND dest.project_id = :projectId)");
		writer.setDataSource(dataSource);
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public CompositeItemWriter<ProjectCustomFieldBean> projectCustomFieldsWriter(final DataSource dataSource) {

		final JdbcBatchItemWriter<ProjectCustomFieldBean> writer1 = new JdbcBatchItemWriter<ProjectCustomFieldBean>();
		writer1.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ProjectCustomFieldBean>());
		writer1.setSql("INSERT INTO mantis_custom_field_table\n"
				+ " (id, name, type_id, possible_values, default_value, valid_regexp)\n"
				+ " VALUES (:id, :name, :typeId, :possibleValues, :defaultValue, :validRegexp)\n"
				+ " ON DUPLICATE KEY UPDATE name = :name, type_id = :typeId, possible_values = :possibleValues,\n"
				+ " default_value = :defaultValue, valid_regexp = :validRegexp");
		writer1.setDataSource(dataSource);
		writer1.afterPropertiesSet();

		final JdbcBatchItemWriter<ProjectCustomFieldBean> writer2 = new JdbcBatchItemWriter<ProjectCustomFieldBean>();
		writer2.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ProjectCustomFieldBean>());
		writer2.setSql("INSERT INTO mantis_custom_field_project_table (field_id, project_id)\n"
				+ " VALUES (:id, :projectId)\n"
				+ " ON DUPLICATE KEY UPDATE project_id = project_id");
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
		writer1.setSql("INSERT INTO mantis_user_table (id, name)\n"
				+ " VALUES (:id, :name)\n"
				+ " ON DUPLICATE KEY UPDATE name = :name");
		writer1.setDataSource(dataSource);
		writer1.afterPropertiesSet();

		final JdbcBatchItemWriter<AccountData> writer2 = new JdbcBatchItemWriter<AccountData>();
		writer2.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccountData>());
		writer2.setSql("INSERT INTO mantis_project_user_list_table (user_id, project_id)\n"
				+ " VALUES (:id, " + projectId + ")\n"
				+ " ON DUPLICATE KEY UPDATE project_id = project_id");
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
		writer.setSql("INSERT INTO mantis_project_version_table\n"
				+ " (id, version, project_id, description, released, obsolete)"
				+ " VALUES (:id, :name, :project_id, :description, :released, :obsolete)\n"
				+ " ON DUPLICATE KEY UPDATE version = :name, project_id = :project_id,\n"
				+ "	description = :description, released = :released, obsolete = :obsolete");
		writer.setDataSource(dataSource);
		return writer;
	}
}
