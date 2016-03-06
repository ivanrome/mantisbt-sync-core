/**
 *
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

	private static final String SQL_QUERY = "MERGE INTO mantis_custom_field_string_table\n"
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
