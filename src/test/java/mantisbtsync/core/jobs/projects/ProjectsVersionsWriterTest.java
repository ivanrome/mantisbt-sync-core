/**
 *
 */
package mantisbtsync.core.jobs.projects;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import biz.futureware.mantis.rpc.soap.client.ProjectVersionData;

import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
public class ProjectsVersionsWriterTest extends AbstractSqlWriterTest {

	@Autowired
	JdbcBatchItemWriter<ProjectVersionData> projectVersionsWriter;

	public StepExecution getStepExecution() {

		final StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
		stepExecution.getExecutionContext().putInt("mantis.loop.project_id", 1);

		return stepExecution;
	}

	@Test
	public void test() throws Exception {
		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_project_version_table")
				.columns("id", "version", "project_id")
				.values(1, "old_version_1", 1)
				.build());

		lauchOperation(op);

		projectVersionsWriter.write(buildItems());

		final List<ProjectVersionData> results = getJdbcTemplate()
				.query("SELECT id, version as name, project_id"
						+ " FROM mantis_project_version_table"
						+ " WHERE project_id = 1",
						new BeanPropertyRowMapper<ProjectVersionData>(ProjectVersionData.class));

		assertEquals(2, results.size());

		for (final ProjectVersionData item : results) {
			assertEquals(BigInteger.ONE, item.getProject_id());
			if (item.getId() == BigInteger.ONE) {
				assertEquals("new_version_1", item.getName());
			} else {
				assertEquals(BigInteger.valueOf(2), item.getId());
				assertEquals("new_version_2", item.getName());
			}
		}
	}

	/**
	 * Build the items to write.
	 *
	 * @return items
	 */
	private List<ProjectVersionData> buildItems() {
		final List<ProjectVersionData> items = new ArrayList<ProjectVersionData>();

		final ProjectVersionData item1 = new ProjectVersionData();
		item1.setId(BigInteger.ONE);
		item1.setName("new_version_1");
		item1.setProject_id(BigInteger.ONE);

		final ProjectVersionData item2 = new ProjectVersionData();
		item2.setId(BigInteger.valueOf(2));
		item2.setName("new_version_2");
		item2.setProject_id(BigInteger.ONE);

		items.add(item1);
		items.add(item2);

		return items;
	}

	/**
	 * @return the projectVersionsWriter
	 */
	public final JdbcBatchItemWriter<ProjectVersionData> getProjectVersionsWriter() {
		return projectVersionsWriter;
	}

	/**
	 * @param projectVersionsWriter the projectVersionsWriter to set
	 */
	public final void setProjectUsersWriter(
			final JdbcBatchItemWriter<ProjectVersionData> projectVersionsWriter) {
		this.projectVersionsWriter = projectVersionsWriter;
	}

}
