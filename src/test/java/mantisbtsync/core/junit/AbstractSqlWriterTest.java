/**
 *
 */
package mantisbtsync.core.junit;

import mantisbtsync.core.Application;

import org.junit.runner.RunWith;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * Abstract Junit class to test writing to database.
 * Hold the DBSetup configuration.
 *
 * @author jdevarulrajah
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({Application.class, JunitTestConfiguration.class})
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
	StepScopeTestExecutionListener.class })
public abstract class AbstractSqlWriterTest {

	@Autowired
	Destination dbSetupDest;

	@Autowired
	JdbcTemplate jdbcTemplate;

	protected final void lauchOperation(final Operation op) {
		new DbSetup(dbSetupDest, op).launch();
	}

	protected final Destination getDbSetupDest() {
		return dbSetupDest;
	}

	public final void setDbSetupDest(final Destination dbSetupDest) {
		this.dbSetupDest = dbSetupDest;
	}

	protected final JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public final void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
