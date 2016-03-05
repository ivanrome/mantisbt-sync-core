/**
 *
 */
package mantisbtsync.core.jobs.projects.tasklets;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.UserData;

/**
 * Tasklet calling mc_login to get the user_acces_level
 * (needed to obtain the users list).
 *
 * @author jdevarulrajah
 *
 */
public class MantisLoginTasklet implements Tasklet {

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
	 * Pantis password.
	 */
	private String password;

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

		final UserData data = clientStub.mc_login(userName, password);
		if (data != null && data.getAccess_level() != null) {
			chunkContext.getStepContext().getStepExecution().getExecutionContext()
			.put("mantis.acess_level", data.getAccess_level());
		}

		return RepeatStatus.FINISHED;
	}

}
