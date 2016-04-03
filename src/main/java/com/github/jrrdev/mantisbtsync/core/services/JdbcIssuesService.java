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
package com.github.jrrdev.mantisbtsync.core.services;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * Implementation of IssuesDao.
 * Use caches to avoid large amount of upsert.
 *
 * @author jrrdev
 *
 */
@Repository
public class JdbcIssuesService implements IssuesDao {

	/**
	 * SQL query used to check if the given project exists in the DB.
	 * Filtering is performed on id and name.
	 */
	private static final String SQL_CHECK_PROJECT = "SELECT count(1) FROM mantis_project_table\n"
			+ " WHERE id = ? AND name = ?";

	/**
	 * SQL query for upserting in mantis_project_table.
	 */
	private static final String SQL_MERGE_PROJECT_TABLE =
			"INSERT INTO mantis_project_table (id, name)\n"
					+ " VALUES (?, ?)\n"
					+ " ON DUPLICATE KEY UPDATE name = ?";

	/**
	 * SQL query used to check if the link between an user and a project exists in the DB.
	 */
	private static final String SQL_CHECK_USER_PROJECT = "SELECT count(1) FROM mantis_project_user_list_table\n"
			+ " WHERE project_id = ? AND user_id = ?";

	/**
	 * SQL query used to insert the link between an user and a project.
	 */
	private static final String SQL_INSERT_USER_PROJECT = "INSERT INTO mantis_project_user_list_table\n"
			+ " (project_id, user_id) values (?, ?)";

	/**
	 * SQL query used to check if the link between a custom field and a project exists in the DB.
	 */
	private static final String SQL_CHECK_CUSTOM_FIELD_PROJECT = "SELECT count(1) FROM mantis_custom_field_project_table\n"
			+ " WHERE project_id = ? AND field_id = ?";

	/**
	 * SQL query used to insert the link between a custom field and a project.
	 */
	private static final String SQL_INSERT_CUSTOM_FIELD_PROJECT = "INSERT INTO mantis_custom_field_project_table\n"
			+ " (project_id, field_id) values (?, ?)";

	/**
	 * SQL query used to get all issues that are still open and not synced since a given datetime.
	 */
	private static final String SQL_GET_NOT_CLOSED_ISSUES_ID = "SELECT bug.id FROM mantis_bug_table bug\n"
			+ " WHERE bug.status_id <> 90\n"
			+ " AND bug.last_sync <= ?";

