/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 J�rard Devarulrajah
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
package mantisbtsync.core.jobs.projects.tasklets;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.ProjectData;

/**
 * Tasklet to read the projects list.
 *
 * @author jdevarulrajah
 *
 */
public class ProjectsListTasklet implements Tasklet {

	/**
	 * SQL request to merge data into the table mantis_project_table.
	 */
	private static final String MERGE_PROJECT_TABLE =
			"MERGE INTO mantis_project_table dest\n"
					+ " USING (SELECT ? as id, ? as name FROM dual) src\n"
					+ " ON (dest.id = src.id)\n"
					+ " WHEN NOT MATCHED THEN INSERT (id, name) VALUES (src.id, src.name)\n"
					+ " WHEN MATCHED THEN UPDATE SET dest.name = src.name";

	/**
	 * SQL request to merge data into the table mantis_project_hierarchy_table.
	 */
	private static final String MERGE_PROJECT__HIERARCHY_TABLE =
			"MERGE INTO mantis_project_hierarchy_table dest\n"
					+ " USING (SELECT ? as parent_id, ? as child_id FROM dual) src\n"
					+ " ON (dest.parent_id = src.parent_id AND dest.child_id = src.child_id)\n"
					+ " WHEN NOT MATCHED THEN INSERT (parent_id, child_id) VALUES (src.parent_id, src.child_id)";

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

		final ProjectData[] projects = clientStub.mc_projects_get_user_accessible(userName, password);
		final Set<BigInteger> projectsId = new HashSet<BigInteger>();
		final int n = projects.length;

		for (int i = 0; i < n; i++) {
			recursiveFilter(projects[i], projectsId);
		}

		chunkContext.getStepContext().getStepExecution().getExecutionContext()
		.put("mantis.loop.projects_to_process", projectsId);

		return RepeatStatus.FINISHED;
	}

	private void recursiveFilter(final ProjectData project, final Set<BigInteger> projectsId) {
		if (projectId == null || projectId.equals(project.getId())) {
			insertIntoDb(project, projectsId, null);
		} else {
			final ProjectData[] childs = project.getSubprojects();
			if (childs != null) {
				final int n = childs.length;
				for (int i = 0; i < n; i++) {
					recursiveFilter(childs[i], projectsId);
				}
			}
		}
	}

	private void insertIntoDb(final ProjectData project, final Set<BigInteger> projectsId,
			final BigInteger parentProject) {

		jdbcTemplate.update(MERGE_PROJECT_TABLE, project.getId(), project.getName());
		if (parentProject != null) {
			jdbcTemplate.update(MERGE_PROJECT__HIERARCHY_TABLE, parentProject, project.getId());
		}

		projectsId.add(project.getId());

		final ProjectData[] childs = project.getSubprojects();
		if (childs != null) {
			final int n = childs.length;
			for (int i = 0; i < n; i++) {
				insertIntoDb(childs[i], projectsId, project.getId());
			}
		}
	}

}
