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
package com.github.jrrdev.mantisbtsync.core.jobs.issues.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;

import com.github.jrrdev.mantisbtsync.core.services.IssuesDao;

/**
 * Step listener used to evict all caches related to issue dependencies
 * inserts into DB.
 * Those caches are used when writing an issue to not perform insert
 * if an enum values already exists in the DB.
 * The listener will evict all cached if the step failed (because it means
 * that an insert rollback may have been performed and so data in the cache
 * and in the DB aren't synced).
 *
 * @author jrrdev
 *
 */
@CacheConfig
public class CacheEvictionListener implements StepExecutionListener {

	/**
	 * The DAO using the caches to evict.
	 */
	@Autowired
	private IssuesDao dao;

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public void beforeStep(final StepExecution stepExecution) {
		// Nothing to do here
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		if(ExitStatus.FAILED.getExitCode().equals(stepExecution.getExitStatus().getExitCode())) {
			dao.evictAllCaches();
		}
		return stepExecution.getExitStatus();
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
