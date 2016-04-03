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
package com.github.jrrdev.mantisbtsync.core.jobs.projects.processors;

import org.springframework.batch.item.ItemProcessor;

import com.github.jrrdev.mantisbtsync.core.jobs.projects.beans.ProjectCategoryBean;

/**
 * Processor for the project categories that simply fills the project id with the given one.
 * This processor is used because mc_project_get_categories doesn't return the project id.
 *
 * @author jrrdev
 *
 */
public class ProjectCategoryProcessor implements ItemProcessor<String, ProjectCategoryBean> {

	/**
	 * Id of the project.
	 */
	private Integer projectId;

	/**
	 * @return the projectId
	 */
	public final Integer getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public final void setProjectId(final Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public ProjectCategoryBean process(final String item) throws Exception {
		final ProjectCategoryBean bean = new ProjectCategoryBean();
		bean.setProjectId(projectId);
		bean.setName(item);
		return bean;
	}
}
