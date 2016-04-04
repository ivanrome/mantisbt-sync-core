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

import biz.futureware.mantis.rpc.soap.client.CustomFieldDefinitionData;

import com.github.jrrdev.mantisbtsync.core.jobs.projects.beans.ProjectCustomFieldBean;

/**
 * Processor that builds the custom field bean from the raw data retrieved from
 * the WS call.
 *
 * @author jrrdev
 *
 */
public class ProjectCustomFieldProcessor
implements ItemProcessor<CustomFieldDefinitionData, ProjectCustomFieldBean> {

	/**
	 * Idof the project.
	 */
	private Integer projectId;

	/**
	 * @return the projectId
	 */
	public Integer getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(final Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public ProjectCustomFieldBean process(final CustomFieldDefinitionData item)
			throws Exception {
		final ProjectCustomFieldBean bean = new ProjectCustomFieldBean();

		if (item.getField() != null) {
			bean.setId(item.getField().getId().intValue());
			bean.setName(item.getField().getName());
		}

		bean.setDefaultValue(item.getDefault_value());
		bean.setPossibleValues(item.getPossible_values());
		bean.setTypeId(item.getType().intValue());
		bean.setValidRegexp(item.getValid_regexp());
		bean.setProjectId(projectId);

		return bean;
	}
}
