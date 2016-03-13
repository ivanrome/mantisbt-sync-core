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
