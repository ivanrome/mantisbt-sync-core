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
package mantisbtsync.core.jobs.projects;

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
