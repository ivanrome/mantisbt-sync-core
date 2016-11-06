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
package com.github.jrrdev.mantisbtsync.core.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.IssueData;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.common.listener.CloseAuthManagerListener;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugIdBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.listener.CacheEvictionListener;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.processors.IssuesProcessor;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.readers.OpenIssuesReader;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.readers.OtherIssuesReader;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.tasklets.HandlersStatTasklet;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.tasklets.IssuesLastRunExtractorTasklet;

/**
 * Configuration for the jobs to sync MantisBT enumerations.
 * Several jobs are available :
 * - syncIssuesJob
 * - forceSyncIssuesJob
 * - fileSyncIssuesJob
 *
 * @author jrrdev
 *
 */
@Configuration
public class JobIssuesConfiguration {

	/**
	 * Build the syncIssuesJob job. This job will update all modified issues which
	 * are still marked as open is MantisBT or in the local DB. The update is perform
	 * in differential mode since last successful execution of this job for the given project.
	 *
	 * Note that all issues related to a subproject are updated to.
	 *
	 * Parameters for this job are :
	 * 	- mantis.username
	 * 		MantisBT username. If anonymous access is used, should be an empty string.
	 *  - mantis.password
	 *  	MantisBT password. If anonymous access is used, should be an empty string.
	 *  - mantis.project_id
	 *  	The id of the project
	 *
	 * @param jobs
	 * 			Job build factory
	 * @param issuesLastSuccessExtractorStep
	 * 			Step extracting the last succeful datetime of this job
	 * @param openIssuesSyncStep
	 * 			Step syncing all modified issues which are still open in MantisBT
	 * @param otherIssuesSyncStep
	 * 			Step syncing all issues which are still open in the local DB
	 * @param authIssuesStep
	 * 			Step for portal authentication at the begining of the job
	 * @param closeIssuesListener
	 * 			Listener for closing the portal authentication connection at the end of the job
	 * @return the job
	 */
	@Bean
	public Job syncIssuesJob(final JobBuilderFactory jobs, final Step issuesLastSuccessExtractorStep,
			final Step openIssuesSyncStep, final Step otherIssuesSyncStep, final Step authIssuesStep,
			final CloseAuthManagerListener closeIssuesListener) {

		return jobs.get("syncIssuesJob")
				.incrementer(new RunIdIncrementer())
				.listener(closeIssuesListener)
				.flow(authIssuesStep)
				.next(issuesLastSuccessExtractorStep)
				.next(openIssuesSyncStep)
				.next(otherIssuesSyncStep)
				.end()
				.build();
	}

	/**
	 * Build the forceSyncIssuesJob job. This job will update all issues for which
	 * the id is passed as job parameter.
	 *
	 *
	 * Parameters for this job are :
	 * 	- mantis.username
	 * 		MantisBT username. If anonymous access is used, should be an empty string.
	 *  - mantis.password
	 *  	MantisBT password. If anonymous access is used, should be an empty string.
	 *  - mantis.issues_id
	 *  	Semi-colon separated list of issues ids
	 *
	 * @param jobs
	 * 			Job build factory
	 * @param authIssuesStep
	 * 			Step for portal authentication at the begining of the job
	 * @param closeIssuesListener
	 * 			Listener for closing the portal authentication connection at the end of the job
	 * @param forceIssuesSyncStep
	 * 			Step syncing all issues matching the given ids
	 * @return the job
	 */
	@Bean
	public Job forceSyncIssuesJob(final JobBuilderFactory jobs, final Step authIssuesStep,
			final CloseAuthManagerListener closeIssuesListener,
			final Step forceIssuesSyncStep) {

		return jobs.get("forceSyncIssuesJob")
				.incrementer(new RunIdIncrementer())
				.listener(closeIssuesListener)
				.flow(authIssuesStep)
				.next(forceIssuesSyncStep)
				.end()
				.build();
	}

