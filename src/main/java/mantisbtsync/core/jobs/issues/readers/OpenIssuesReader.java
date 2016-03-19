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

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

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
public class OpenIssuesReader extends AbstractIssuesReader {

	private int currentPage = 0;

	/**
	 * Index of the last read item.
	 */
	private int i = -1;

	/**
	 * Results array got from the WS.
	 */
	private IssueData[] items;

	private static final BigInteger PAGE_SIZE = BigInteger.valueOf(20);

	private Calendar lastJobRun = null;

	@Override
	public IssueData read() throws Exception, UnexpectedInputException,
	ParseException, NonTransientResourceException {

		Assert.notNull(getClientStub());

		// If auth manager is set, try to get the cookie
		if (getAuthManager() != null && getAuthManager().getAuthCookie() != null) {
			getClientStub()._setProperty(HTTPConstants.HEADER_COOKIE,
					getAuthManager().getAuthCookie());
		}

		if (i < 0 || i >= (items.length - 1)) {
			currentPage++;
			i = -1;
			items = getClientStub().mc_project_get_issues(getUserName(), getPassword(),
					getProjectId(), BigInteger.valueOf(currentPage), PAGE_SIZE);
		}

		i++;
		if (items != null && i < items.length) {
			final IssueData item = items[i];

			if (item != null
					&& (lastJobRun == null
					|| item.getLast_updated() == null
					|| item.getLast_updated().after(lastJobRun))) {

				return item;

			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * @return the lastJobRun
	 */
	public Calendar getLastJobRun() {
		return lastJobRun;
	}

	/**
	 * @param pLastJobRun the lastJobRun to set
	 */
	public void setLastJobRun(final Date pLastJobRun) {
		if (pLastJobRun != null) {
			lastJobRun = Calendar.getInstance();
			lastJobRun.setTime(pLastJobRun);
		} else {
			lastJobRun = null;
		}
	}
}
