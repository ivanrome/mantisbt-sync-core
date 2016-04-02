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
package com.github.jrrdev.mantisbtsync.core.jobs.issues.writers;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugHistoryBean;

/**
 * @author jdevarulrajah
 *
 */
public class BugHistoryWriter implements ItemWriter<BugBean> {

	private final JdbcBatchItemWriter<BugHistoryBean> writer;

	private static final String SQL_QUERY = "INSERT INTO mantis_bug_history_table\n"
			+ " (bug_id, user_id, field_name, old_value, new_value, history_type, date_modified)\n"
			+ " SELECT :bugId, :userId, :fieldName, :oldValue, :newValue, :historyType,\n"
			+ "	:dateModified FROM dual\n"
			+ " WHERE NOT EXISTS (SELECT 1 FROM mantis_bug_history_table\n"
			+ " 		WHERE bug_id = :bugId AND user_id = :userId AND field_name = :fieldName\n"
			+ "			AND old_value = :oldValue AND new_value = :newValue\n"
			+ "			AND history_type = :historyType AND date_modified = :dateModified)";

	public BugHistoryWriter() {
		writer = new JdbcBatchItemWriter<BugHistoryBean>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BugHistoryBean>());
		writer.setSql(SQL_QUERY);
		writer.setAssertUpdates(false);
	}

	@Override
	public void write(final List<? extends BugBean> items) throws Exception {
		if (items != null) {
			for (final BugBean bug : items) {
				if (bug.getHistory() != null) {
					writer.write(bug.getHistory());
				}
			}
		}
	}

	public void afterPropertiesSet() {
		writer.afterPropertiesSet();
	}

	public void setDataSource(final DataSource dataSource) {
		writer.setDataSource(dataSource);
	}
}
