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
package mantisbtsync.core.jobs.issues.readers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.Assert;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * @author jdevarulrajah
 *
 */
public class IssuesReader implements ItemReader<IssueData> {

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Client stub generate by Apache Axis.
	 */
	private MantisConnectBindingStub clientStub;

	private int currentPage = 0;

	/**
	 * Index of the last read item.
	 */
	private int i = -1;

	/**
	 * Results array got from the WS.
	 */
	private IssueData[] items;

	private static final BigInteger PAGE_SIZE = BigInteger.valueOf(20);

	/**
	 * Mantis username.
	 */
	private String userName;

	/**
	 * Pantis password.
	 */
	private String password;

	private Calendar lastJobRun = null;

	private BigInteger projectId;

	@Override
	public IssueData read() throws Exception, UnexpectedInputException,
	ParseException, NonTransientResourceException {

		Assert.notNull(clientStub);

		// If auth manager is set, try to get the cookie
		if (authManager != null && authManager.getAuthCookie() != null) {
			clientStub._setProperty(HTTPConstants.HEADER_COOKIE,
					authManager.getAuthCookie());
		}

		if (i < 0 || i >= (items.length - 1)) {
			currentPage++;
			i = -1;
			items = clientStub.mc_project_get_issues(userName, password, projectId,
					BigInteger.valueOf(currentPage), PAGE_SIZE);
		}

		i++;
		if (items != null && i < items.length) {
			final IssueData item = items[i];

			if (item != null
					&& (lastJobRun == null
					|| item.getLast_updated() == null
					|| item.getLast_updated().after(lastJobRun))) {

				return item;

			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * @return the authManager
	 */
	public PortalAuthManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param authManager the authManager to set
	 */
	public void setAuthManager(final PortalAuthManager authManager) {
		this.authManager = authManager;
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
	 * @return the lastJobRun
	 */
	public Calendar getLastJobRun() {
		return lastJobRun;
	}

	/**
	 * @param pLastJobRun the lastJobRun to set
	 */
	public void setLastJobRun(final Date pLastJobRun) {
		if (pLastJobRun != null) {
			lastJobRun = Calendar.getInstance();
			lastJobRun.setTime(pLastJobRun);
		} else {
			lastJobRun = null;
		}
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
}
