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
package com.github.jrrdev.mantisbtsync.core.jobs.issues.tasklets;

import java.util.Calendar;
import java.util.Date;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.jrrdev.mantisbtsync.core.services.IssuesDao;

/**
 * Tasklet launching the computing of the number of issues by project,
 * handler and status.
 *
 * @author jrrdev
 *
 */
public class HandlersStatTasklet implements Tasklet {

	@Autowired
	private IssuesDao dao;

	/**
	 * Computing date of stats.
	 */
	private Date computeDate;

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(final StepContribution contribution,
			final ChunkContext chunkContext) throws Exception {

		if (computeDate != null) {
			final Calendar date = Calendar.getInstance();
			date.setTimeInMillis(computeDate.getTime());

			dao.computeHandlersStat(date);
		}

		return RepeatStatus.FINISHED;
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

	/**
	 * @return the computeDate
	 */
	public Date getComputeDate() {
		return computeDate;
	}

	/**
	 * @param computeDate the computeDate to set
	 */
	public void setComputeDate(final Date computeDate) {
		this.computeDate = computeDate;
	}

}
