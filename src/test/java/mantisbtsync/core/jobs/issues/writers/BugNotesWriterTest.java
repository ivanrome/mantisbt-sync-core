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
import mantisbtsync.core.jobs.issues.beans.BugNoteBean;
import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
public class BugNotesWriterTest extends AbstractSqlWriterTest {

	@Autowired
	BugNotesWriter bugNotesWriter;

	@Test
	public void test() throws Exception {
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

		bugNotesWriter.afterPropertiesSet();
		bugNotesWriter.write(buildItems());
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
		final BugNoteBean note = new BugNoteBean();
		note.setId(BigInteger.ONE);
		note.setBugId(BigInteger.ONE);
		note.setReporterId(BigInteger.ONE);
		note.setTextNote("note_1");
		note.setDateSubmitted(date);
		note.setLastModified(date);

		item1.getNotes().add(note);
		items.add(item1);

		return items;
	}

	/**
	 * @return the bugNotesWriter
	 */
	public BugNotesWriter getBugNotesWriter() {
		return bugNotesWriter;
	}

	/**
	 * @param bugNotesWriter the bugNotesWriter to set
	 */
	public void setBugNotesWriter(final BugNotesWriter bugNotesWriter) {
		this.bugNotesWriter = bugNotesWriter;
	}

}
