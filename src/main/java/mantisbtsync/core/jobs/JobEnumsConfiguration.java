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
package mantisbtsync.core.jobs;

import mantisbtsync.core.common.listener.CloseAuthManagerListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * Configuration for the job of Mantis enumerations syncing.
 * Parameters for this job are :
 * 	- mantis.username
 *  - mantis.password
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class JobEnumsConfiguration {

	@Bean
	public Job syncEnumsJob(final JobBuilderFactory jobs, final Step customFieldTypesStep, final Step etasStep,
			final Step prioritiesStep, final Step projectionsStep, final Step projectStatusStep, final Step projectViewStatesStep,
			final Step reproducibilitiesStep, final Step resolutionsStep, final Step severitiesStep, final Step statusStep,
			final Step authStep, final CloseAuthManagerListener closeAuthManagerListener) {

		return jobs.get("syncEnumsJob")
				.incrementer(new RunIdIncrementer())
				.listener(closeAuthManagerListener)
				.flow(authStep)
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

	@Bean
	public Step customFieldTypesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> customFieldTypesReader,
			final ItemWriter<ObjectRef> customFieldTypesWriter) {

		return getEnumStep("customFieldTypesStep", stepBuilderFactory, customFieldTypesReader, customFieldTypesWriter);
	}

	@Bean
	public Step etasStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> etasReader,
			final ItemWriter<ObjectRef> etasWriter) {

		return getEnumStep("etasStep", stepBuilderFactory, etasReader, etasWriter);
	}

	@Bean
	public Step prioritiesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> prioritiesReader,
			final ItemWriter<ObjectRef> prioritiesWriter) {

		return getEnumStep("prioritiesStep", stepBuilderFactory, prioritiesReader, prioritiesWriter);
	}

	@Bean
	public Step projectionsStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> projectionsReader,
			final ItemWriter<ObjectRef> projectionsWriter) {

		return getEnumStep("projectionsStep", stepBuilderFactory, projectionsReader, projectionsWriter);
	}

	@Bean
	public Step projectStatusStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> projectStatusReader,
			final ItemWriter<ObjectRef> projectStatusWriter) {

		return getEnumStep("projectStatusStep", stepBuilderFactory, projectStatusReader, projectStatusWriter);
	}

	@Bean
	public Step projectViewStatesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> projectViewStatesReader,
			final ItemWriter<ObjectRef> projectViewStatesWriter) {

		return getEnumStep("projectViewStatesStep", stepBuilderFactory, projectViewStatesReader, projectViewStatesWriter);
	}

	@Bean
	public Step reproducibilitiesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> reproducibilitiesReader,
			final ItemWriter<ObjectRef> reproducibilitiesWriter) {

		return getEnumStep("reproducibilitiesStep", stepBuilderFactory, reproducibilitiesReader, reproducibilitiesWriter);
	}

	@Bean
	public Step resolutionsStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> resolutionsReader,
			final ItemWriter<ObjectRef> resolutionsWriter) {

		return getEnumStep("resolutionsStep", stepBuilderFactory, resolutionsReader, resolutionsWriter);
	}

	@Bean
	public Step severitiesStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> severitiesReader,
			final ItemWriter<ObjectRef> severitiesWriter) {

		return getEnumStep("severitiesStep", stepBuilderFactory, severitiesReader, severitiesWriter);
	}

	@Bean
	public Step statusStep(final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> statusReader,
			final ItemWriter<ObjectRef> statusWriter) {

		return getEnumStep("statusStep", stepBuilderFactory, statusReader, statusWriter);
	}

	private Step getEnumStep(final String stepName,	final StepBuilderFactory stepBuilderFactory,
			final ItemReader<ObjectRef> reader,	final ItemWriter<ObjectRef> writer) {

		return stepBuilderFactory.get(stepName).<ObjectRef, ObjectRef> chunk(10)
				.reader(reader).writer(writer).build();
	}
}
