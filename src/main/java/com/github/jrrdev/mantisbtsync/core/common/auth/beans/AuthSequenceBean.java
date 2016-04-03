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
package com.github.jrrdev.mantisbtsync.core.common.auth.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple wrapper for the whole sequence of requests.
 * Just a convenient way to write the XML file.
 *
 * @author jrrdev
 *
 */
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
