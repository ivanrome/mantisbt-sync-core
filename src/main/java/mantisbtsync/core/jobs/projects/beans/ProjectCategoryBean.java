/**
 *
 */
package mantisbtsync.core.jobs.projects.beans;

/**
 * Bean for storing data related to a project category.
 *
 * @author jdevarulrajah
 *
 */
public final class ProjectCategoryBean {

	private String name;

	private Integer id;

	private Integer projectId;

	/**
	 * Default constructor.
	 */
	public ProjectCategoryBean() {
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public final Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(final Integer id) {
		this.id = id;
	}

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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectCategoryBean [name=" + name + ", id=" + id
				+ ", projectId=" + projectId + "]";
	}
}
