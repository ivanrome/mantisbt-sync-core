/**
 *
 */
package mantisbtsync.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Entry point for the application.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
@EnableAutoConfiguration
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
