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
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;
import com.github.jrrdev.mantisbtsync.core.common.listener.CloseAuthManagerListener;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.beans.ProjectCategoryBean;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.beans.ProjectCustomFieldBean;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.decider.ProjectFlowDecider;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.processors.ProjectCategoryProcessor;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.processors.ProjectCustomFieldProcessor;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.tasklets.MantisLoginTasklet;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.tasklets.ProjectsExtractorTasklet;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.tasklets.ProjectsListTasklet;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.CustomFieldDefinitionData;
import biz.futureware.mantis.rpc.soap.client.ProjectVersionData;

/**
 * Configuration for the job of Mantis projects syncing.
 * Parameters for this job are :
 * 	- mantis.username
 *  - mantis.password
 *  - mantis.project_id
 *
 * @author jrrdev
 *
 */
@Configuration
public class JobProjectsConfiguration {

	// tag:job[]

	@Bean
	public Job syncProjectsJob(final JobBuilderFactory jobs, final Step mantisProjectsListStep,
			final Flow projectInitFlow, final JobExecutionDecider jobProjectInitFlowDecider,
			final Step authProjectsStep, final CloseAuthManagerListener closeProjectsListener) {

		final FlowBuilder<Flow> loopBuilder = new FlowBuilder<Flow>("projectInitLoop");
		final Flow loop = loopBuilder.start(projectInitFlow)
				.next(jobProjectInitFlowDecider)
				.on("LOOP").to(projectInitFlow)
				.from(jobProjectInitFlowDecider)
				.on("END_LOOP").end().build();


		return jobs.get("syncProjectsJob")
				.incrementer(new RunIdIncrementer())
				.listener(closeProjectsListener)
				.flow(authProjectsStep)
				.next(mantisProjectsListStep)
				.next(loop)
				.end()
				.build();
	}

	// end:job[]

	// tag::flow[]

	@Bean
	public Flow projectInitFlow(final Step mantisProjectExtractorStep, final Step projectCategoriesStep,
			final Step projectCustomFieldsStep, final Step mantisLoginStep, final Step projectUsersStep,
			final Step projectVersionsStep) {

		final FlowBuilder<Flow> builder = new FlowBuilder<Flow>("projectInitFlow");
		builder.start(mantisProjectExtractorStep)
		.next(projectCategoriesStep)
		.next(projectCustomFieldsStep)
		.next(mantisLoginStep)
		.next(projectUsersStep)
		.next(projectVersionsStep);

		return builder.build();
	}

	// end::flow[]

	// tag::step[]

	@Bean
	public CloseAuthManagerListener closeProjectsListener(final PortalAuthManager authManager) {
		final CloseAuthManagerListener listener = new CloseAuthManagerListener();
		listener.setMgr(authManager);
		return listener;
	}

	@Bean
	public Step authProjectsStep(final StepBuilderFactory stepBuilderFactory,
			final MethodInvokingTaskletAdapter authTasklet) {

		return stepBuilderFactory.get("authProjectsStep").allowStartIfComplete(true)
				.tasklet(authTasklet).build();
	}

	@Bean
	public Step mantisProjectsListStep(final StepBuilderFactory stepBuilderFactory,
			final ProjectsListTasklet mantisProjectsListTasklet,
			final StepExecutionListener mantisProjectsListListener) {

		return stepBuilderFactory.get("mantisProjectsListStep")
				.tasklet(mantisProjectsListTasklet)
				.listener(mantisProjectsListListener)
				.build();
	}

	@Bean
	public Step mantisProjectExtractorStep(final StepBuilderFactory stepBuilderFactory,
			final ProjectsExtractorTasklet mantisProjectExtractorTasklet,
			final StepExecutionListener mantisProjectExtractorListener) {

		return stepBuilderFactory.get("mantisProjectExtractorStep")
				.tasklet(mantisProjectExtractorTasklet)
				.listener(mantisProjectExtractorListener)
				.build();
	}

	@Bean
	public Step projectCategoriesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<String> projectCategoriesReader,
			final ProjectCategoryProcessor projectCategoriesProcessor,
			final ItemWriter<ProjectCategoryBean> projectCategoriesWriter) {

		return stepBuilderFactory.get("projectCategoriesStep")
				.<String, ProjectCategoryBean> chunk(10)
				.reader(projectCategoriesReader)
				.processor(projectCategoriesProcessor)
				.writer(projectCategoriesWriter).build();
	}

	@Bean
	public Step projectCustomFieldsStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<CustomFieldDefinitionData> projectCustomFieldsReader,
			final ProjectCustomFieldProcessor projectCustomFieldProcessor,
			final ItemWriter<ProjectCustomFieldBean> projectCustomFieldsWriter) {

		return stepBuilderFactory.get("projectCustomFieldsStep")
				.<CustomFieldDefinitionData, ProjectCustomFieldBean> chunk(10)
				.reader(projectCustomFieldsReader)
				.processor(projectCustomFieldProcessor)
				.writer(projectCustomFieldsWriter).build();
	}

	@Bean
	public Step mantisLoginStep(final StepBuilderFactory stepBuilderFactory,
			final MantisLoginTasklet mantisLoginTasklet,
			final StepExecutionListener mantisLoginPromotionListener) {

		return stepBuilderFactory.get("mantisLoginStep")
				.tasklet(mantisLoginTasklet)
				.listener(mantisLoginPromotionListener)
				.build();
	}

	@Bean
	public Step projectUsersStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<AccountData> projectUsersReader,
			final ItemWriter<AccountData> projectUsersWriter) {

		return stepBuilderFactory.get("projectUsersStep")
				.<AccountData, AccountData> chunk(10)
				.reader(projectUsersReader)
				.writer(projectUsersWriter)
				.build();
	}

	@Bean
	public Step projectVersionsStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ProjectVersionData> projectVersionsReader,
			final ItemWriter<ProjectVersionData> projectVersionsWriter) {

		return stepBuilderFactory.get("projectVersionsStep")
				.<ProjectVersionData, ProjectVersionData> chunk(10)
				.reader(projectVersionsReader)
				.writer(projectVersionsWriter)
				.build();
	}

	// end::step[]

	// tag::decider[]

	@Bean
	public ProjectFlowDecider jobProjectInitFlowDecider() {
		return new ProjectFlowDecider();
	}

	// end::decider[]
}
