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
package com.github.jrrdev.mantisbtsync.core.services;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;

import com.github.jrrdev.mantisbtsync.core.junit.AbstractSqlWriterTest;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author jrrdev
 *
 */
public class JdbcIssuesServiceTest extends AbstractSqlWriterTest {

	@Autowired
	private IssuesDao dao;

	@Before
	@CacheEvict(value = {"projects", "users", "users_project", "priorities", "severities"
			, "status", "resolutions", "customFields", "customFields_project"}, allEntries = true)
	public void clearCaches() {
		// do nothing, everything is in the annotations
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertProjectIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertProjectIfNotExistsNull() {
		final boolean result = dao.insertProjectIfNotExists(null);
		assertTrue(result);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertProjectIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertProjectIfNotExists() {
		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		final boolean result = dao.insertProjectIfNotExists(item);
		assertTrue(result);

		final List<ObjectRef> list = getJdbcTemplate()
				.query("SELECT id, name FROM mantis_project_table",
						new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		final boolean result2 = dao.insertProjectIfNotExists(item);
		assertTrue(result2);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertUserIfNotExists(biz.futureware.mantis.rpc.soap.client.AccountData, java.math.BigInteger)}.
	 */
	@Test
	public void testInsertUserIfNotExistsItemNull() {
		final Operation op = insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build();

		lauchOperation(op);

		dao.insertUserIfNotExists(null, BigInteger.ONE);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertUserIfNotExists(biz.futureware.mantis.rpc.soap.client.AccountData, java.math.BigInteger)}.
	 */
	@Test
	public void testInsertUserIfNotExistsProjectNull() {
		final AccountData item = new AccountData();
		item.setId(BigInteger.valueOf(1));
		item.setName("new_user_1");

		dao.insertUserIfNotExists(item, null);

		final List<AccountData> list = getJdbcTemplate().query("SELECT usr.id, usr.name"
				+ " FROM mantis_user_table usr",
				new BeanPropertyRowMapper<AccountData>(AccountData.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertUserIfNotExists(biz.futureware.mantis.rpc.soap.client.AccountData, java.math.BigInteger)}.
	 */
	@Test
	public void testInsertUserIfNotExists() {
		final Operation op = insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build();

		lauchOperation(op);

		final AccountData item = new AccountData();
		item.setId(BigInteger.valueOf(1));
		item.setName("new_user_1");

		dao.insertUserIfNotExists(item, BigInteger.ONE);

		final List<AccountData> list = getJdbcTemplate().query("SELECT usr.id, usr.name"
				+ " FROM mantis_user_table usr"
				+ " INNER JOIN mantis_project_user_list_table pul ON pul.user_id = usr.id"
				+ " WHERE pul.project_id = 1",
				new BeanPropertyRowMapper<AccountData>(AccountData.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		dao.insertUserIfNotExists(item, BigInteger.ONE);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertPriorityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertPriorityIfNotExistsNull() {
		final boolean result = dao.insertPriorityIfNotExists(null);
		assertTrue(result);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertPriorityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertPriorityIfNotExists() {
		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		final boolean result = dao.insertPriorityIfNotExists(item);
		assertTrue(result);

		final List<ObjectRef> list = getJdbcTemplate()
				.query("SELECT id, name FROM mantis_enum_priorities",
						new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		final boolean result2 = dao.insertPriorityIfNotExists(item);
		assertTrue(result2);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertSeverityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertSeverityIfNotExistsNull() {
		final boolean result = dao.insertSeverityIfNotExists(null);
		assertTrue(result);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertSeverityIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertSeverityIfNotExists() {
		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		final boolean result = dao.insertSeverityIfNotExists(item);
		assertTrue(result);

		final List<ObjectRef> list = getJdbcTemplate()
				.query("SELECT id, name FROM mantis_enum_severities",
						new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		final boolean result2 = dao.insertSeverityIfNotExists(item);
		assertTrue(result2);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertStatusIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertStatusIfNotExistsNull() {
		final boolean result = dao.insertStatusIfNotExists(null);
		assertTrue(result);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertStatusIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertStatusIfNotExists() {
		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		final boolean result = dao.insertStatusIfNotExists(item);
		assertTrue(result);

		final List<ObjectRef> list = getJdbcTemplate()
				.query("SELECT id, name FROM mantis_enum_status",
						new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		final boolean result2 = dao.insertStatusIfNotExists(item);
		assertTrue(result2);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertResolutionIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertResolutionIfNotExistsNull() {
		final boolean result = dao.insertResolutionIfNotExists(null);
		assertTrue(result);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertResolutionIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef)}.
	 */
	@Test
	public void testInsertResolutionIfNotExists() {
		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		final boolean result = dao.insertResolutionIfNotExists(item);
		assertTrue(result);

		final List<ObjectRef> list = getJdbcTemplate()
				.query("SELECT id, name FROM mantis_enum_resolutions",
						new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		final boolean result2 = dao.insertResolutionIfNotExists(item);
		assertTrue(result2);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertCustomFieldIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef, java.math.BigInteger)}.
	 */
	@Test
	public void testInsertCustomFieldIfNotExistsItemNull() {
		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_enum_custom_field_types")
				.columns("id", "name")
				.values(1, "type_1")
				.build());

		lauchOperation(op);

		dao.insertCustomFieldIfNotExists(null, BigInteger.ONE);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertCustomFieldIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef, java.math.BigInteger)}.
	 */
	@Test
	public void testInsertCustomFieldIfNotExistsProjectNull() {
		final Operation op = insertInto("mantis_enum_custom_field_types")
				.columns("id", "name")
				.values(1, "type_1")
				.build();

		lauchOperation(op);

		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		dao.insertCustomFieldIfNotExists(item, null);

		final List<ObjectRef> list = getJdbcTemplate().query("SELECT cf.id, cf.name"
				+ " FROM mantis_custom_field_table cf",
				new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#insertCustomFieldIfNotExists(biz.futureware.mantis.rpc.soap.client.ObjectRef, java.math.BigInteger)}.
	 */
	@Test
	public void testInsertCustomFieldIfNotExists() {
		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_enum_custom_field_types")
				.columns("id", "name")
				.values(1, "type_1")
				.build());

		lauchOperation(op);

		final ObjectRef item = new ObjectRef(BigInteger.ONE, "item");
		dao.insertCustomFieldIfNotExists(item, BigInteger.ONE);

		final List<ObjectRef> list = getJdbcTemplate().query("SELECT cf.id, cf.name"
				+ " FROM mantis_custom_field_table cf"
				+ " INNER JOIN mantis_custom_field_project_table cfp ON cf.id = cfp.field_id"
				+ " WHERE cfp.project_id = 1",
				new BeanPropertyRowMapper<ObjectRef>(ObjectRef.class));

		assertEquals(1, list.size());
		assertEquals(item, list.get(0));

		dao.insertCustomFieldIfNotExists(item, BigInteger.ONE);
	}

	/**
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#getNotClosedIssuesId(java.util.Calendar)}.
	 */
	@Test
	public void testGetNotClosedIssuesId() {

		final Calendar cal = Calendar.getInstance();
		final Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 10);

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());
		final Timestamp after = new java.sql.Timestamp(cal2.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.values(2, "project_2")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.values(90, "Close")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "status_id")
				.values(1, 1, "sum", before, 1)
				.values(2, 1, "sum", after, 1)
				.values(3, 1, "sum", before, 90)
				.values(4, 2, "sum", before, 1)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		final List<BigInteger> list = dao.getNotClosedIssuesId(cal, BigInteger.ONE);
		assertEquals(1, list.size());
		assertEquals(BigInteger.ONE, list.get(0));
	}

	/**
	 * Tests that issues of a subproject are retrieved.
	 *
	 * Test method for {@link com.github.jrrdev.mantisbtsync.core.services.JdbcIssuesService#getNotClosedIssuesId(java.util.Calendar)}.
	 */
	@Test
	public void testGetNotClosedIssuesIdSubProject() {

		final Calendar cal = Calendar.getInstance();

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_parent")
				.values(2, "project_child")
				.build(),

				insertInto("mantis_project_hierarchy_table")
				.columns("parent_id", "child_id")
				.values(1, 2)
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.values(90, "Close")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "status_id")
				.values(1, 2, "sum", before, 1)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		final List<BigInteger> list = dao.getNotClosedIssuesId(cal, BigInteger.ONE);
		assertEquals(1, list.size());
		assertEquals(BigInteger.ONE, list.get(0));
	}

	/**
	 * Test for correct project assignment when computing handler stats.
	 *
	 * Test method for {@link JdbcIssuesService#computeHandlersStat(Calendar)}.
	 */
	@Test
	public void testComputeHandlersStatProject() {

		final Calendar cal = Calendar.getInstance();

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.values(2, "project_2")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "user 1")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "date_submitted" , "status_id", "handler_id")
				.values(1, 1, "sum", before, before, 1, 1)
				.values(2, 1, "sum", before, before, 1, 1)
				.values(3, 2, "sum", before, before, 1, 1)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		dao.computeHandlersStat(cal);
		final Integer nbProject1 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE project_id = 1", Integer.class);
		final Integer nbProject2 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE project_id = 2", Integer.class);

		assertEquals(Integer.valueOf(2), nbProject1);
		assertEquals(Integer.valueOf(1), nbProject2);
	}

	/**
	 * Test for correct user assignment when computing handler stats.
	 *
	 * Test method for {@link JdbcIssuesService#computeHandlersStat(Calendar)}.
	 */
	@Test
	public void testComputeHandlersStatUser() {

		final Calendar cal = Calendar.getInstance();

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "user 1")
				.values(2, "user 2")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "date_submitted" , "status_id", "handler_id")
				.values(1, 1, "sum", before, before, 1, 1)
				.values(2, 1, "sum", before, before, 1, 1)
				.values(3, 1, "sum", before, before, 1, 2)
				.values(4, 1, "sum", before, before, 1, null)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		dao.computeHandlersStat(cal);
		final Integer nbUser1 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE handler_id = 1", Integer.class);
		final Integer nbUser2 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE handler_id = 2", Integer.class);
		final Integer nbNoUser = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE handler_id is null", Integer.class);

		assertEquals(Integer.valueOf(2), nbUser1);
		assertEquals(Integer.valueOf(1), nbUser2);
		assertEquals(Integer.valueOf(1), nbNoUser);
	}

	/**
	 * Test for correct status assignment when computing handler stats.
	 *
	 * Test method for {@link JdbcIssuesService#computeHandlersStat(Calendar)}.
	 */
	@Test
	public void testComputeHandlersStatStatus() {

		final Calendar cal = Calendar.getInstance();

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "user 1")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.values(90, "Close")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "date_submitted" , "status_id", "handler_id")
				.values(1, 1, "sum", before, before, 1, 1)
				.values(2, 1, "sum", before, before, 1, 1)
				.values(3, 1, "sum", before, before, 90, 1)
				.values(4, 1, "sum", before, before, null, 1)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		dao.computeHandlersStat(cal);
		final Integer nbStatus1 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE status_id = 1", Integer.class);
		final Integer nbStatus2 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE status_id = 90", Integer.class);
		final Integer nbNoStatus = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE status_id is null", Integer.class);

		assertEquals(Integer.valueOf(2), nbStatus1);
		assertEquals(Integer.valueOf(1), nbStatus2);
		assertEquals(Integer.valueOf(1), nbNoStatus);
	}

	/**
	 * Tests that issues opened after the given date are not taken into account.
	 *
	 * Test method for {@link JdbcIssuesService#computeHandlersStat(Calendar)}.
	 */
	@Test
	public void testComputeHandlersStatDateSubmitted() {

		final Calendar cal = Calendar.getInstance();
		final Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 10);

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());
		final Timestamp after = new java.sql.Timestamp(cal2.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "user 1")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "date_submitted" , "status_id", "handler_id")
				.values(1, 1, "sum", before, before, 1, 1)
				.values(2, 1, "sum", before, after, 1, 1)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		dao.computeHandlersStat(cal);
		final Integer nbIssues = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE project_id = 1", Integer.class);

		assertEquals(Integer.valueOf(1), nbIssues);
	}

	/**
	 * Tests that changes of handler are taken into account.
	 *
	 * Test method for {@link JdbcIssuesService#computeHandlersStat(Calendar)}.
	 */
	@Test
	public void testComputeHandlersStatHistoryHandler() {

		final Calendar cal = Calendar.getInstance();
		final Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 3);
		final Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.MINUTE, 10);

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());
		final Timestamp during = new java.sql.Timestamp(cal2.getTimeInMillis());
		final Timestamp after = new java.sql.Timestamp(cal3.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "user 1")
				.values(2, "user 2")
				.values(3, "user 3")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "date_submitted" , "status_id", "handler_id")
				.values(1, 1, "sum", after, before, 1, 3)
				.values(2, 1, "sum", before, before, 1, 1)
				.values(3, 1, "sum", before, before, 1, 2)
				.values(4, 1, "sum", before, before, 1, 3)
				.build(),

