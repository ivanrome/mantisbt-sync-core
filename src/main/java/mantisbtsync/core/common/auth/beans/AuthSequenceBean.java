package mantisbtsync.core.common.auth.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "authsequence")
public class AuthSequenceBean {

	/**
	 * First request in the sequence.
	 */
	private HttpRequestBean firstRequest;

	/**
	 * Default constructor.
	 */
	public AuthSequenceBean() {
	}

	/**
	 * @return the firstRequest
	 */
	public HttpRequestBean getFirstRequest() {
		return firstRequest;
	}

	/**
	 * @param firstRequest the firstRequest to set
	 */
	@XmlElement
	public void setFirstRequest(final HttpRequestBean firstRequest) {
		this.firstRequest = firstRequest;
	}
}
