/**
 *
 */
package mantisbtsync.core.jobs.issues.processors;

import java.math.BigInteger;
import java.util.Calendar;

import mantisbtsync.core.common.auth.PortalAuthManager;
import mantisbtsync.core.jobs.issues.beans.BugBean;
import mantisbtsync.core.jobs.issues.beans.BugCustomFieldValue;
import mantisbtsync.core.jobs.issues.beans.BugHistoryBean;
import mantisbtsync.core.jobs.issues.beans.BugNoteBean;
import mantisbtsync.core.services.IssuesDao;

import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.CustomFieldValueForIssueData;
import biz.futureware.mantis.rpc.soap.client.HistoryData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.IssueNoteData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

/**
 * @author jdevarulrajah
 *
 */
public class IssuesProcessor implements ItemProcessor<IssueData, BugBean> {

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Client stub generate by Apache Axis.
	 */
	private MantisConnectBindingStub clientStub;

	/**
	 * Mantis username.
	 */
	private String userName;

	/**
	 * Pantis password.
	 */
	private String password;

	@Autowired
	private IssuesDao dao;

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
	 * @return the dao
	 */
	public IssuesDao getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(final IssuesDao dao) {
		this.dao = dao;
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public BugBean process(final IssueData item) throws Exception {

		Assert.notNull(clientStub);

		// If auth manager is set, try to get the cookie
		if (authManager != null && authManager.getAuthCookie() != null) {
			clientStub._setProperty(HTTPConstants.HEADER_COOKIE,
					authManager.getAuthCookie());
		}

		insertIssueDependencies(item);
		final BugBean bean = getBeanFromDto(item);

		final HistoryData[] histories = clientStub.mc_issue_get_history(userName, password, item.getId());
		insertHistoryDependencies(histories, bean.getProjectId());
		fillHistory(bean, histories);

		return bean;
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
			bean.setDateSubmitted(getSqlDate(data.getDate_submitted()));
		}

		if (data.getLast_updated() != null) {
			bean.setLastUpdated(getSqlDate(data.getLast_updated()));
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
				noteBean.setTextNote(noteData.getText());
				if (noteData.getDate_submitted() != null) {
					noteBean.setDateSubmitted(getSqlDate(noteData.getDate_submitted()));
				}
				if (noteData.getLast_modified() != null) {
					noteBean.setLastModified(getSqlDate(noteData.getLast_modified()));
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
					histBean.setDateModified(getSqlDate(cal));
				}

				bean.getHistory().add(histBean);
			}
		}
	}

	private java.sql.Timestamp getSqlDate(final Calendar cal) {
		return new java.sql.Timestamp(cal.getTimeInMillis());
	}

	private void insertIssueDependencies(final IssueData item) {
		final BigInteger projectId;
		if (item.getProject() != null) {
			projectId = item.getProject().getId();
		} else {
			projectId = null;
		}

		dao.insertProjectIfNotExists(item.getProject());
		dao.insertUserIfNotExists(item.getReporter(), projectId);
		dao.insertUserIfNotExists(item.getReporter(), projectId);
		dao.insertPriorityIfNotExists(item.getPriority());
		dao.insertSeverityIfNotExists(item.getSeverity());
		dao.insertStatusIfNotExists(item.getStatus());
		dao.insertResolutionIfNotExists(item.getResolution());

		if (item.getNotes() != null) {
			final IssueNoteData[] notes = item.getNotes();
			for (final IssueNoteData noteData : notes) {
				dao.insertUserIfNotExists(noteData.getReporter(), projectId);
			}
		}

		if (item.getCustom_fields() != null) {
			final CustomFieldValueForIssueData[] valuesData = item.getCustom_fields();
			for (final CustomFieldValueForIssueData value : valuesData) {
				if (value.getField() != null) {
					dao.insertCustomFieldIfNotExists(value.getField(), projectId);
				}
			}
		}
	}

	private void insertHistoryDependencies(final HistoryData[] histories, final BigInteger projectId) {
		if (histories != null) {
			for (final HistoryData histData : histories) {
				if (histData.getUserid() != null) {
					final AccountData usr = new AccountData();
					usr.setId(histData.getUserid());
					usr.setName(histData.getUsername());

					dao.insertUserIfNotExists(usr, projectId);
				}
			}
		}
	}

}
