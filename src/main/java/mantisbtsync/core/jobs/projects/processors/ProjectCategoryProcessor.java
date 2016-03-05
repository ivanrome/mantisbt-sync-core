/**
 *
 */
package mantisbtsync.core.jobs.projects.processors;

import mantisbtsync.core.jobs.projects.beans.ProjectCategoryBean;

import org.springframework.batch.item.ItemProcessor;

/**
 * @author jdevarulrajah
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
