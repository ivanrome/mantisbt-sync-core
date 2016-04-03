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
package com.github.jrrdev.mantisbtsync.core.jobs.issues.readers;

import java.math.BigInteger;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.services.IssuesDao;

/**
 * Abtract issue reader.
 *
 * @author jrrdev
 *
 */
public abstract class AbstractIssuesReader implements ItemReader<IssueData> {

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Client stub generate by Apache Axis.
	 */
	private MantisConnectBindingStub clientStub;

	@Autowired
	private IssuesDao dao;

	/**
	 * Mantis username.
	 */
	private String userName;

	/**
	 * Pantis password.
	 */
	private String password;

	private BigInteger projectId;

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
	 * @return the dao
	 */
	public IssuesDao getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(final IssuesDao dao) {
		this.dao = dao;
	}

}
