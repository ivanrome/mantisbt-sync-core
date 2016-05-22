/**
 *
 */
package com.github.jrrdev.mantisbtsync.core.common.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.test.JobScopeTestExecutionListener;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.jrrdev.mantisbtsync.core.Application;
import com.github.jrrdev.mantisbtsync.core.junit.JunitTestConfiguration;

/**
 * @author jrrdev
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({Application.class, JunitTestConfiguration.class})
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
	StepScopeTestExecutionListener.class, JobScopeTestExecutionListener.class })
public class PortalAuthManagerTest {

	@Autowired
	private PortalAuthManager authManager;

	@Test
	public void test() throws Exception {
		assertNotNull(authManager);
		assertNotNull(authManager.getFirstRequest());

		final ExitStatus status = authManager.authentificate();
		assertEquals(ExitStatus.COMPLETED, status);
		assertNotNull(authManager.getAuthCookie());

		authManager.close();
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
}
