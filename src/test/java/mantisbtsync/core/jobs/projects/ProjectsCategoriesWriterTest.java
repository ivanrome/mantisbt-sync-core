/**
 *
 */
package mantisbtsync.core.jobs.projects;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import mantisbtsync.core.jobs.projects.beans.ProjectCategoryBean;
import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
public class ProjectsCategoriesWriterTest extends AbstractSqlWriterTest {

	@Autowired
	JdbcBatchItemWriter<ProjectCategoryBean> projectCategoriesWriter;

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

		final Operation opProject = insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build();
		lauchOperation(opProject);

		final Operation opCategorie = insertInto("mantis_category_table")
				.columns("id", "name", "project_id")
				.values(1, "old_categorie_1", 1)
				.build();

		lauchOperation(opCategorie);

		projectCategoriesWriter.write(buildItems());

		final List<ProjectCategoryBean> results = getItemsFromDb();
		assertEquals(2, results.size());

		for (final ProjectCategoryBean bean : results) {
			assertEquals(Integer.valueOf(1), bean.getProjectId());
			if (bean.getId() == 1) {
				assertEquals("old_categorie_1", bean.getName());
			} else {
				assertEquals(Integer.valueOf(2), bean.getId());
				assertEquals("new_categorie_2", bean.getName());
			}
		}
	}

	/**
	 * Build the items to write.
	 *
	 * @return items
	 */
	private List<ProjectCategoryBean> buildItems() {
		final List<ProjectCategoryBean> items = new ArrayList<ProjectCategoryBean>();

		final ProjectCategoryBean item1 = new ProjectCategoryBean();
		item1.setProjectId(1);
		item1.setName("old_categorie_1");

		final ProjectCategoryBean item2 = new ProjectCategoryBean();
		item2.setProjectId(1);
		item2.setName("new_categorie_2");

		items.add(item1);
		items.add(item2);

		return items;
	}

	/**
	 * Get the items from the database
	 *
	 * @param tableName
	 * 			Name of the target table
	 * @return items in the DB
	 */
	private List<ProjectCategoryBean> getItemsFromDb() {
		return getJdbcTemplate()
				.query("SELECT id, name, project_id FROM mantis_category_table",
						new BeanPropertyRowMapper<ProjectCategoryBean>(ProjectCategoryBean.class));
	}

	/**
	 * @return the projectCategoriesWriter
	 */
	public final JdbcBatchItemWriter<ProjectCategoryBean> getProjectCategoriesWriter() {
		return projectCategoriesWriter;
	}

	/**
	 * @param projectCategoriesWriter the projectCategoriesWriter to set
	 */
	public final void setProjectCategoriesWriter(
			final JdbcBatchItemWriter<ProjectCategoryBean> projectCategoriesWriter) {
		this.projectCategoriesWriter = projectCategoriesWriter;
	}

}
