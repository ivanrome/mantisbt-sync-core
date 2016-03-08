package mantisbtsync.core.common.auth.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "httpRequest")
@XmlType(propOrder = { "uri", "requestType", "nextRequest"})
public class HttpRequestBean {

	private String uri;

	private HttpRequestBean nextRequest;

	private AbstractHttpRequestTypeBean requestType;

	/**
	 * Default constructor.
	 */
	public HttpRequestBean() {
		super();
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	@XmlElement
	public void setUri(final String uri) {
		this.uri = uri;
	}

	/**
	 * @return the nextRequest
	 */
	public HttpRequestBean getNextRequest() {
		return nextRequest;
	}

	/**
	 * @param nextRequest the nextRequest to set
	 */
	@XmlElement(nillable = true)
	public void setNextRequest(final HttpRequestBean nextRequest) {
		this.nextRequest = nextRequest;
	}

	/**
	 * @return the requestType
	 */
	public AbstractHttpRequestTypeBean getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	@XmlElements(value = {
			@XmlElement(name="get",
					type=HttpGetBean.class),
					@XmlElement(name="post",
					type=HttpPostBean.class)
	})
	public void setRequestType(final AbstractHttpRequestTypeBean requestType) {
		this.requestType = requestType;
	}
}
