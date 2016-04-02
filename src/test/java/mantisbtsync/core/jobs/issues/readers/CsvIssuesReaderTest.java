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
package mantisbtsync.core.jobs.issues.readers;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mantisbtsync.core.Application;
import mantisbtsync.core.jobs.issues.beans.BugIdBean;
import mantisbtsync.core.junit.JunitTestConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jdevarulrajah
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({Application.class, JunitTestConfiguration.class})
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
	StepScopeTestExecutionListener.class })
public class CsvIssuesReaderTest {

	@Autowired
	FlatFileItemReader<BugIdBean> csvIssuesReader;

	public StepExecution getStepExecution() {

		final Map<String, JobParameter> map = new HashMap<String, JobParameter>();
		map.put("mantis.filepath", new JobParameter("issuesId.csv"));

		final StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(new JobParameters(map));

		return stepExecution;
	}

	@Test
	public void test() throws Exception {
		final List<BugIdBean> list = new ArrayList<BugIdBean>();
		csvIssuesReader.open(getStepExecution().getExecutionContext());

		BugIdBean item = csvIssuesReader.read();
		while (item != null) {
			list.add(item);
			item = csvIssuesReader.read();
		}

		assertEquals(2, list.size());
		assertEquals(BigInteger.valueOf(17428), list.get(0).getId());
		assertEquals(BigInteger.valueOf(14234), list.get(1).getId());
	}

	/**
	 * @return the csvIssuesReader
	 */
	public FlatFileItemReader<BugIdBean> getCsvIssuesReader() {
		return csvIssuesReader;
	}

	/**
	 * @param csvIssuesReader the csvIssuesReader to set
	 */
	public void setCsvIssuesReader(final FlatFileItemReader<BugIdBean> csvIssuesReader) {
		this.csvIssuesReader = csvIssuesReader;
	}

}
