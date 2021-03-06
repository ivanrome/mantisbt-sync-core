/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 J�rard Devarulrajah
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
 * Bean wrapping a note on an issue.
 *
 * @author jrrdev
 *
 */
public class BugNoteBean {

	/**
	 * Id of the note.
	 */
	private BigInteger id;

	/**
	 * Id of the issue.
	 */
	private BigInteger bugId;

	/**
	 * Id of the author.
	 */
	private BigInteger reporterId;

	/**
	 * Text of the note.
	 */
	private String textNote;

	/**
	 * Initial date of the note writing.
	 */
	private Timestamp dateSubmitted;

	/**
	 * Last modification date of the note.
	 */
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
	public String getTextNote() {
		return textNote;
	}

	/**
	 * @param text the text to set
	 */
	public void setTextNote(final String text) {
		this.textNote = text;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BugNoteBean [id=" + id + ", bugId=" + bugId + ", reporterId="
				+ reporterId + ", textNote=" + textNote + ", dateSubmitted="
				+ dateSubmitted + ", lastModified=" + lastModified + "]";
	}
}
