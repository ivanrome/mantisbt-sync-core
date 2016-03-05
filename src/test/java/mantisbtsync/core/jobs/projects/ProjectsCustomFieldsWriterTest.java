/**
 *
 */
package mantisbtsync.core.jobs.projects;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import mantisbtsync.core.jobs.projects.beans.ProjectCustomFieldBean;
import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
public class ProjectsCustomFieldsWriterTest extends AbstractSqlWriterTest {

	@Autowired
	CompositeItemWriter<ProjectCustomFieldBean> projectCustomFieldsWriter;

	@Test
	public void test() throws Exception {

		lauchOperation(deleteAllFrom("mantis_project_user_list_table",
				"mantis_user_table",
				"mantis_custom_field_project_table",
				"mantis_custom_field_table",
				"mantis_enum_custom_field_types",
				"mantis_project_hierarchy_table",
				"mantis_category_table",
				"mantis_project_version_table",
				"mantis_project_table"));

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_enum_custom_field_types")
				.columns("id", "name")
				.values(1, "type_1")
				.build(),

				insertInto("mantis_custom_field_table")
				.columns("id", "name", "type_id")
				.values(1, "old_field_1", 1)
				.build(),

				insertInto("mantis_custom_field_project_table")
				.columns("field_id", "project_id")
				.values(1, 1)
				.build());

		lauchOperation(op);

		projectCustomFieldsWriter.write(buildItems());

		final List<ProjectCustomFieldBean> results = getJdbcTemplate().query("SELECT cf.id, cf.name, cfp.project_id"
				+ " FROM mantis_custom_field_table cf"
				+ " INNER JOIN mantis_custom_field_project_table cfp ON cf.id = cfp.field_id",
				new BeanPropertyRowMapper<ProjectCustomFieldBean>(ProjectCustomFieldBean.class));

		assertEquals(2, results.size());

		for (final ProjectCustomFieldBean item : results) {
			assertEquals(Integer.valueOf(1), item.getProjectId());
			if (item.getId() == 1) {
				assertEquals("new_field_1", item.getName());
			} else {
				assertEquals(Integer.valueOf(2), item.getId());
				assertEquals("new_field_2", item.getName());
			}
		}
	}

	/**
	 * Build the items to write.
	 *
	 * @return items
	 */
	private List<ProjectCustomFieldBean> buildItems() {
		final List<ProjectCustomFieldBean> items = new ArrayList<ProjectCustomFieldBean>();

		final ProjectCustomFieldBean item1 = new ProjectCustomFieldBean();
		item1.setId(1);
		item1.setName("new_field_1");
		item1.setProjectId(1);
		item1.setTypeId(1);

		final ProjectCustomFieldBean item2 = new ProjectCustomFieldBean();
		item2.setId(2);
		item2.setName("new_field_2");
		item2.setProjectId(1);
		item2.setTypeId(1);

		items.add(item1);
		items.add(item2);

		return items;
	}

	/**
	 * @return the projectCustomFieldsWriter
	 */
	public final CompositeItemWriter<ProjectCustomFieldBean> getProjectCustomFieldsWriter() {
		return projectCustomFieldsWriter;
	}

	/**
	 * @param projectCustomFieldsWriter the projectCustomFieldsWriter to set
	 */
	public final void setProjectCustomFieldsWriter(
			final CompositeItemWriter<ProjectCustomFieldBean> projectCustomFieldsWriter) {
		this.projectCustomFieldsWriter = projectCustomFieldsWriter;
	}

}
