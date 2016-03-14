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
package mantisbtsync.core.common;

import java.io.IOException;
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
	public PortalAuthBuilder authBuilder() {
		return new PortalAuthBuilder();
	}

	@Bean
	public PortalAuthManager authManager(@Value("${mantis.auth.filepath:}") final String filepath,
			final PortalAuthBuilder authBuilder) throws JAXBException, IOException {
		return authBuilder.buildAuthManager(filepath);
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
