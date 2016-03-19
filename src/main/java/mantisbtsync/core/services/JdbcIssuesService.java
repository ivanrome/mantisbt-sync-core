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
package mantisbtsync.core.services;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * @author jdevarulrajah
 *
 */
@Repository
public class JdbcIssuesService implements IssuesDao {

	private static final String SQL_CHECK_PROJECT = "SELECT count(1) FROM mantis_project_table\n"
			+ " WHERE id = ? AND name = ?";

	private static final String SQL_MERGE_PROJECT_TABLE =
			"MERGE INTO mantis_project_table dest\n"
					+ " USING (SELECT ? as id, ? as name FROM dual) src\n"
					+ " ON (dest.id = src.id)\n"
					+ " WHEN NOT MATCHED THEN INSERT (id, name) VALUES (src.id, src.name)";

	private static final String SQL_CHECK_USER_PROJECT = "SELECT count(1) FROM mantis_project_user_list_table\n"
			+ " WHERE project_id = ? AND user_id = ?";

	private static final String SQL_INSERT_USER_PROJECT = "INSERT INTO mantis_project_user_list_table\n"
			+ " (project_id, user_id) values (?, ?)";

	private static final String SQL_CHECK_CUSTOM_FIELD_PROJECT = "SELECT count(1) FROM mantis_custom_field_project_table\n"
			+ " WHERE project_id = ? AND field_id = ?";

	private static final String SQL_INSERT_CUSTOM_FIELD_PROJECT = "INSERT INTO mantis_custom_field_project_table\n"
			+ " (project_id, field_id) values (?, ?)";

	private static final String SQL_GET_BIGGEST_ID = "SELECT NVL(MAX(bug.id), 0) FROM mantis_bug_table bug";

	private static final String SQL_GET_NOT_CLOSED_ISSUES_ID = "SELECT bug.id FROM mantis_bug_table bug\n"
			+ " WHERE bug.status_id <> 90\n"
			+ " AND bug.last_sync <= ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertProjectIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("projects")
	public boolean insertProjectIfNotExists(final ObjectRef item) {
		final Boolean exists = jdbcTemplate.queryForObject(SQL_CHECK_PROJECT, Boolean.class, item.getId(), item.getName());

		if (!Boolean.TRUE.equals(exists)) {
			jdbcTemplate.update(SQL_MERGE_PROJECT_TABLE, item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertUserIfNotExists(biz.futureware.mantis.rpc.soap.client.AccountData, java.math.BigInteger)
	 */
	@Override
	public void insertUserIfNotExists(final AccountData item,
			final BigInteger parentProjectId) {
		insertIntoUserIfNotExists(item);
		insertIntoUserProjectIfNotExists(item, parentProjectId);
	}

	@Cacheable("users")
	public boolean insertIntoUserIfNotExists(final AccountData item) {
		if (!existsById("mantis_user_table", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_user_table"), item.getId(), item.getName());
		}

		return true;
	}

	@Cacheable("users_project")
	public boolean insertIntoUserProjectIfNotExists(final AccountData item, final BigInteger parentProjectId) {

		if (parentProjectId != null) {
			final Boolean exist = jdbcTemplate.queryForObject(SQL_CHECK_USER_PROJECT, Boolean.class, parentProjectId, item.getId());
			if (!Boolean.TRUE.equals(exist)) {
				jdbcTemplate.update(SQL_INSERT_USER_PROJECT, parentProjectId, item.getId());
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertPriorityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("priorities")
	public boolean insertPriorityIfNotExists(final ObjectRef item) {
		if (!existsById("mantis_enum_priorities", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_priorities"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertSeverityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("severities")
	public boolean insertSeverityIfNotExists(final ObjectRef item) {
		if (!existsById("mantis_enum_severities", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_severities"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertStatusIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("status")
	public boolean insertStatusIfNotExists(final ObjectRef item) {
		if (!existsById("mantis_enum_status", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_status"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertResolutionIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("resolutions")
	public boolean insertResolutionIfNotExists(final ObjectRef item) {
		if (!existsById("mantis_enum_resolutions", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_resolutions"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#insertCustomFieldIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef, java.math.BigInteger)
	 */
	@Override
	public void insertCustomFieldIfNotExists(final ObjectRef item,
			final BigInteger parentProjectId) {
		insertIntoCustomFieldIfNotExists(item);
		insertIntoCustomFieldProjectIfNotExists(item, parentProjectId);
	}

	@Cacheable("customFields")
	public boolean insertIntoCustomFieldIfNotExists(final ObjectRef item) {
		if (!existsById("mantis_custom_field_table", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_custom_field_table"), item.getId(), item.getName());
		}

		return true;
	}

	@Cacheable("customFields_project")
	public boolean insertIntoCustomFieldProjectIfNotExists(final ObjectRef item,
			final BigInteger parentProjectId) {
		if (parentProjectId != null) {
			final Boolean exist = jdbcTemplate.queryForObject(SQL_CHECK_CUSTOM_FIELD_PROJECT,
					Boolean.class, parentProjectId, item.getId());
			if (!Boolean.TRUE.equals(exist)) {
				jdbcTemplate.update(SQL_INSERT_CUSTOM_FIELD_PROJECT, parentProjectId, item.getId());
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#getIssuesBiggestId()
	 */
	@Override
	public BigInteger getIssuesBiggestId() {
		return jdbcTemplate.queryForObject(SQL_GET_BIGGEST_ID, BigInteger.class);
	}

	/**
	 * {@inheritDoc}
	 * @see mantisbtsync.core.services.IssuesDao#getNotClosedIssuesId(java.sql.Date)
	 */
	@Override
	public List<BigInteger> getNotClosedIssuesId(final Calendar jobStartTime) {
		final java.sql.Timestamp time = new java.sql.Timestamp(jobStartTime.getTimeInMillis());
		return jdbcTemplate.queryForList(SQL_GET_NOT_CLOSED_ISSUES_ID, BigInteger.class, time);
	}

	private boolean existsById(final String table, final BigInteger id) {
		final String sql = "SELECT count(1) FROM " + table +" WHERE id = ?";
		final Boolean exist = jdbcTemplate.queryForObject(sql, Boolean.class, id);

		return Boolean.TRUE.equals(exist);
	}

	private String getInsertQueryIdName(final String table) {

		return "INSERT INTO " + table + " (id, name) values (?, ?)";
	}

}
