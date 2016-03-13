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
package mantisbtsync.core.jobs.issues.writers;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.beans.BugCustomFieldValue;
import mantisbtsync.core.junit.AbstractSqlWriterTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jdevarulrajah
 *
 */
public class BugCustomFieldsWriterTest extends AbstractSqlWriterTest {

	@Autowired
	BugCustomFieldsWriter bugCustomFieldsWriter;

	@Test
	public void test() throws Exception {

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary")
				.values(1, 1, "summary_1")
				.build(),

				insertInto("mantis_enum_custom_field_types")
				.columns("id", "name")
				.values(1, "type_1")
				.build(),

				insertInto("mantis_custom_field_table")
				.columns("id", "name", "type_id")
				.values(1, "field_1", 1)
				.build()
				);

		lauchOperation(op);

		bugCustomFieldsWriter.afterPropertiesSet();
		bugCustomFieldsWriter.write(buildItems());
	}

	/**
	 * Build the items to write.
	 *
	 * @return items
	 */
	private List<BugBean> buildItems() {

		final List<BugBean> items = new ArrayList<BugBean>();

		final BugBean item1 = new BugBean();
		final BugCustomFieldValue val = new BugCustomFieldValue();
		val.setBugId(BigInteger.ONE);
		val.setFieldId(BigInteger.ONE);
		val.setFieldValue("value_1");

		item1.getCustomFields().add(val);
		items.add(item1);

		return items;
	}

	/**
	 * @return the bugCustomFieldsWriter
	 */
	public BugCustomFieldsWriter getBugCustomFieldsWriter() {
		return bugCustomFieldsWriter;
	}

	/**
	 * @param bugCustomFieldsWriter the bugCustomFieldsWriter to set
	 */
	public void setBugCustomFieldsWriter(final BugCustomFieldsWriter bugCustomFieldsWriter) {
		this.bugCustomFieldsWriter = bugCustomFieldsWriter;
	}

}
