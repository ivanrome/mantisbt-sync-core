/**
 *
 */
package mantisbtsync.core.jobs.enums;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mantisbtsync.core.jobs.enums.EnumsWritersConfiguration;
import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
@SpringApplicationConfiguration({EnumsWritersConfiguration.class})
public class EnumsWritersTest extends AbstractSqlWriterTest {

	@Autowired
	private ItemWriter<ObjectRef> customFieldTypesWriter;

	@Autowired
	private ItemWriter<ObjectRef> etasWriter;

	@Autowired
	private ItemWriter<ObjectRef> prioritiesWriter;

	@Autowired
	private ItemWriter<ObjectRef> projectionsWriter;

	@Autowired
	private ItemWriter<ObjectRef> projectStatusWriter;

	@Autowired
	private ItemWriter<ObjectRef> projectViewStatesWriter;

	@Autowired
	private ItemWriter<ObjectRef> reproducibilitiesWriter;

	@Autowired
	private ItemWriter<ObjectRef> resolutionsWriter;

	@Autowired
	private ItemWriter<ObjectRef> severitiesWriter;


	/**
	 * Test for the writer of the table mantis_enum_custom_field_types.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testCustomFieldTypesWriter() throws Exception {

		final String tableName = "mantis_enum_custom_field_types";

		insertInitialData(tableName);
		customFieldTypesWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_etas.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testEtasWriter() throws Exception {
		final String tableName = "mantis_enum_etas";

		insertInitialData(tableName);
		etasWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_priorities.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testPrioritiesWriter() throws Exception {
		final String tableName = "mantis_enum_priorities";

		insertInitialData(tableName);
		prioritiesWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_projections.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testProjectionsWriter() throws Exception {
		final String tableName = "mantis_enum_projections";

		insertInitialData(tableName);
		projectionsWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_project_status.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testProjectStatusWriter() throws Exception {
		final String tableName = "mantis_enum_project_status";

		insertInitialData(tableName);
		projectStatusWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_project_view_states.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testProjectViewStatesWriter() throws Exception {
		final String tableName = "mantis_enum_project_view_states";

		insertInitialData(tableName);
		projectViewStatesWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_reproducibilities.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testReproducibilitiesWriter() throws Exception {
		final String tableName = "mantis_enum_reproducibilities";

		insertInitialData(tableName);
		reproducibilitiesWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_resolutions.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testResolutionsWriter() throws Exception {
		final String tableName = "mantis_enum_resolutions";

		insertInitialData(tableName);
		resolutionsWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}

	/**
	 * Test for the writer of the table mantis_enum_severities.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testSeveritiesWriter() throws Exception {
		final String tableName = "mantis_enum_severities";

		insertInitialData(tableName);
		severitiesWriter.write(buildItems());
		final List<ObjectRef> resultItems = getItemsFromDb(tableName);
		checkResults(resultItems);
	}




	/**
	 * Set the customFieldTypesWriter
	 * @param customFieldTypesWriter
	 * 			The customFieldTypesWriter to set
	 */
	public void setCustomFieldTypesWriter(
			final ItemWriter<ObjectRef> customFieldTypesWriter) {
		this.customFieldTypesWriter = customFieldTypesWriter;
	}

	/**
	 * @param etasWriter the etasWriter to set
	 */
	public void setEtasWriter(final ItemWriter<ObjectRef> etasWriter) {
		this.etasWriter = etasWriter;
	}


	/**
	 * @param prioritiesWriter the prioritiesWriter to set
	 */
	public void setPrioritiesWriter(final ItemWriter<ObjectRef> prioritiesWriter) {
		this.prioritiesWriter = prioritiesWriter;
	}


	/**
	 * @param projectionsWriter the projectionsWriter to set
	 */
	public void setProjectionsWriter(final ItemWriter<ObjectRef> projectionsWriter) {
		this.projectionsWriter = projectionsWriter;
	}


	/**
	 * @param projectStatusWriter the projectStatusWriter to set
	 */
	public void setProjectStatusWriter(
			final ItemWriter<ObjectRef> projectStatusWriter) {
		this.projectStatusWriter = projectStatusWriter;
	}


	/**
	 * @param projectViewStatesWriter the projectViewStatesWriter to set
	 */
	public void setProjectViewStatesWriter(
			final ItemWriter<ObjectRef> projectViewStatesWriter) {
		this.projectViewStatesWriter = projectViewStatesWriter;
	}


	/**
	 * @param reproducibilitiesWriter the reproducibilitiesWriter to set
	 */
	public void setReproducibilitiesWriter(
			final ItemWriter<ObjectRef> reproducibilitiesWriter) {
		this.reproducibilitiesWriter = reproducibilitiesWriter;
	}


	/**
	 * @param resolutionsWriter the resolutionsWriter to set
	 */
	public void setResolutionsWriter(final ItemWriter<ObjectRef> resolutionsWriter) {
		this.resolutionsWriter = resolutionsWriter;
	}


	/**
	 * @param severitiesWriter the severitiesWriter to set
	 */
	public void setSeveritiesWriter(final ItemWriter<ObjectRef> severitiesWriter) {
		this.severitiesWriter = severitiesWriter;
	}


	/**
	 * Build the items to write.
	 *
	 * @return items to write
	 */
	private List<ObjectRef> buildItems() {
		final List<ObjectRef> items = new ArrayList<ObjectRef>();
		final ObjectRef item1 = new ObjectRef(BigInteger.valueOf(1), "new_value_1");
		final ObjectRef item2 = new ObjectRef(BigInteger.valueOf(3), "new_value_3");
		items.add(item1);
		items.add(item2);

		return items;
	}

	/**
	 * Insert data for the tests
	 *
	 * @param tableName
	 * 			Nom de la table
	 */
	private void insertInitialData(final String tableName) {
		final Operation op = insertInto(tableName)
				.columns("id", "name")
				.values(1, "old_value_1")
				.values(2, "old_value_2")
				.build();

		lauchOperation(op);

		// Check if insertion is ok
		assertEquals(Integer.valueOf(2), getJdbcTemplate()
				.queryForObject("SELECT count(*) FROM " + tableName, Integer.class));

	}

	/**
	 * Get the items from the database
	 *
	 * @param tableName
	 * 			Name of the target table
	 * @return items in the DB
	 */
	private List<ObjectRef> getItemsFromDb(final String tableName) {
		return getJdbcTemplate()
				.query("SELECT id, name FROM " + tableName,
						new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));
	}

	/**
	 * Check if the results are OK
	 *
	 * @param resultItems
	 * 			Items in the DB
	 */
	private void checkResults(final List<ObjectRef> resultItems) {
		assertEquals(3, resultItems.size());
		for (final ObjectRef item : resultItems) {
			assertNotNull(item.getId());
			if (item.getId().intValue() == 1 || item.getId().intValue() == 3) {
				assertEquals("new_value_" + item.getId(), item.getName());
			} else if (item.getId().intValue() == 2) {
				assertEquals("old_value_2", item.getName());
			} else {
				fail("Item not expected : [id=" + item.getId()
						+ ", name=" + item.getName() + "]");
			}
		}
	}
}
