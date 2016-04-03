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
package com.github.jrrdev.mantisbtsync.core.jobs.issues;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.jobs.issues.tasklets.IssuesLastRunExtractorTasklet;

/**
 * Configuration for the tasklets used to sync issues.
 *
 * @author jrrdev
 *
 */
@Configuration
public class IssuesTaskletsConfiguration {

	/**
	 * Tasklet extracting the last sucessful run of the job with the same parameters.
	 *
	 * @param jobExplorer
	 * 			The job explorer
	 * @return the tasklet
	 */
	@Bean
	@StepScope
	public IssuesLastRunExtractorTasklet mantisLastRunExtractorTasklet(final JobExplorer jobExplorer) {
		final IssuesLastRunExtractorTasklet tasklet = new IssuesLastRunExtractorTasklet();
		tasklet.setJobExplorer(jobExplorer);
		return tasklet;
	}

	/**
	 * Execution context promotion listener that promotes mantis.update.last_job_run and
	 * mantis.update.current_job_run to the job context.
	 *
	 * @return the execution context promotion listener
	 */
	@Bean
	@StepScope
	public ExecutionContextPromotionListener mantisLastRunExtractorPromotionListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"mantis.update.last_job_run", "mantis.update.current_job_run"});
		return listener;
	}

}
