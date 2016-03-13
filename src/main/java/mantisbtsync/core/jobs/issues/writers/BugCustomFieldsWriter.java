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
import mantisbtsync.core.jobs.issues.beans.BugCustomFieldValue;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

/**
 * @author jdevarulrajah
 *
 */
public class BugCustomFieldsWriter implements ItemWriter<BugBean> {

	private final JdbcBatchItemWriter<BugCustomFieldValue> writer;

	private static final String SQL_QUERY = "MERGE INTO mantis_custom_field_string_table dest\n"
			+ " USING (SELECT :fieldId as field_id, :bugId as bug_id, :fieldValue as field_value FROM dual) src\n"
			+ " ON (dest.field_id = src.field_id AND dest.bug_id = src.bug_id)\n"
			+ " WHEN NOT MATCHED THEN INSERT (field_id, bug_id, field_value)\n"
			+ " 		      VALUES (src.field_id, src.bug_id, src.field_value)\n"
			+ " WHEN MATCHED THEN UPDATE SET dest.field_value = src.field_value";

	public BugCustomFieldsWriter() {
		writer = new JdbcBatchItemWriter<BugCustomFieldValue>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BugCustomFieldValue>());
		writer.setSql(SQL_QUERY);
		writer.setAssertUpdates(false);
	}

	@Override
	public void write(final List<? extends BugBean> items) throws Exception {
		if (items != null) {
			for (final BugBean bug : items) {
				if (bug.getCustomFields() != null) {
					writer.write(bug.getCustomFields());
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
