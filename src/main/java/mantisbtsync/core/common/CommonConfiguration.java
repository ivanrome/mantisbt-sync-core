/**
 *
 */
package mantisbtsync.core.common;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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
}
