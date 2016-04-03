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
package com.github.jrrdev.mantisbtsync.core.jobs.issues.beans;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Bean wrapping a line in the history of a bean.
 *
 * @author jrrdev
 *
 */
public final class BugHistoryBean {

	/**
	 * Issue id.
	 */
	private BigInteger bugId;

	/**
	 * Id of the user who performed the operation.
	 */
	private BigInteger userId;

	/**
	 * Name of the modified field.
	 */
	private String fieldName;

	/**
	 * Old value of the field.
	 */
	private String oldValue;

	/**
	 * New value of the field.
	 */
	private String newValue;

	/**
	 * History type.
	 */
	private BigInteger historyType;

	/**
	 * Date of the operation.
	 */
	private java.sql.Timestamp dateModified;

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
	public Timestamp getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified the dateModified to set
	 */
	public void setDateModified(final Timestamp dateModified) {
		this.dateModified = dateModified;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BugHistoryBean [bugId=" + bugId + ", userId=" + userId
				+ ", fieldName=" + fieldName + ", oldValue=" + oldValue
				+ ", newValue=" + newValue + ", historyType=" + historyType
				+ ", dateModified=" + dateModified + "]";
	}
}
