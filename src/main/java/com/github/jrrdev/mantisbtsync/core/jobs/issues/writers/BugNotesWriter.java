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
package com.github.jrrdev.mantisbtsync.core.jobs.issues.writers;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugBean;
import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugNoteBean;

/**
 * Writer used to upsert the notes related to
 * an issue.
 * Insert entries in mantis_bugnote_table table.
 *
 * @author jrrdev
 *
 */
public class BugNotesWriter implements ItemWriter<BugBean> {

	/**
	 * Sub-writer used to write each item in the list of notes.
	 */
	private final JdbcBatchItemWriter<BugNoteBean> writer;

	/**
	 * SQL query used to perform the upsert.
	 */
	private static final String SQL_QUERY = "INSERT INTO mantis_bugnote_table\n"
			+ " (id, bug_id, reporter_id, text_note, date_submitted, last_modified)\n"
			+ " VALUES (:id, :bugId, :reporterId, :textNote, :dateSubmitted, :lastModified)\n"
			+ " ON DUPLICATE KEY UPDATE text_note = :textNote, last_modified = :lastModified";

	/**
	 * Default constructor.
	 */
	public BugNotesWriter() {
		writer = new JdbcBatchItemWriter<BugNoteBean>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BugNoteBean>());
		writer.setSql(SQL_QUERY);
		writer.setAssertUpdates(false);
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(final List<? extends BugBean> items) throws Exception {
		if (items != null) {
			for (final BugBean bug : items) {
				if (bug.getNotes() != null) {
					writer.write(bug.getNotes());
				}
			}
		}
	}

	/**
	 * Check mandatory properties.
	 */
	public void afterPropertiesSet() {
		writer.afterPropertiesSet();
	}

	/**
	 * Set the datasource.
	 *
	 * @param dataSource
	 * 			the datasource to set
	 */
	public void setDataSource(final DataSource dataSource) {
		writer.setDataSource(dataSource);
	}
}
