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
package mantisbtsync.core.jobs.issues;

import mantisbtsync.core.jobs.issues.tasklets.IssuesLastRunExtractorTasklet;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jdevarulrajah
 *
 */
@Configuration
public class IssuesTaskletsConfiguration {

	@Bean
	@StepScope
	public IssuesLastRunExtractorTasklet mantisLastRunExtractorTasklet(final JobExplorer jobExplorer) {
		final IssuesLastRunExtractorTasklet tasklet = new IssuesLastRunExtractorTasklet();
		tasklet.setJobExplorer(jobExplorer);
		return tasklet;
	}

	@Bean
	@StepScope
	public ExecutionContextPromotionListener mantisLastRunExtractorPromotionListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"mantis.update.last_job_run"});
		return listener;
	}

}
