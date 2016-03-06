/**
 *
 */
package mantisbtsync.core.jobs.issues.beans;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author jdevarulrajah
 *
 */
public class BugNoteBean {

	private BigInteger id;

	private BigInteger bugId;

	private BigInteger reporterId;

	private String text;

	private Date dateSubmitted;

	private Date lastModified;

	/**
	 * Default constructor.
	 */
	public BugNoteBean() {
	}

	/**
	 * @return the id
	 */
	public BigInteger getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final BigInteger id) {
		this.id = id;
	}

	/**
	 * @return the bugId
	 */
	public BigInteger getBugId() {
		return bugId;
	}

	/**
	 * @param bugId the bugId to set
	 */
	public void setBugId(final BigInteger bugId) {
		this.bugId = bugId;
	}

	/**
	 * @return the reporterId
	 */
	public BigInteger getReporterId() {
		return reporterId;
	}

	/**
	 * @param reporterId the reporterId to set
	 */
	public void setReporterId(final BigInteger reporterId) {
		this.reporterId = reporterId;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the dateSubmitted
	 */
	public Date getDateSubmitted() {
		return dateSubmitted;
	}

	/**
	 * @param dateSubmitted the dateSubmitted to set
	 */
	public void setDateSubmitted(final Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(final Date lastModified) {
		this.lastModified = lastModified;
	}
}
