/**
 *
 */
package mantisbtsync.core.jobs.projects.processors;

import mantisbtsync.core.jobs.projects.beans.ProjectCustomFieldBean;

import org.springframework.batch.item.ItemProcessor;

import biz.futureware.mantis.rpc.soap.client.CustomFieldDefinitionData;

/**
 * @author jdevarulrajah
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
