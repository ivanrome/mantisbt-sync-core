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
