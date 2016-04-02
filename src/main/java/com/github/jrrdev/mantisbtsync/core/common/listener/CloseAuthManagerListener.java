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
package com.github.jrrdev.mantisbtsync.core.common.listener;

import java.io.IOException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;

/**
 * Job listener executing AuthManager closing at the end of the job.
 *
 * @author jdevarulrajah
 *
 */
public class CloseAuthManagerListener implements JobExecutionListener {

	private PortalAuthManager mgr;

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.JobExecutionListener#beforeJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void beforeJob(final JobExecution jobExecution) {
		//Do nothing
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.JobExecutionListener#afterJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void afterJob(final JobExecution jobExecution) {
		if (mgr != null) {
			try {
				mgr.close();
			} catch (final IOException e) {
				jobExecution.addFailureException(e);
			}
		}
	}

	/**
	 * @return the mgr
	 */
	public PortalAuthManager getMgr() {
		return mgr;
	}

	/**
	 * @param mgr the mgr to set
	 */
	public void setMgr(final PortalAuthManager mgr) {
		this.mgr = mgr;
	}

}
