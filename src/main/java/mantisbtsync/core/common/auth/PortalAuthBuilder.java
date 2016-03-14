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
package mantisbtsync.core.common.auth;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import mantisbtsync.core.common.auth.beans.AuthSequenceBean;
import mantisbtsync.core.common.auth.beans.HttpGetBean;
import mantisbtsync.core.common.auth.beans.HttpPostBean;
import mantisbtsync.core.common.auth.beans.HttpRequestBean;
import mantisbtsync.core.common.auth.request.AbstractAuthHttpRequest;
import mantisbtsync.core.common.auth.request.AuthHttpGet;
import mantisbtsync.core.common.auth.request.AuthHttpPost;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * @author jdevarulrajah
 *
 */
public class PortalAuthBuilder implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	/**
	 * Private constructor.
	 */
	public PortalAuthBuilder() {
	}

	public PortalAuthManager buildAuthManager(final String filepath) throws JAXBException, IOException {
		final PortalAuthManager mgr = new PortalAuthManager();

		if (filepath != null && !filepath.isEmpty()) {

			final File file = resourceLoader.getResource(filepath).getFile();

			if (file.exists() && file.isFile() && file.canRead()) {
				final JAXBContext jaxbContext = JAXBContext.newInstance(AuthSequenceBean.class);
				final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				final AuthSequenceBean sequence = (AuthSequenceBean) jaxbUnmarshaller.unmarshal(file);

				if (sequence != null) {
					mgr.setFirstRequest(buildRequest(sequence.getFirstRequest()));
				}
			}
		}

		return mgr;
	}

	private AbstractAuthHttpRequest buildRequest(final HttpRequestBean reqBean) {
		AbstractAuthHttpRequest req = null;

		if (reqBean != null) {
			if (reqBean.getRequestType() instanceof HttpGetBean) {
				final AuthHttpGet reqImp = new AuthHttpGet();
				reqImp.setUri(reqBean.getUri());
				req = reqImp;
			} else if (reqBean.getRequestType() instanceof HttpPostBean) {
				final HttpPostBean postBean = (HttpPostBean) reqBean.getRequestType();
				final AuthHttpPost reqImp = new AuthHttpPost();
				reqImp.setUri(reqBean.getUri());
				reqImp.setParameters(postBean.getParameters());
				reqImp.setFormAction(postBean.getFormAction());
				req = reqImp;
			}

			if (req != null) {
				req.setNextRequest(buildRequest(reqBean.getNextRequest()));
			}
		}

		return req;
	}

	@Override
	public void setResourceLoader(final ResourceLoader pResourceLoader) {
		resourceLoader = pResourceLoader;
	}
}
