/**
 *
 */
package mantisbtsync.core.jobs.issues.readers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.beans.BugCustomFieldValue;
import mantisbtsync.core.jobs.issues.beans.BugHistoryBean;
import mantisbtsync.core.jobs.issues.beans.BugNoteBean;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.Assert;

import biz.futureware.mantis.rpc.soap.client.CustomFieldValueForIssueData;
import biz.futureware.mantis.rpc.soap.client.HistoryData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.IssueNoteData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * @author jdevarulrajah
 *
 */
public class IssuesReader implements ItemReader<BugBean> {

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Client stub generate by Apache Axis.
	 */
	private MantisConnectBindingStub clientStub;

	private int currentPage = 0;

	/**
	 * Index of the last read item.
	 */
	private int i = -1;

	/**
	 * Results array got from the WS.
	 */
	private IssueData[] items;

	private static final BigInteger PAGE_SIZE = BigInteger.valueOf(20);

	/**
	 * Mantis username.
	 */
	private String userName;

	/**
	 * Pantis password.
	 */
	private String password;

	private Calendar lastJobRun = null;

	private BigInteger projectId;

	@Override
	public BugBean read() throws Exception, UnexpectedInputException,
	ParseException, NonTransientResourceException {

		Assert.notNull(clientStub);

		// If auth manager is set, try to get the cookie
		if (authManager != null && authManager.getAuthCookie() != null) {
			clientStub._setProperty(HTTPConstants.HEADER_COOKIE,
					authManager.getAuthCookie());
		}

		if (i < 0 || i >= (items.length - 1)) {
			currentPage++;
			i = -1;
			items = clientStub.mc_project_get_issues(userName, password, projectId,
					BigInteger.valueOf(currentPage), PAGE_SIZE);
		}

		i++;
		if (items != null && i < items.length) {
			final IssueData item = items[i];

			if (item != null
					&& (lastJobRun == null
					|| item.getLast_updated() == null
					|| item.getLast_updated().after(lastJobRun))) {

				final BugBean bean = getBeanFromDto(item);
				final HistoryData[] histories = clientStub.mc_issue_get_history(userName, password, item.getId());
				fillHistory(bean, histories);
				return bean;

			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * @return the authManager
	 */
	public PortalAuthManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param authManager the authManager to set
	 */
	public void setAuthManager(final PortalAuthManager authManager) {
		this.authManager = authManager;
	}

	/**
	 * @return the clientStub
	 */
	public MantisConnectBindingStub getClientStub() {
		return clientStub;
	}

	/**
	 * @param clientStub the clientStub to set
	 */
	public void setClientStub(final MantisConnectBindingStub clientStub) {
		this.clientStub = clientStub;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the lastJobRun
	 */
	public Calendar getLastJobRun() {
		return lastJobRun;
	}

	/**
	 * @param pLastJobRun the lastJobRun to set
	 */
	public void setLastJobRun(final Date pLastJobRun) {
		if (pLastJobRun != null) {
			lastJobRun = Calendar.getInstance();
			lastJobRun.setTime(pLastJobRun);
		} else {
			lastJobRun = null;
		}
	}

	/**
	 * @return the projectId
	 */
	public BigInteger getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(final BigInteger projectId) {
		this.projectId = projectId;
	}

	private BugBean getBeanFromDto(final IssueData data) {
		final BugBean bean = new BugBean();
		bean.setId(data.getId());

		if (data.getProject() != null) {
			bean.setProjectId(data.getProject().getId());
		}

		if (data.getReporter() != null) {
			bean.setReporterId(data.getReporter().getId());
		}

		if (data.getHandler() != null) {
			bean.setHandlerId(data.getHandler().getId());
		}

		if (data.getPriority() != null) {
			bean.setPriorityId(data.getPriority().getId());
		}

		if (data.getSeverity() != null) {
			bean.setSeverityId(data.getSeverity().getId());
		}

		if (data.getStatus() != null) {
			bean.setStatusId(data.getStatus().getId());
		}

		if (data.getResolution() != null) {
			bean.setResolutionId(data.getResolution().getId());
		}

		bean.setDescription(data.getDescription());
		bean.setStepsToReproduce(data.getSteps_to_reproduce());
		bean.setAdditionalInformation(data.getAdditional_information());
		bean.setPlatform(data.getPlatform());
		bean.setVersion(data.getVersion());
		bean.setFixedInVersion(data.getFixed_in_version());
		bean.setTargetVersion(data.getTarget_version());
		bean.setSummary(data.getSummary());
		bean.setCategory(data.getCategory());

		if (data.getDate_submitted() != null) {
			bean.setDateSubmitted(data.getDate_submitted().getTime());
		}

		if (data.getLast_updated() != null) {
			bean.setLastUpdated(data.getLast_updated().getTime());
		}

		fillNotes(bean, data);
		fillCustomField(bean, data);

		return bean;
	}

	private void fillNotes(final BugBean bean, final IssueData data) {
		if (data != null && data.getNotes() != null && bean != null) {

			final IssueNoteData[] notes = data.getNotes();
			for (final IssueNoteData noteData : notes) {
				final BugNoteBean noteBean = new BugNoteBean();
				noteBean.setId(noteData.getId());
				noteBean.setBugId(bean.getId());
				if (noteData.getReporter() != null) {
					noteBean.setReporterId(noteData.getReporter().getId());
				}
				noteBean.setText(noteData.getText());
				if (noteData.getDate_submitted() != null) {
					noteBean.setDateSubmitted(noteData.getDate_submitted().getTime());
				}
				if (noteData.getLast_modified() != null) {
					noteBean.setLastModified(noteData.getLast_modified().getTime());
				}

				bean.getNotes().add(noteBean);
			}
		}
	}

	private void fillCustomField(final BugBean bean, final IssueData data) {
		if (data != null && data.getCustom_fields() != null && bean != null) {

			final CustomFieldValueForIssueData[] valuesData = data.getCustom_fields();
			for (final CustomFieldValueForIssueData value : valuesData) {
				final BugCustomFieldValue valueBean = new BugCustomFieldValue();

				if (value.getField() != null) {
					valueBean.setFieldId(value.getField().getId());
				}

				valueBean.setBugId(bean.getId());
				valueBean.setFieldValue(value.getValue());

				bean.getCustomFields().add(valueBean);
			}
		}
	}

	private void fillHistory(final BugBean bean, final HistoryData[] histories) {
		if (histories != null && bean != null) {

			final Calendar cal = Calendar.getInstance();

			for (final HistoryData histData : histories) {
				final BugHistoryBean histBean = new BugHistoryBean();
				histBean.setBugId(bean.getId());
				histBean.setUserId(histData.getUserid());
				histBean.setFieldName(histData.getField());
				histBean.setOldValue(histData.getOld_value());
				histBean.setNewValue(histData.getNew_value());
				histBean.setHistoryType(histData.getType());
				if (histData.getDate() != null) {
					cal.setTimeInMillis(histData.getDate().longValue() * 1000L);
					histBean.setDateModified(cal.getTime());
				}
			}
		}
	}
}
