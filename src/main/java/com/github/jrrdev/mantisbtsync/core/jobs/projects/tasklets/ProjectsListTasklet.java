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
package com.github.jrrdev.mantisbtsync.core.jobs.projects.tasklets;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * Tasklet to read the projects list.
 *
 * @author jrrdev
 *
 */
public class ProjectsListTasklet implements Tasklet {

	/**
	 * SQL request to merge data into the table mantis_project_table.
	 */
	private static final String MERGE_PROJECT_TABLE =
			"INSERT INTO mantis_project_table (id)\n"
					+ " VALUES (?)\n"
					+ " ON DUPLICATE KEY UPDATE name = name";

	/**
	 * SQL request to merge data into the table mantis_project_hierarchy_table.
	 */
	private static final String MERGE_PROJECT__HIERARCHY_TABLE =
			"INSERT INTO mantis_project_hierarchy_table (parent_id, child_id)\n"
					+ " VALUES (?, ?)\n"
					+ " ON DUPLICATE KEY UPDATE child_id = child_id";

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Apache Axis stub.
	 */
	private MantisConnectBindingStub clientStub;

	/**
	 * Mantis username.
	 */
	private String userName;

	/**
	 * Mantis password.
	 */
	private String password;

	/**
	 * Id of master project.
	 */
	private BigInteger projectId;

	/**
	 * JDBC template.
	 */
	private JdbcTemplate jdbcTemplate;

	/**
	 * @return the authManager
	 */
	public final PortalAuthManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param authManager the authManager to set
	 */
	public final void setAuthManager(final PortalAuthManager authManager) {
		this.authManager = authManager;
	}

	/**
	 * @return the clientStub
	 */
	public final MantisConnectBindingStub getClientStub() {
		return clientStub;
	}

	/**
	 * @param clientStub the clientStub to set
	 */
	public final void setClientStub(final MantisConnectBindingStub clientStub) {
		this.clientStub = clientStub;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the projectId
	 */
	public BigInteger getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(final BigInteger projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(final StepContribution contribution,
			final ChunkContext chunkContext) throws Exception {

		Assert.notNull(clientStub);

		// Si on a renseigné un authManager, on cherche à récupérer le cookie
		if (authManager != null && authManager.getAuthCookie() != null) {
			clientStub._setProperty(HTTPConstants.HEADER_COOKIE,
					authManager.getAuthCookie());
		}

		final Set<BigInteger> projectsId = new HashSet<BigInteger>();
		projectsId.add(projectId);
		insertIntoDb(projectId, null);
		searchAndInsertSubProject(projectId, projectsId);

		chunkContext.getStepContext().getStepExecution().getExecutionContext()
		.put("mantis.loop.projects_to_process", projectsId);

		return RepeatStatus.FINISHED;
	}

	private void searchAndInsertSubProject(final BigInteger projectId,
			final Set<BigInteger> projectsId) throws RemoteException {

		final String[] idStrList = clientStub.mc_project_get_all_subprojects(userName, password, projectId);
		for (final String idStr : idStrList) {
			final BigInteger subProjectId = BigInteger.valueOf(Long.parseLong(idStr));
			projectsId.add(subProjectId);

			insertIntoDb(subProjectId, projectId);
			searchAndInsertSubProject(subProjectId, projectsId);
		}
	}

	private void insertIntoDb(final BigInteger projectId, final BigInteger parentProjectId) {

		jdbcTemplate.update(MERGE_PROJECT_TABLE, projectId);
		if (parentProjectId != null) {
			jdbcTemplate.update(MERGE_PROJECT__HIERARCHY_TABLE, parentProjectId, projectId);
		}
	}

}
