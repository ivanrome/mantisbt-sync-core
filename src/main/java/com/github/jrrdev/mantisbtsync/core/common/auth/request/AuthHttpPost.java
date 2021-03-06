/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 J�rard Devarulrajah
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
package com.github.jrrdev.mantisbtsync.core.common.auth.request;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Implementation of a POST request.
 *
 * @author jrrdev
 *
 */
public final class AuthHttpPost extends AbstractAuthHttpRequest {

	/**
	 * HTML hidden attribut.
	 */
	private static final String HTML_HIDDEN = "hidden";

	/**
	 * HTML form attribut.
	 */
	private static final String HTML_FORM = "form";

	/**
	 * HTML action attribut.
	 */
	private static final String HTML_ACTION = "action";

	/**
	 * HTML input attribut.
	 */
	private static final String HTML_INPUT = "input";

	/**
	 * HTML type attribut.
	 */
	private static final String HTML_TYPE = "type";

	/**
	 * HTML value attribut.
	 */
	private static final String HTML_VALUE = "value";

	/**
	 * HTML name attribut.
	 */
	private static final String HTML_NAME = "name";


	/**
	 * Request builder for POST.
	 */
	private RequestBuilder builder = RequestBuilder.post();

	/**
	 * Parameters of the POST request.
	 */
	private Map<String, String> parameters;

	/**
	 * Form action of the POST request.
	 */
	private String formAction;

	/**
	 * Default constructor.
	 */
	public AuthHttpPost() {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public void configFromPreviousResponse(final HttpEntity entity) throws ParseException, IOException {
		if (formAction == null || entity == null) {
			return;
		}

		final String content = EntityUtils.toString(entity);
		final Elements forms = Jsoup.parse(content).getElementsByTag(HTML_FORM);

		for (final Element form : forms) {
			// Get the form
			if (form.hasAttr(HTML_ACTION) && formAction.equalsIgnoreCase(form.attr(HTML_ACTION))) {

				// Parsing of hidden inputs
				final Elements inputs = form.getElementsByTag(HTML_INPUT);
				for (final Element input : inputs) {
					if (input.hasAttr(HTML_TYPE) && HTML_HIDDEN.equalsIgnoreCase(input.attr(HTML_TYPE))) {
						final String value = input.attr(HTML_VALUE);
						final String name = input.attr(HTML_NAME);
						builder = builder.addParameter(name, value);
					}
				}
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {

		builder = builder.setUri(getUri());

		if (parameters != null) {
			final Set<Map.Entry<String, String>> mapEntries = parameters.entrySet();
			for (final Map.Entry<String, String> entry : mapEntries) {
				builder = builder.addParameter(entry.getKey(), entry.getValue());
			}

		}

		setHttpRequest(builder.build());
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the formAction
	 */
	public String getFormAction() {
		return formAction;
	}

	/**
	 * @param formAction the formAction to set
	 */
	public void setFormAction(final String formAction) {
		this.formAction = formAction;
	}
}

