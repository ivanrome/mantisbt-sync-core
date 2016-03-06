/**
 *
 */
package mantisbtsync.core.jobs.issues.beans;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @author jdevarulrajah
 *
 */
public class BugNoteBean {

	private BigInteger id;

	private BigInteger bugId;

	private BigInteger reporterId;

	private String text;

	private Timestamp dateSubmitted;

	private Timestamp lastModified;

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
	public Timestamp getDateSubmitted() {
		return dateSubmitted;
	}

	/**
	 * @param dateSubmitted the dateSubmitted to set
	 */
	public void setDateSubmitted(final Timestamp dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	/**
	 * @return the lastModified
	 */
	public Timestamp getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(final Timestamp lastModified) {
		this.lastModified = lastModified;
	}
}