	/**
	 * Build the fileSyncIssuesJob job. This job will update all issues matching a list
	 * of ids contains in a CSV file.
	 * The CSV file must not have a header line for columns definition.
	 * The file is loaded through Spring resource loader so the filepath can contains
	 * definitions like classpath: and others.
	 *
	 * Parameters for this job are :
	 * 	- mantis.username
	 * 		MantisBT username. If anonymous access is used, should be an empty string.
	 *  - mantis.password
	 *  	MantisBT password. If anonymous access is used, should be an empty string.
	 *  - mantis.filepath
	 *  	File path of the CSV file
	 *
	 * @param jobs
	 * 			Job build factory
	 * @param authIssuesStep
	 * 			Step for portal authentication at the begining of the job
	 * @param closeIssuesListener
	 * 			Listener for closing the portal authentication connection at the end of the job
	 * @param fileIssuesSyncStep
	 * 			Step syncing all issues matching the given ids
	 * @return the job
	 */
	@Bean
	public Job fileSyncIssuesJob(final JobBuilderFactory jobs, final Step authIssuesStep,
			final CloseAuthManagerListener closeIssuesListener,
			final Step fileIssuesSyncStep) {

		return jobs.get("fileSyncIssuesJob")
				.incrementer(new RunIdIncrementer())
				.listener(closeIssuesListener)
				.flow(authIssuesStep)
				.next(fileIssuesSyncStep)
				.end()
				.build();
	}

	/**
	 * Build the handlersStatJob job. This job will compute the number of issues by project,
	 * handler and status in the handlers_stats table.
	 *
	 * Parameters for this job are :
	 * 	- mantis.computeDate
	 * 		The computing date, passed as "yyyy-MM-dd'T'HH:mm:ss".
	 *
	 * @param jobs
	 * 			Job build factory
	 * @param handlersStatStep
	 * 			The step launching the computation
	 * @return the job
	 */
	@Bean
	public Job handlersStatJob(final JobBuilderFactory jobs,
			final Step handlersStatStep) {

		return jobs.get("handlersStatJob")
				.incrementer(new RunIdIncrementer())
				.flow(handlersStatStep)
				.end()
				.build();
	}

	/**
	 * Build the listener for closing the portal authentication connection at the end of the job.
	 *
	 * @param authManager
	 * 			The portal auth manager
	 * @return the listener
	 */
	@Bean
	public CloseAuthManagerListener closeIssuesListener(final PortalAuthManager authManager) {
		final CloseAuthManagerListener listener = new CloseAuthManagerListener();
		listener.setMgr(authManager);
		return listener;
	}

	/**
	 * Build the step for portal authentication at the begining of the job.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param authTasklet
	 * 			The tasklet performing the authentication
	 * @return the step
	 */
	@Bean
	public Step authIssuesStep(final StepBuilderFactory stepBuilderFactory,
			final MethodInvokingTaskletAdapter authTasklet) {

		return stepBuilderFactory.get("authIssuesStep").allowStartIfComplete(true)
				.tasklet(authTasklet).build();
	}

	/**
	 * Build the step extracting the last succeful datetime of this job.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param mantisLastRunExtractorTasklet
	 * 			The tasklet getting the last successful start time of the job
	 * @param mantisLastRunExtractorPromotionListener
	 * 			The execution context promotion listener that promotes
	 * 			mantis.update.last_job_run and mantis.update.current_job_run to the job context.
	 * @return the step
	 */
	@Bean
	public Step issuesLastSuccessExtractorStep(final StepBuilderFactory stepBuilderFactory,
			final IssuesLastRunExtractorTasklet mantisLastRunExtractorTasklet,
			final StepExecutionListener mantisLastRunExtractorPromotionListener) {

		return stepBuilderFactory.get("issuesLastSuccessExtractorStep")
				.tasklet(mantisLastRunExtractorTasklet)
				.listener(mantisLastRunExtractorPromotionListener)
				.build();
	}

	/**
	 * The step syncing all modified issues which are still open in MantisBT.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param openIssuesReader
	 * 			The reader
	 * @param issuesProcessor
	 * 			The processor
	 * @param compositeIssuesWriter
	 * 			The writer
	 * @param cacheEvictionListener
	 * 			Listener for caches eviction if the step fails
	 * @return
	 */
	@Bean
	public Step openIssuesSyncStep(final StepBuilderFactory stepBuilderFactory,
			final OpenIssuesReader openIssuesReader,
			final IssuesProcessor issuesProcessor,
			final CompositeItemWriter<BugBean> compositeIssuesWriter,
			final CacheEvictionListener cacheEvictionListener) {

		return stepBuilderFactory.get("openIssuesSyncStep")
				.<IssueData, BugBean> chunk(10)
				.reader(openIssuesReader)
				.processor(issuesProcessor)
				.writer(compositeIssuesWriter)
				.listener(cacheEvictionListener)
				.build();
	}

