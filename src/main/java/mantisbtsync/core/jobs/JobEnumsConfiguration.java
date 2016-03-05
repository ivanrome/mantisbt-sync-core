/**
 *
 */
package mantisbtsync.core.jobs;

import mantisbtsync.core.jobs.enums.EnumsReadersConfiguration;
import mantisbtsync.core.jobs.enums.EnumsWritersConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the job of Mantis enumerations syncing.
 *
 * @author jdevarulrajah
 *
 */
@Configuration
@Import({EnumsWritersConfiguration.class, EnumsReadersConfiguration.class})
public class JobEnumsConfiguration {

}
