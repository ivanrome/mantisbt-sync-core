/**
 *
 */
package mantisbtsync.core.common;

import java.net.MalformedURLException;
import java.net.URL;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import mantisbtsync.core.common.auth.PortalAuthBuilder;
import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.BasicClientConfig;
import org.springframework.beans.factory.annotation.Value;
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
	public PortalAuthManager authManager(@Value("${mantis.auth.filepath:}") final String filepath) throws JAXBException {
		return PortalAuthBuilder.buildAuthManager(filepath);
	}

	@Bean
	public Stub clientStub(@Value("${mantis.endpoint}") final String endpoint) throws AxisFault, MalformedURLException {

		if (endpoint == null) {
			throw new MalformedURLException("Mantis endpoint can't be null");
		}

		final MantisConnectLocator loc = new MantisConnectLocator(new BasicClientConfig());
		loc.setMantisConnectPortEndpointAddress(endpoint);
		final MantisConnectBindingStub stub = new MantisConnectBindingStub(new URL("http://www.mantisbt.org/bugs/api/soap/mantisconnect.php"), loc);
		return stub;
	}
}
