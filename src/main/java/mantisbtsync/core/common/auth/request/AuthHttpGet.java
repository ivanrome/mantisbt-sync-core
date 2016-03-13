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
package mantisbtsync.core.common.auth.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;

/**
 * Implementation of a GET request.
 *
 * @author jdevarulrajah
 *
 */
public final class AuthHttpGet extends AbstractAuthHttpRequest {

	/**
	 * Default constructor.
	 */
	public AuthHttpGet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configFromPreviousResponse(final HttpEntity entity) {
		// No configuration needed from previous response
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		setHttpRequest(new HttpGet(getUri()));
	}
}