	/**
	 * JDBC template.
	 */
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
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertProjectIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("projects")
	public boolean insertProjectIfNotExists(final ObjectRef item) {
		if (item != null) {
			final Boolean exists = jdbcTemplate.queryForObject(SQL_CHECK_PROJECT, Boolean.class, item.getId(), item.getName());

			if (!Boolean.TRUE.equals(exists)) {
				jdbcTemplate.update(SQL_MERGE_PROJECT_TABLE, item.getId(), item.getName(), item.getName());
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertUserIfNotExists(biz.futureware.mantis.rpc.soap.client.AccountData, java.math.BigInteger)
	 */
	@Override
	public void insertUserIfNotExists(final AccountData item,
			final BigInteger parentProjectId) {
		if (item != null) {
			insertIntoUserIfNotExists(item);
			insertIntoUserProjectIfNotExists(item, parentProjectId);
		}
	}

	/**
	 * Insert into the mantis_user_table user if the row doesn't exist.
	 * Marked as public for caching management.
	 *
	 * @param item
	 * 			The user account data
	 * @return dummy boolean, just for caching management
	 */
	@Cacheable("users")
	public boolean insertIntoUserIfNotExists(final AccountData item) {
		if (item != null && !existsById("mantis_user_table", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_user_table"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * Insert into mantis_project_user_list_table if the row doesn't exist.
	 * Marked as public for caching management.
	 *
	 * @param item
	 * 			The user account data
	 * @param parentProjectId
	 * 			The project id
	 * @return dummy boolean, just for caching management
	 */
	@Cacheable("users_project")
	public boolean insertIntoUserProjectIfNotExists(final AccountData item, final BigInteger parentProjectId) {

		if (item != null && parentProjectId != null) {
			final Boolean exist = jdbcTemplate.queryForObject(SQL_CHECK_USER_PROJECT, Boolean.class, parentProjectId, item.getId());
			if (!Boolean.TRUE.equals(exist)) {
				jdbcTemplate.update(SQL_INSERT_USER_PROJECT, parentProjectId, item.getId());
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertPriorityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("priorities")
	public boolean insertPriorityIfNotExists(final ObjectRef item) {
		if (item != null && !existsById("mantis_enum_priorities", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_priorities"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertSeverityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("severities")
	public boolean insertSeverityIfNotExists(final ObjectRef item) {
		if (item != null && !existsById("mantis_enum_severities", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_severities"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertStatusIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("status")
	public boolean insertStatusIfNotExists(final ObjectRef item) {
		if (item != null && !existsById("mantis_enum_status", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_status"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertResolutionIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)
	 */
	@Override
	@Cacheable("resolutions")
	public boolean insertResolutionIfNotExists(final ObjectRef item) {
		if (item != null && !existsById("mantis_enum_resolutions", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_enum_resolutions"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#insertCustomFieldIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef, java.math.BigInteger)
	 */
	@Override
	public void insertCustomFieldIfNotExists(final ObjectRef item,
			final BigInteger parentProjectId) {
		if (item != null) {
			insertIntoCustomFieldIfNotExists(item);
			insertIntoCustomFieldProjectIfNotExists(item, parentProjectId);
		}
	}

	/**
	 * Insert into mantis_custom_field_table if the row doesn't exist.
	 * Marked as public for caching management.
	 *
	 * @param item
	 * 			The custom field data
	 * @return dummy boolean, just for caching management
	 */
	@Cacheable("customFields")
	public boolean insertIntoCustomFieldIfNotExists(final ObjectRef item) {
		if (item != null && !existsById("mantis_custom_field_table", item.getId())) {
			jdbcTemplate.update(getInsertQueryIdName("mantis_custom_field_table"), item.getId(), item.getName());
		}

		return true;
	}

	/**
	 * Insert into mantis_custom_field_project_table if the row doesn't exist.
	 * Marked as public for caching management.
	 *
	 * @param item
	 * 			The custom field data
	 * @param parentProjectId
	 * 			The project id
	 * @return dummy boolean, just for caching management
	 */
	@Cacheable("customFields_project")
	public boolean insertIntoCustomFieldProjectIfNotExists(final ObjectRef item,
			final BigInteger parentProjectId) {
		if (item != null && parentProjectId != null) {
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
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#getNotClosedIssuesId(java.sql.Date)
	 */
	@Override
	public List<BigInteger> getNotClosedIssuesId(final Calendar jobStartTime) {
		final java.sql.Timestamp time = new java.sql.Timestamp(jobStartTime.getTimeInMillis());
		return jdbcTemplate.queryForList(SQL_GET_NOT_CLOSED_ISSUES_ID, BigInteger.class, time);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.jrrdev.mantisbtsync.core.services.IssuesDao#evictAllCaches()
	 */
	@Override
	@CacheEvict(value = {"projects", "users", "users_project", "priorities", "severities"
			, "status", "resolutions", "customFields", "customFields_project"}, allEntries = true)
	public void evictAllCaches() {
		// do nothing, everything is in the annotations
	}

	private boolean existsById(final String table, final BigInteger id) {
		final String sql = "SELECT count(1) FROM " + table +" WHERE id = ?";
		final Boolean exist = jdbcTemplate.queryForObject(sql, Boolean.class, id);

		return Boolean.TRUE.equals(exist);
	}

	/**
	 * Build the insert query for a given table which contains
	 * only id and name columns.
	 *
	 * @param table
	 * 			Table name
	 * @return the insert query
	 */
	private String getInsertQueryIdName(final String table) {

		return "INSERT INTO " + table + " (id, name) values (?, ?)";
	}

}
