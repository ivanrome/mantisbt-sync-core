/**
 *
 */
package mantisbtsync.core.common.auth;

import java.io.File;

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

/**
 * @author jdevarulrajah
 *
 */
public class PortalAuthBuilder {

	/**
	 * Private constructor.
	 */
	private PortalAuthBuilder() {
	}

	public static PortalAuthManager buildAuthManager(final String filepath) throws JAXBException {
		final PortalAuthManager mgr = new PortalAuthManager();

		if (filepath != null && !filepath.isEmpty()) {
			final File file = new File(filepath);

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

	private static AbstractAuthHttpRequest buildRequest(final HttpRequestBean reqBean) {
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
}
