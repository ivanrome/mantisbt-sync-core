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
package mantisbtsync.core.services;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author thor
 *
 */
public class IssuesDaoTest extends AbstractSqlWriterTest {

	@Autowired
	private IssuesDao dao;

	/**
	 * Test method for {@link mantisbtsync.core.services.IssuesDao#getIssuesBiggestId()}.
	 */
	@Test
	public void testGetIssuesBiggestIdNoIssues() {
		final BigInteger result = dao.getIssuesBiggestId();
		assertEquals(BigInteger.ZERO, result);
	}

	/**
	 * Test method for {@link mantisbtsync.core.services.IssuesDao#getIssuesBiggestId()}.
	 */
	@Test
	public void testGetIssuesBiggestId() {

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "old_user_1")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync")
				.values(10, 1, "summary_1", ValueGenerators.dateSequence().nextValue())
				.build()
				);

		lauchOperation(op);

		final BigInteger result = dao.getIssuesBiggestId();
		assertEquals(BigInteger.TEN, result);
	}

	/**
	 * @return the dao
	 */
	public IssuesDao getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(final IssuesDao dao) {
		this.dao = dao;
	}

}
