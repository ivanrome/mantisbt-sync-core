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

import static com.ninja_squad.dbsetup.Operations.insertInto;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.jrrdev.mantisbtsync.core.jobs.issues.beans.BugBean;
import com.github.jrrdev.mantisbtsync.core.junit.AbstractSqlWriterTest;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jrrdev
 *
 */
public class BugsWriterTest extends AbstractSqlWriterTest {

	@Autowired
	JdbcBatchItemWriter<BugBean> bugsWriter;

	@Test
	public void test() throws Exception {

		final Operation op = insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build();

		lauchOperation(op);

		bugsWriter.write(buildItems());
	}

	/**
	 * Build the items to write.
	 *
	 * @return items
	 */
	private List<BugBean> buildItems() {

		final Calendar cal = Calendar.getInstance();
		final List<BugBean> items = new ArrayList<BugBean>();

		final BugBean item1 = new BugBean();
		item1.setId(BigInteger.ONE);
		item1.setProjectId(BigInteger.ONE);
		item1.setDescription("Description_1");
		item1.setSummary("Summary_1");
		item1.setDateSubmitted(new Timestamp(cal.getTimeInMillis()));
		item1.setLastUpdated(new Timestamp(cal.getTimeInMillis()));

		items.add(item1);

		return items;
	}

	/**
	 * @return the bugsWriter
	 */
	public JdbcBatchItemWriter<BugBean> getBugsWriter() {
		return bugsWriter;
	}

	/**
	 * @param bugsWriter the bugsWriter to set
	 */
	public void setBugsWriter(final JdbcBatchItemWriter<BugBean> bugsWriter) {
		this.bugsWriter = bugsWriter;
	}

}
