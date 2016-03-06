/**
 *
 */
package mantisbtsync.core.common;

import java.net.MalformedURLException;
import java.net.URL;

import javax.sql.DataSource;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.BasicClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.MantisConnectLocator;

/**
 * Common configuration for the app.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
public class CommonConfiguration {

	@Bean
	public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public PortalAuthManager authManager() {
		return new PortalAuthManager();
	}

	@Bean
	public Stub clientStub() throws AxisFault, MalformedURLException {
		final MantisConnectLocator loc = new MantisConnectLocator(new BasicClientConfig());
		loc.setMantisConnectPortEndpointAddress("http://www.mantisbt.org/bugs/api/soap/mantisconnect.php");
		final MantisConnectBindingStub stub = new MantisConnectBindingStub(new URL("http://www.mantisbt.org/bugs/api/soap/mantisconnect.php"), loc);
		return stub;
	}
}
