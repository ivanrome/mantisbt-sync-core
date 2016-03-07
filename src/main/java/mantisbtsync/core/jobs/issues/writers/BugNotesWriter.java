/**
 *
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