				insertInto("mantis_bug_history_table")
				.columns("bug_id", "user_id", "field_name", "old_value", "new_value", "date_modified")
				.values(1, 1, "handler_id", 1, 2, during)
				.values(1, 1, "handler_id", 2, 3, after)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		dao.computeHandlersStat(cal);
		final Integer nbUser1 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE handler_id = 1", Integer.class);
		final Integer nbUser2 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE handler_id = 2", Integer.class);
		final Integer nbUser3 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE handler_id = 3", Integer.class);

		assertEquals(Integer.valueOf(1), nbUser1);
		assertEquals(Integer.valueOf(2), nbUser2);
		assertEquals(Integer.valueOf(1), nbUser3);
	}

	/**
	 * Tests that changes of status are taken into account.
	 *
	 * Test method for {@link JdbcIssuesService#computeHandlersStat(Calendar)}.
	 */
	@Test
	public void testComputeHandlersStatHistoryStatus() {

		final Calendar cal = Calendar.getInstance();
		final Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 3);
		final Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.MINUTE, 10);

		final Timestamp before = new java.sql.Timestamp(cal.getTimeInMillis());
		final Timestamp during = new java.sql.Timestamp(cal2.getTimeInMillis());
		final Timestamp after = new java.sql.Timestamp(cal3.getTimeInMillis());

		final Operation op = sequenceOf(
				insertInto("mantis_project_table")
				.columns("id", "name")
				.values(1, "project_1")
				.build(),

				insertInto("mantis_user_table")
				.columns("id", "name")
				.values(1, "user 1")
				.build(),

				insertInto("mantis_enum_status")
				.columns("id", "name")
				.values(1, "Open")
				.values(2, "Assigned")
				.values(90, "Closed")
				.build(),

				insertInto("mantis_bug_table")
				.columns("id", "project_id", "summary", "last_sync", "date_submitted" , "status_id", "handler_id")
				.values(1, 1, "sum", after, before, 90, 1)
				.values(2, 1, "sum", before, before, 1, 1)
				.values(3, 1, "sum", before, before, 2, 1)
				.values(4, 1, "sum", before, before, 90, 1)
				.build(),

				insertInto("mantis_bug_history_table")
				.columns("bug_id", "user_id", "field_name", "old_value", "new_value", "date_modified")
				.values(1, 1, "status", 1, 2, during)
				.values(1, 1, "status", 2, 90, after)
				.build());

		lauchOperation(op);

		cal.add(Calendar.MINUTE, 5);

		dao.computeHandlersStat(cal);
		final Integer nbStatus1 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE status_id = 1", Integer.class);
		final Integer nbStatus2 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE status_id = 2", Integer.class);
		final Integer nbStatus3 = getJdbcTemplate().queryForObject(
				"SELECT nb_issues FROM handlers_stats WHERE status_id = 90", Integer.class);

		assertEquals(Integer.valueOf(1), nbStatus1);
		assertEquals(Integer.valueOf(2), nbStatus2);
		assertEquals(Integer.valueOf(1), nbStatus3);
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

}
