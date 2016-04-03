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
package com.github.jrrdev.mantisbtsync.core.common.auth;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import com.github.jrrdev.mantisbtsync.core.common.auth.beans.AuthSequenceBean;
import com.github.jrrdev.mantisbtsync.core.common.auth.beans.HttpGetBean;
import com.github.jrrdev.mantisbtsync.core.common.auth.beans.HttpPostBean;
import com.github.jrrdev.mantisbtsync.core.common.auth.beans.HttpRequestBean;
import com.github.jrrdev.mantisbtsync.core.common.auth.request.AbstractAuthHttpRequest;
import com.github.jrrdev.mantisbtsync.core.common.auth.request.AuthHttpGet;
import com.github.jrrdev.mantisbtsync.core.common.auth.request.AuthHttpPost;

/**
 * Class used to build an {@link PortalAuthManager} from
 * the definition of a sequence of requests contains in a
 * XML file.
 *
 * @author jrrdev
 *
 */
public class PortalAuthBuilder implements ResourceLoaderAware {

	/**
	 * Spring resource loader.
	 */
	private ResourceLoader resourceLoader;

	/**
	 * Private constructor.
	 */
	public PortalAuthBuilder() {
	}

	/**
	 * Build the portal authentication manager from an XML file
	 * describing the sequence of requests to be sent.
	 *
	 * @param filepath
	 * 		File path of the XML file. The file is loaded through Spring resource loader, so
	 * 		the file path can contain definition like "classpath:"
	 * @return the portal authentication manager
	 * @throws JAXBException
	 * 		If an error occurs during the XML unmarshalling
	 * @throws IOException
	 * 		if the resource cannot be resolved as absolute file path, i.e. if the resource is
	 * 		not available in a file system
	 */
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

	/**
	 * Build an implementation of the request described by the bean
	 * and make a recursive call to be build the next request in the sequence.
	 *
	 * @param reqBean
	 * 		Bean containing the description of the request
	 * @return An implementation of the request
	 */
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

	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
	 */
	@Override
	public void setResourceLoader(final ResourceLoader pResourceLoader) {
		resourceLoader = pResourceLoader;
	}
}
