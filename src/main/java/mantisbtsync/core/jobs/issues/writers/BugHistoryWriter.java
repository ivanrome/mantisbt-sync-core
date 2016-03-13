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
package mantisbtsync.core.jobs.issues.writers;

import java.util.List;

import javax.sql.DataSource;

import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.beans.BugHistoryBean;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

/**
 * @author jdevarulrajah
 *
 */
public class BugHistoryWriter implements ItemWriter<BugBean> {

	private final JdbcBatchItemWriter<BugHistoryBean> writer;

	private static final String SQL_QUERY = "MERGE INTO mantis_bug_history_table dest\n"
			+ " USING (SELECT :bugId as bug_id, :userId as user_id, :fieldName as field_name,\n"
			+ " 		:oldValue as old_value, :newValue as new_value, :historyType as history_type,\n"
			+ " 		:dateModified as date_modified FROM dual) src\n"
			+ " ON (dest.bug_id = src.bug_id AND dest.user_id = src.user_id AND dest.history_type = src.history_type\n"
			+ "   AND dest.field_name = src.field_name AND dest.old_value = src.old_value AND dest.new_value = src.new_value\n"
			+ "   AND cast(dest.date_modified as datetime) = cast(src.date_modified as datetime))\n"
			+ " WHEN NOT MATCHED THEN INSERT (bug_id, user_id, field_name, old_value, new_value, history_type, date_modified)\n"
			+ " 	VALUES (src.bug_id, src.user_id, src.field_name, src.old_value, src.new_value, src.history_type, src.date_modified)";

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
