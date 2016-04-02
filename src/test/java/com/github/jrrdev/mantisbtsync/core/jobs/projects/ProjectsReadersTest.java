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
package com.github.jrrdev.mantisbtsync.core.jobs.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.JobExecution;
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

import com.github.jrrdev.mantisbtsync.core.Application;
import com.github.jrrdev.mantisbtsync.core.common.readers.AxisAuthItemsArrayReader;
import com.github.jrrdev.mantisbtsync.core.junit.JunitTestConfiguration;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.CustomFieldDefinitionData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;
import biz.futureware.mantis.rpc.soap.client.ProjectVersionData;

/**
 * @author jrrdev
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({Application.class, JunitTestConfiguration.class})
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
	StepScopeTestExecutionListener.class})
public class ProjectsReadersTest {

	@Autowired
	private AxisAuthItemsArrayReader<String> projectCategoriesReader;

	@Autowired
	private AxisAuthItemsArrayReader<CustomFieldDefinitionData> projectCustomFieldsReader;

	@Autowired
	private AxisAuthItemsArrayReader<AccountData> projectUsersReader;

	@Autowired
	private AxisAuthItemsArrayReader<ProjectVersionData> projectVersionsReader;

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

		final JobParameters jobParams = new JobParameters(map);

		final JobExecution exec = MetaDataInstanceFactory.createJobExecution(
				"testJob", 1L, 1L, jobParams);
		exec.getExecutionContext().put("mantis.acess_level", BigInteger.TEN);
		exec.getExecutionContext().put("mantis.loop.project_id", BigInteger.ONE);

		final StepExecution stepExecution = exec.createStepExecution("testStep");

		return stepExecution;
	}

	/**
	 * Test the reader for the table mantis_category_table.
	 *
	 * @throws Exception
	 * 			Technical Exception
	 */
	@Test
	public void testProjectCategoriesReader() throws Exception {
		final String[] expected = new String[] {"categorie_1", "categorie_2"};

		Mockito.when(clientStub.mc_project_get_categories("toto", "passwd", BigInteger.ONE))
		.thenReturn(expected);

		projectCategoriesReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final String item = projectCategoriesReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i], item);
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test the reader for the table mantis_category_table.
	 *
	 * @throws Exception
	 * 			Technical Exception
	 */
	@Test
	public void testProjectCustomFieldsReader() throws Exception {
		final CustomFieldDefinitionData field1 = new CustomFieldDefinitionData();
		field1.setField(new ObjectRef(BigInteger.ONE, "field_1"));
		final CustomFieldDefinitionData field2 = new CustomFieldDefinitionData();
		field2.setField(new ObjectRef(BigInteger.valueOf(2), "field_2"));

		final CustomFieldDefinitionData[] expected = new CustomFieldDefinitionData[] {field1, field2};

		Mockito.when(clientStub.mc_project_get_custom_fields("toto", "passwd", BigInteger.ONE))
		.thenReturn(expected);

		projectCustomFieldsReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final CustomFieldDefinitionData item = projectCustomFieldsReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getField().getId(), item.getField().getId());
				assertEquals(expected[i].getField().getName(), item.getField().getName());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test the reader for the table mantis_user_table.
	 *
	 * @throws Exception
	 * 			Technical Exception
	 */
	@Test
	public void testProjectUsersReader() throws Exception {
		final AccountData[] expected = new AccountData[]
				{new AccountData(BigInteger.ONE, "user_1", "user_real_1", "toto1@foo.fr"),
				new AccountData(BigInteger.valueOf(2), "user_2", "user_real_2", "toto2@foo.fr")};

		Mockito.when(clientStub.mc_project_get_users("toto", "passwd", BigInteger.ONE, BigInteger.TEN))
		.thenReturn(expected);

		projectUsersReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final AccountData item = projectUsersReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
				assertEquals(expected[i].getReal_name(), item.getReal_name());
				assertEquals(expected[i].getEmail(), item.getEmail());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * Test the reader for the table mantis_project_version_table.
	 *
	 * @throws Exception
	 * 			Technical Exception
	 */
	@Test
	public void testProjectVersionsReader() throws Exception {
		final ProjectVersionData[] expected = new ProjectVersionData[]
				{new ProjectVersionData(BigInteger.ONE, "version_1", BigInteger.ONE, null, null, null, null),
				new ProjectVersionData(BigInteger.valueOf(2), "version_2", BigInteger.ONE, null, null, null, null)};

		Mockito.when(clientStub.mc_project_get_versions("toto", "passwd", BigInteger.ONE))
		.thenReturn(expected);

		projectVersionsReader.setClientStub(clientStub);

		for (int i = 0; i <= expected.length; i++) {
			final ProjectVersionData item = projectVersionsReader.read();
			if (i < expected.length) {
				assertNotNull(item);
				assertEquals(expected[i].getId(), item.getId());
				assertEquals(expected[i].getName(), item.getName());
				assertEquals(expected[i].getProject_id(), item.getProject_id());
			} else {
				assertNull(item);
			}
		}
	}

	/**
	 * @return the projectCategoriesReader
	 */
	public AxisAuthItemsArrayReader<String> getProjectCategoriesReader() {
		return projectCategoriesReader;
	}

	/**
	 * @param projectCategoriesReader the projectCategoriesReader to set
	 */
	public void setProjectCategoriesReader(
			final AxisAuthItemsArrayReader<String> projectCategoriesReader) {
		this.projectCategoriesReader = projectCategoriesReader;
	}

	/**
	 * @return the projectCustomFieldsReader
	 */
	public AxisAuthItemsArrayReader<CustomFieldDefinitionData> getProjectCustomFieldsReader() {
		return projectCustomFieldsReader;
	}

	/**
	 * @param projectCustomFieldsReader the projectCustomFieldsReader to set
	 */
	public void setProjectCustomFieldsReader(
			final AxisAuthItemsArrayReader<CustomFieldDefinitionData> projectCustomFieldsReader) {
		this.projectCustomFieldsReader = projectCustomFieldsReader;
	}

	/**
	 * @return the projectUsersReader
	 */
	public AxisAuthItemsArrayReader<AccountData> getProjectUsersReader() {
		return projectUsersReader;
	}

	/**
	 * @param projectUsersReader the projectUsersReader to set
	 */
	public void setProjectUsersReader(
			final AxisAuthItemsArrayReader<AccountData> projectUsersReader) {
		this.projectUsersReader = projectUsersReader;
	}

	/**
	 * @return the projectVersionsReader
	 */
	public AxisAuthItemsArrayReader<ProjectVersionData> getProjectVersionsReader() {
		return projectVersionsReader;
	}

	/**
	 * @param projectVersionsReader the projectVersionsReader to set
	 */
	public void setProjectVersionsReader(
			final AxisAuthItemsArrayReader<ProjectVersionData> projectVersionsReader) {
		this.projectVersionsReader = projectVersionsReader;
	}

	/**
	 * @return the clientStub
	 */
	public MantisConnectBindingStub getClientStub() {
		return clientStub;
	}

	/**
	 * @param clientStub the clientStub to set
	 */
	public void setClientStub(final MantisConnectBindingStub clientStub) {
		this.clientStub = clientStub;
	}

}
