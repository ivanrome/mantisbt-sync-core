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
package mantisbtsync.core.common.readers;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.client.Stub;
import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;

/**
 * Read on item from Apache Axis WebService. The WebService method
 * is supposed to return a array of items.
 * Can use an authentification cookie if PortalAuthManager is set.
 *
 * @author jdevarulrajah
 *
 */
public class AxisAuthItemsArrayReader<T> extends
AbstractMethodInvokingDelegator<T[]> implements ItemReader<T> {

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Client stub generate by Apache Axis.
	 */
	private Stub clientStub;

	/**
	 * Index of the last read item.
	 */
	private int i = -1;

	/**
	 * Results array got from the WS.
	 */
	private T[] items;


	/**
	 * @return the authManager
	 */
	public PortalAuthManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param authManager the authManager to set
	 */
	public void setAuthManager(final PortalAuthManager authManager) {
		this.authManager = authManager;
	}

	/**
	 * @return the clientStub
	 */
	public Stub getClientStub() {
		return clientStub;
	}

	/**
	 * @param clientStub the clientStub to set
	 */
	public void setClientStub(final Stub clientStub) {
		this.clientStub = clientStub;
		setTargetObject(clientStub);
	}


	/**
	 * @return return value of the target method.
	 */
	@Override
	public T read() throws Exception {

		// If auth manager is set, try to get the cookie
		if (authManager != null && authManager.getAuthCookie() != null) {
			clientStub._setProperty(HTTPConstants.HEADER_COOKIE,
					authManager.getAuthCookie());
		}

		if (i < 0) {
			items = invokeDelegateMethod();
			i = -1;
		}

		i++;
		if (items != null && i < items.length) {
			return items[i];
		} else {
			return null;
		}
	}
}
