package mantisbtsync.core.common.auth.beans;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "formAction", "parameters"})
public class HttpPostBean extends AbstractHttpRequestTypeBean {

	private String formAction;

	private Map<String, String> parameters;

	/**
	 * Default constructor.
	 */
	public HttpPostBean() {
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
	@XmlElement
	public void setFormAction(final String formAction) {
		this.formAction = formAction;
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
	@XmlElement(nillable = true)
	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
