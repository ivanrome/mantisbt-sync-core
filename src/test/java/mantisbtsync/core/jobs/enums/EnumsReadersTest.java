/**
 *
 */
package mantisbtsync.core.jobs.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import mantisbtsync.core.Application;
import mantisbtsync.core.common.readers.AxisAuthItemsArrayReader;
import mantisbtsync.core.junit.JunitTestConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * @author jdevarulrajah
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({Application.class, JunitTestConfiguration.class})
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
	StepScopeTestExecutionListener.class })
public class EnumsReadersTest {

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> customFieldTypesReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> etasReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> prioritiesReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> projectionsReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> projectStatusReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> projectViewStatesReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> reproducibilitiesReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> resolutionsReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> severitiesReader;

	@Autowired
	private AxisAuthItemsArrayReader<ObjectRef> statusReader;

	@Mock
	private MantisConnectBindingStub clientStub;


	@Before
	public void setUpBefore() {
		MockitoAnnotations.initMocks(this);
	}

	public StepExecution getStepExecution() {

		final Map<String, JobParameter> map = new HashMap<String, JobParameter>();
		map.put("mantis.username", new JobParameter("toto"));
		map.put("mantis.password", new JobParameter("passwd"));

		final StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(new JobParameters(map));

		return stepExecution;
	}


	/**
	 * Test for the reader of the table mantis_enum_custom_field_types.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testCustomFieldTypesReader() throws Exception {

		final ObjectRef[] expected = generateItems("mc_enum_custom_field_types");

		Mockito.when(clientStub.mc_enum_custom_field_types("toto", "passwd"))
		.thenReturn(expected);

		customFieldTypesReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = customFieldTypesReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_etas.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testEtasReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_etas");

		Mockito.when(clientStub.mc_enum_etas("toto", "passwd"))
		.thenReturn(expected);

		etasReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = etasReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_priorities.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testPrioritiesReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_priorities");

		Mockito.when(clientStub.mc_enum_priorities("toto", "passwd"))
		.thenReturn(expected);

		prioritiesReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = prioritiesReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_projections.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testProjectionsReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_projections");

		Mockito.when(clientStub.mc_enum_projections("toto", "passwd"))
		.thenReturn(expected);

		projectionsReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = projectionsReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_project_status.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testProjectStatusReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_project_status");

		Mockito.when(clientStub.mc_enum_project_status("toto", "passwd"))
		.thenReturn(expected);

		projectStatusReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = projectStatusReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_project_view_states.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testProjectViewStatesReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_project_view_states");

		Mockito.when(clientStub.mc_enum_project_view_states("toto", "passwd"))
		.thenReturn(expected);

		projectViewStatesReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = projectViewStatesReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_reproducibilities.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testReproducibilitiesReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_reproducibilities");

		Mockito.when(clientStub.mc_enum_reproducibilities("toto", "passwd"))
		.thenReturn(expected);

		reproducibilitiesReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = reproducibilitiesReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_resolutions.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testResolutionsReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_resolutions");

		Mockito.when(clientStub.mc_enum_resolutions("toto", "passwd"))
		.thenReturn(expected);

		resolutionsReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = resolutionsReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_severities.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testSeveritiesReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_severities");

		Mockito.when(clientStub.mc_enum_severities("toto", "passwd"))
		.thenReturn(expected);

		severitiesReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = severitiesReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test for the reader of the table mantis_enum_status.
	 *
	 * @throws Exception
	 * 			Technical exception
	 */
	@Test
	public void testStatusReader() throws Exception {
		final ObjectRef[] expected = generateItems("mc_enum_status");

		Mockito.when(clientStub.mc_enum_status("toto", "passwd"))
		.thenReturn(expected);

		statusReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ObjectRef item = statusReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * @param customFieldTypesReader the customFieldTypesReader to set
	 */
	public void setCustomFieldTypesReader(
			final AxisAuthItemsArrayReader<ObjectRef> customFieldTypesReader) {
		this.customFieldTypesReader = customFieldTypesReader;
	}

	/**
	 * @param etasReader the etasReader to set
	 */
	public void setEtasReader(final AxisAuthItemsArrayReader<ObjectRef> etasReader) {
		this.etasReader = etasReader;
	}

	/**
	 * @param prioritiesReader the prioritiesReader to set
	 */
	public void setPrioritiesReader(final AxisAuthItemsArrayReader<ObjectRef> prioritiesReader) {
		this.prioritiesReader = prioritiesReader;
	}

	/**
	 * @param projectionsReader the projectionsReader to set
	 */
	public void setProjectionsReader(final AxisAuthItemsArrayReader<ObjectRef> projectionsReader) {
		this.projectionsReader = projectionsReader;
	}

	/**
	 * @param projectStatusReader the projectStatusReader to set
	 */
	public void setProjectStatusReader(
			final AxisAuthItemsArrayReader<ObjectRef> projectStatusReader) {
		this.projectStatusReader = projectStatusReader;
	}

	/**
	 * @param projectViewStatesReader the projectViewStatesReader to set
	 */
	public void setProjectViewStatesReader(
			final AxisAuthItemsArrayReader<ObjectRef> projectViewStatesReader) {
		this.projectViewStatesReader = projectViewStatesReader;
	}

	/**
	 * @param reproducibilitiesReader the reproducibilitiesReader to set
	 */
	public void setReproducibilitiesReader(
			final AxisAuthItemsArrayReader<ObjectRef> reproducibilitiesReader) {
		this.reproducibilitiesReader = reproducibilitiesReader;
	}

	/**
	 * @param resolutionsReader the resolutionsReader to set
	 */
	public void setResolutionsReader(final AxisAuthItemsArrayReader<ObjectRef> resolutionsReader) {
		this.resolutionsReader = resolutionsReader;
	}

	/**
	 * @param severitiesReader the severitiesReader to set
	 */
	public void setSeveritiesReader(final AxisAuthItemsArrayReader<ObjectRef> severitiesReader) {
		this.severitiesReader = severitiesReader;
	}

	/**
	 * @param statusReader the statusReader to set
	 */
	public void setStatusReader(final AxisAuthItemsArrayReader<ObjectRef> statusReader) {
		this.statusReader = statusReader;
	}

	/**
	 * @param clientStub the clientStub to set
	 */
	public void setClientStub(final MantisConnectBindingStub clientStub) {
		this.clientStub = clientStub;
	}


	/**
	 * Generate items for the tests.
	 *
	 * @param operation
	 * 			Operation name
	 * @return items
	 */
	private ObjectRef[] generateItems(final String operation) {
		final ObjectRef[] items = new ObjectRef[] {
				new ObjectRef(BigInteger.valueOf(1), operation + "_1"),
				new ObjectRef(BigInteger.valueOf(2), operation + "_2")};
		return items;
	}

}
