/**
 *
 */
package mantisbtsync.core.junit;

import javax.sql.DataSource;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Stub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;

import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;

/**
 * Configuration Spring pour les tests JUnit.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class JunitTestConfiguration {

	@Bean
	public Destination dbSetupDest(final DataSource dataSource) {
		return new DataSourceDestination(dataSource);
	}

	@Bean
	public PortalAuthManager authManager() {
		return new PortalAuthManager();
	}

	@Bean
	public Stub clientStub() throws AxisFault {
		return new MantisConnectBindingStub();
	}

}