	/**
	 * Build the step syncing all issues which are still open in the local DB.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param otherIssuesReader
	 * 			The reader
	 * @param issuesProcessor
	 * 			The processor
	 * @param compositeIssuesWriter
	 * 			The writer
	 * @param cacheEvictionListener
	 * 			Listener for caches eviction if the step fails
	 * @return
	 */
	@Bean
	public Step otherIssuesSyncStep(final StepBuilderFactory stepBuilderFactory,
			final OtherIssuesReader otherIssuesReader,
			final IssuesProcessor issuesProcessor,
			final CompositeItemWriter<BugBean> compositeIssuesWriter,
			final CacheEvictionListener cacheEvictionListener) {

		return stepBuilderFactory.get("otherIssuesSyncStep")
				.<IssueData, BugBean> chunk(10)
				.reader(otherIssuesReader)
				.processor(issuesProcessor)
				.writer(compositeIssuesWriter)
				.listener(cacheEvictionListener)
				.build();
	}

	/**
	 * The step syncing all issues matching the given ids.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param listIssuesReader
	 * 			The reader
	 * @param compositeIssuesProcessor
	 * 			The processor
	 * @param compositeIssuesWriter
	 * 			The writer
	 * @param cacheEvictionListener
	 * 			Listener for caches eviction if the step fails
	 * @return
	 */
	@Bean
	public Step forceIssuesSyncStep(final StepBuilderFactory stepBuilderFactory,
			final ListItemReader<BugIdBean> listIssuesReader,
			final CompositeItemProcessor<BugIdBean, BugBean> compositeIssuesProcessor,
			final CompositeItemWriter<BugBean> compositeIssuesWriter,
			final CacheEvictionListener cacheEvictionListener) {

		return stepBuilderFactory.get("forceIssuesSyncStep")
				.<BugIdBean, BugBean> chunk(10)
				.reader(listIssuesReader)
				.processor(compositeIssuesProcessor)
				.writer(compositeIssuesWriter)
				.listener(cacheEvictionListener)
				.build();
	}

	/**
	 * The step syncing all issues matching the given ids.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param csvIssuesReader
	 * 			The reader
	 * @param compositeIssuesProcessor
	 * 			The processor
	 * @param compositeIssuesWriter
	 * 			The writer
	 * @param cacheEvictionListener
	 * 			Listener for caches eviction if the step fails
	 * @return
	 */
	@Bean
	public Step fileIssuesSyncStep(final StepBuilderFactory stepBuilderFactory,
			final FlatFileItemReader<BugIdBean> csvIssuesReader,
			final CompositeItemProcessor<BugIdBean, BugBean> compositeIssuesProcessor,
			final CompositeItemWriter<BugBean> compositeIssuesWriter,
			final CacheEvictionListener cacheEvictionListener) {

		return stepBuilderFactory.get("fileIssuesSyncStep")
				.<BugIdBean, BugBean> chunk(10)
				.reader(csvIssuesReader)
				.processor(compositeIssuesProcessor)
				.writer(compositeIssuesWriter)
				.listener(cacheEvictionListener)
				.build();
	}

	/**
	 * The step launching the computation of the number of issues by project,
	 * handler and status.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param mantisHandlersStatTasklet
	 * 			The tasklet
	 * @return
	 */
	@Bean
	public Step handlersStatStep(final StepBuilderFactory stepBuilderFactory,
			final HandlersStatTasklet mantisHandlersStatTasklet) {

		return stepBuilderFactory.get("handlersStatStep")
				.tasklet(mantisHandlersStatTasklet)
				.build();
	}

	/**
	 * Build the listener for caches eviction if the step fails.
	 *
	 * @return the listener
	 */
	@Bean
	public CacheEvictionListener cacheEvictionListener() {
		return new CacheEvictionListener();
	}
}
