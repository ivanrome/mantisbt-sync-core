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
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.common.listener.CloseAuthManagerListener;

/**
 * Configuration for the jobs to sync MantisBT enumerations.
 *
 * @author jrrdev
 *
 */
@Configuration
public class JobEnumsConfiguration {

	/**
	 * Build the syncEnumsJob job (sync of MantisBT enumerations).
	 * Parameters for this job are :
	 * 	- mantis.username
	 * 		MantisBT username. If anonymous access is used, should be an empty string.
	 *  - mantis.password
	 *  	MantisBT password. If anonymous access is used, should be an empty string.
	 *
	 *  Steps are executed in a linear flow.
	 *
	 * @param jobs
	 * 			Job build factory
	 * @param customFieldTypesStep
	 * 			Step syncing the custom field types
	 * @param etasStep
	 * 			Step syncing the etas
	 * @param prioritiesStep
	 * 			Step syncing the priorities
	 * @param projectionsStep
	 * 			Step syncing the projections
	 * @param projectStatusStep
	 * 			Step syncing the project status
	 * @param projectViewStatesStep
	 * 			Step syncing the project view states
	 * @param reproducibilitiesStep
	 * 			Step syncing the reproducibilities
	 * @param resolutionsStep
	 * 			Step syncing the resolutions
	 * @param severitiesStep
	 * 			Step syncing the severities
	 * @param statusStep
	 * 			Step syncing the status
	 * @param authEnumsStep
	 * 			Step for portal authentication at the begining of the job
	 * @param closeEnumsListener
	 * 			Listener for closing the portal authentication connection at the end of the job
	 * @return the job
	 */
	@Bean
	public Job syncEnumsJob(final JobBuilderFactory jobs, final Step customFieldTypesStep, final Step etasStep,
			final Step prioritiesStep, final Step projectionsStep, final Step projectStatusStep, final Step projectViewStatesStep,
			final Step reproducibilitiesStep, final Step resolutionsStep, final Step severitiesStep, final Step statusStep,
			final Step authEnumsStep, final CloseAuthManagerListener closeEnumsListener) {

		return jobs.get("syncEnumsJob")
				.incrementer(new RunIdIncrementer())
				.listener(closeEnumsListener)
				.flow(authEnumsStep)
				.next(customFieldTypesStep)
				.next(etasStep)
				.next(prioritiesStep)
				.next(projectionsStep)
				.next(projectStatusStep)
				.next(projectViewStatesStep)
				.next(reproducibilitiesStep)
				.next(resolutionsStep)
				.next(severitiesStep)
				.next(statusStep)
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
	public CloseAuthManagerListener closeEnumsListener(final PortalAuthManager authManager) {
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
	public Step authEnumsStep(final StepBuilderFactory stepBuilderFactory,
			final MethodInvokingTaskletAdapter authTasklet) {

		return stepBuilderFactory.get("authEnumsStep").allowStartIfComplete(true)
				.tasklet(authTasklet).build();
	}

	/**
	 * Build the step syncing the custom field types.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param customFieldTypesReader
	 * 			The reader
	 * @param customFieldTypesWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step customFieldTypesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> customFieldTypesReader,
			final ItemWriter<ObjectRef> customFieldTypesWriter) {

		return getEnumStep("customFieldTypesStep", stepBuilderFactory, customFieldTypesReader, customFieldTypesWriter);
	}

	/**
	 * Build the step syncing the etas.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param etasReader
	 * 			The reader
	 * @param etasWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step etasStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> etasReader,
			final ItemWriter<ObjectRef> etasWriter) {

		return getEnumStep("etasStep", stepBuilderFactory, etasReader, etasWriter);
	}

	/**
	 * Build the step syncing the priorities.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param prioritiesReader
	 * 			The reader
	 * @param prioritiesWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step prioritiesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> prioritiesReader,
			final ItemWriter<ObjectRef> prioritiesWriter) {

		return getEnumStep("prioritiesStep", stepBuilderFactory, prioritiesReader, prioritiesWriter);
	}

	/**
	 * Build the step syncing the projections.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param projectionsReader
	 * 			The reader
	 * @param projectionsWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step projectionsStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> projectionsReader,
			final ItemWriter<ObjectRef> projectionsWriter) {

		return getEnumStep("projectionsStep", stepBuilderFactory, projectionsReader, projectionsWriter);
	}

	/**
	 * Build the step syncing the project status.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param projectStatusReader
	 * 			The reader
	 * @param projectStatusWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step projectStatusStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> projectStatusReader,
			final ItemWriter<ObjectRef> projectStatusWriter) {

		return getEnumStep("projectStatusStep", stepBuilderFactory, projectStatusReader, projectStatusWriter);
	}

	/**
	 * Build the step syncing the project view states.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param projectViewStatesReader
	 * 			The reader
	 * @param projectViewStatesWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step projectViewStatesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> projectViewStatesReader,
			final ItemWriter<ObjectRef> projectViewStatesWriter) {

		return getEnumStep("projectViewStatesStep", stepBuilderFactory, projectViewStatesReader, projectViewStatesWriter);
	}

	/**
	 * Build the step syncing the reproducibilities.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param reproducibilitiesReader
	 * 			The reader
	 * @param reproducibilitiesWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step reproducibilitiesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> reproducibilitiesReader,
			final ItemWriter<ObjectRef> reproducibilitiesWriter) {

		return getEnumStep("reproducibilitiesStep", stepBuilderFactory, reproducibilitiesReader, reproducibilitiesWriter);
	}

	/**
	 * Build the step syncing the resolutions.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param resolutionsReader
	 * 			The reader
	 * @param resolutionsWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step resolutionsStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> resolutionsReader,
			final ItemWriter<ObjectRef> resolutionsWriter) {

		return getEnumStep("resolutionsStep", stepBuilderFactory, resolutionsReader, resolutionsWriter);
	}

	/**
	 * Build the step syncing the severities.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param severitiesReader
	 * 			The reader
	 * @param severitiesWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step severitiesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> severitiesReader,
			final ItemWriter<ObjectRef> severitiesWriter) {

		return getEnumStep("severitiesStep", stepBuilderFactory, severitiesReader, severitiesWriter);
	}

	/**
	 * Build the step syncing the issues status.
	 *
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param statusReader
	 * 			The reader
	 * @param statusWriter
	 * 			The writer
	 * @return the step
	 */
	@Bean
	public Step statusStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> statusReader,
			final ItemWriter<ObjectRef> statusWriter) {

		return getEnumStep("statusStep", stepBuilderFactory, statusReader, statusWriter);
	}

	/**
	 * Build the step with the given name, reader and writer.
	 *
	 * @param stepName
	 * 			The step name
	 * @param stepBuilderFactory
	 * 			The step builder factory
	 * @param reader
	 * 			The reader
	 * @param writer
	 * 			The writer
	 * @return the step
	 */
	private Step getEnumStep(final String stepName,	final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> reader,	final ItemWriter<ObjectRef> writer) {

		return stepBuilderFactory.get(stepName).<ObjectRef, ObjectRef> chunk(10)
				.reader(reader).writer(writer).build();
	}
}
