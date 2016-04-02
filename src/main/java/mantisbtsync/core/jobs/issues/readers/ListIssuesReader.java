/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 J?rard Devarulrajah
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.Assert;

import biz.futureware.mantis.rpc.soap.client.IssueData;

/**
 * @author jdevarulrajah
 *
 */
public class ListIssuesReader extends AbstractIssuesReader {

	private final List<BigInteger> issues = new ArrayList<BigInteger>();

	private int index = 0;

	@Override
	public IssueData read() throws Exception, UnexpectedInputException,
	ParseException, NonTransientResourceException {

		Assert.notNull(getClientStub());

		// If auth manager is set, try to get the cookie
		if (getAuthManager() != null && getAuthManager().getAuthCookie() != null) {
			getClientStub()._setProperty(HTTPConstants.HEADER_COOKIE,
					getAuthManager().getAuthCookie());
		}

		IssueData item = null;
		if (issues != null  && index < issues.size()) {
			final BigInteger issueId = issues.get(index);
			item = getClientStub().mc_issue_get(getUserName(), getPassword(), issueId);
			index++;
		}

		return item;

	}

	public void setIssuesToSync(final String listId) {
		final String[] strIds = listId.split(";");
		for (final String strId : strIds) {
			final long idValue = Long.valueOf(strId);
			issues.add(BigInteger.valueOf(idValue));
		}
	}
}
