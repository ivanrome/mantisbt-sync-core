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
package com.github.jrrdev.mantisbtsync.core.jobs.projects;

import java.math.BigInteger;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.jobs.projects.processors.ProjectCategoryProcessor;
import com.github.jrrdev.mantisbtsync.core.jobs.projects.processors.ProjectCustomFieldProcessor;

/**
 * Configuration for the processors used by the job of
 * Mantis projects syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class ProjectsProcessorsConfiguration {

	@Bean
	@StepScope
	public ProjectCategoryProcessor projectCategoriesProcessor(
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final ProjectCategoryProcessor proc = new ProjectCategoryProcessor();
		if (projectId != null) {
			proc.setProjectId(projectId.intValue());
		}

		return proc;
	}

	@Bean
	@StepScope
	public ProjectCustomFieldProcessor projectCustomFieldProcessor(
			@Value("#{jobExecutionContext['mantis.loop.project_id']}") final BigInteger projectId) {

		final ProjectCustomFieldProcessor proc = new ProjectCustomFieldProcessor();
		if (projectId != null) {
			proc.setProjectId(projectId.intValue());
		}

		return proc;
	}
}
