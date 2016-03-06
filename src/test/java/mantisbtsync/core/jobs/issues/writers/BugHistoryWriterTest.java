/**
 *
 */
package mantisbtsync.core.jobs.issues.writers;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.beans.BugHistoryBean;
import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
public class BugHistoryWriterTest extends AbstractSqlWriterTest {

	@Autowired
	BugHistoryWriter bugHistoryWriter;

	@Test
	public void test() throws Exception {

		lauchOperation(Operations.deleteAllFrom("mantis_bug_history_table"));

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "old_user_1")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary")
				.values(1, 1, "summary_1")
				.build()
				);

		lauchOperation(op);

		bugHistoryWriter.afterPropertiesSet();
		bugHistoryWriter.write(buildItems());
	}

	/**
	 * Build the items to write.
	 *
	 * @return items
	 */
	private List<BugBean> buildItems() {

		final Calendar cal = Calendar.getInstance();
		final Timestamp date = new Timestamp(cal.getTimeInMillis());
		final List<BugBean> items = new ArrayList<BugBean>();

		final BugBean item1 = new BugBean();
		final BugHistoryBean hist = new BugHistoryBean();
		hist.setBugId(BigInteger.ONE);
		hist.setUserId(BigInteger.ONE);
		hist.setOldValue("old");
		hist.setNewValue("new");
		hist.setHistoryType(BigInteger.TEN);
		hist.setDateModified(date);

		item1.getHistory().add(hist);
		items.add(item1);

		return items;
	}

	/**
	 * @return the bugHistoryWriter
	 */
	public BugHistoryWriter getBugHistoryWriter() {
		return bugHistoryWriter;
	}

	/**
	 * @param bugHistoryWriter the bugHistoryWriter to set
	 */
	public void setBugHistoryWriter(final BugHistoryWriter bugHistoryWriter) {
		this.bugHistoryWriter = bugHistoryWriter;
	}

}
