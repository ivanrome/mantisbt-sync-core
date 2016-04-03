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

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * DAO service for issues related operations.
 * Those operations are mainly insertions of dependencies
 * for Foreign Keys resolutions. Those operations should be
 * cached to avoid having a large amount of upsert in the batch.
 *
 * @author jrrdev
 *
 */
public interface IssuesDao {

	/**
	 * Insert a project if it doesn't exist in the DB.
	 *
	 * @param item
	 * 			The project
	 * @return dummy boolean, just for caching management
	 */
	public boolean insertProjectIfNotExists(final ObjectRef item);

	/**
	 * Insert a user if it doesn't exist in the DB.
	 *
	 * @param item
	 * 			The user
	 * @param parentProjectId
	 * 			The project id
	 */
	public void insertUserIfNotExists(final AccountData item, final BigInteger parentProjectId);

	/**
	 * Add a priority in the enumeration if it doesn't exist in the DB.
	 *
	 * @param item
	 * 			the data
	 * @return dummy boolean, just for caching management
	 */
	public boolean insertPriorityIfNotExists(final ObjectRef item);

	/**
	 * Add a severity in the enumeration if it doesn't exist in the DB.
	 *
	 * @param item
	 * 			the data
	 * @return dummy boolean, just for caching management
	 */
	public boolean insertSeverityIfNotExists(final ObjectRef item);

	/**
	 * Add a status in the enumeration if it doesn't exist in the DB.
	 *
	 * @param item
	 * 			the data
	 * @return dummy boolean, just for caching management
	 */
	public boolean insertStatusIfNotExists(final ObjectRef item);

	/**
	 * Add a resolution in the enumeration if it doesn't exist in the DB.
	 *
	 * @param item
	 * 			the data
	 * @return dummy boolean, just for caching management
	 */
	public boolean insertResolutionIfNotExists(final ObjectRef item);

	/**
	 * Add a custom field to a project if it doesn't exist in the DB.
	 * @param item
	 * 			The data of the custom field
	 * @param parentProjectId
	 * 			The project id
	 */
	public void insertCustomFieldIfNotExists(final ObjectRef item, final BigInteger parentProjectId);

	/**
	 * Get the issues still open in the DB and that wasn't synced since
	 * the given time
	 *
	 * @param jobStartTime
	 * 			Time used for filtering
	 * @return the list of issues ids
	 */
	public List<BigInteger> getNotClosedIssuesId(Calendar jobStartTime);

	/**
	 * Evict all caches.
	 */
	public void evictAllCaches();
}
