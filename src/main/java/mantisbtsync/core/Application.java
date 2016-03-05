/**
 *
 */
package mantisbtsync.core;

import mantisbtsync.core.common.CommonConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Entry point for the application.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
@EnableAutoConfiguration
@Import(CommonConfiguration.class)
public class Application {

	/**
	 * Entry point method.
	 *
	 * @param args
	 * 			Command line arguments
	 */
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
