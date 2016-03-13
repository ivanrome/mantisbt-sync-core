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
import mantisbtsync.core.jobs.issues.beans.BugNoteBean;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

/**
 * @author jdevarulrajah
 *
 */
public class BugNotesWriter implements ItemWriter<BugBean> {

	private final JdbcBatchItemWriter<BugNoteBean> writer;

	private static final String SQL_QUERY = "MERGE INTO mantis_bugnote_table dest\n"
			+ " USING (SELECT :id as id, :bugId as bug_id, :reporterId as reporter_id,\n"
			+ " 	:textNote as text_note, :dateSubmitted as date_submitted, :lastModified as last_modified FROM dual) src\n"
			+ " ON (dest.id = src.id)\n"
			+ " WHEN NOT MATCHED THEN INSERT (id, bug_id, reporter_id, text_note, date_submitted, last_modified)\n"
			+ " 		      VALUES (src.id, src.bug_id, src.reporter_id, src.text_note, src.date_submitted, src.last_modified)\n"
			+ " WHEN MATCHED THEN UPDATE SET dest.text_note = src.text_note, dest.last_modified = src.last_modified";

	public BugNotesWriter() {
		writer = new JdbcBatchItemWriter<BugNoteBean>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BugNoteBean>());
		writer.setSql(SQL_QUERY);
		writer.setAssertUpdates(false);
	}

	@Override
	public void write(final List<? extends BugBean> items) throws Exception {
		if (items != null) {
			for (final BugBean bug : items) {
				if (bug.getNotes() != null) {
					writer.write(bug.getNotes());
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
