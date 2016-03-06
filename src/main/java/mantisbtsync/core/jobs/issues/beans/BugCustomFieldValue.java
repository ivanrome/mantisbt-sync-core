/**
 *
 */
package mantisbtsync.core.jobs.issues.beans;

import java.math.BigInteger;

/**
 * @author jdevarulrajah
 *
 */
public class BugCustomFieldValue {

	private BigInteger fieldId;

	private BigInteger bugId;

	private String fieldValue;

	/**
	 * Default constructor.
	 */
	public BugCustomFieldValue() {
	}

	/**
	 * @return the fieldId
	 */
	public BigInteger getFieldId() {
		return fieldId;
	}

	/**
	 * @param fieldId the fieldId to set
	 */
	public void setFieldId(final BigInteger fieldId) {
		this.fieldId = fieldId;
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
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldValue(final String fieldValue) {
		this.fieldValue = fieldValue;
	}
}
