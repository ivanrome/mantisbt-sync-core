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
public final class BugHistoryBean {

	private BigInteger bugId;

	private BigInteger userId;

	private String fieldName;

	private String oldValue;

	private String newValue;

	private BigInteger historyType;

	private Date dateModified;

	/**
	 * Default constructor.
	 */
	public BugHistoryBean() {
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
	 * @return the userId
	 */
	public BigInteger getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(final BigInteger userId) {
		this.userId = userId;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the oldValue
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * @param oldValue the oldValue to set
	 */
	public void setOldValue(final String oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * @return the newValue
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @param newValue the newValue to set
	 */
	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}

	/**
	 * @return the historyType
	 */
	public BigInteger getHistoryType() {
		return historyType;
	}

	/**
	 * @param historyType the historyType to set
	 */
	public void setHistoryType(final BigInteger historyType) {
		this.historyType = historyType;
	}

	/**
	 * @return the dateModified
	 */
	public Date getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified the dateModified to set
	 */
	public void setDateModified(final Date dateModified) {
		this.dateModified = dateModified;
	}
}
