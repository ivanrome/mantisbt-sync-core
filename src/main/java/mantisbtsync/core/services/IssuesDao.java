/**
 *
 */
package mantisbtsync.core.services;

import java.math.BigInteger;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;

/**
 * @author thor
 *
 */
public interface IssuesDao {

	public boolean insertProjectIfNotExists(final ObjectRef item);

	public void insertUserIfNotExists(final AccountData item, final BigInteger parentProjectId);

	public boolean insertPriorityIfNotExists(final ObjectRef item);

	public boolean insertSeverityIfNotExists(final ObjectRef item);

	public boolean insertStatusIfNotExists(final ObjectRef item);

	public boolean insertResolutionIfNotExists(final ObjectRef item);

	public void insertCustomFieldIfNotExists(final ObjectRef item, final BigInteger parentProjectId);
}
