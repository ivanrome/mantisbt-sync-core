/**
 *
 */
package mantisbtsync.core.jobs.projects.decider;

import java.math.BigInteger;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * Decider to know if we need to loop on the flow or if all projects
 * have been processed.
 *
 * @author jdevarulrajah
 *
 */
public class ProjectFlowDecider implements JobExecutionDecider {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.batch.core.job.flow.JobExecutionDecider#decide(org.springframework.batch.core.JobExecution, org.springframework.batch.core.StepExecution)
	 */
	@Override
	public FlowExecutionStatus decide(final JobExecution jobExecution,
			final StepExecution stepExecution) {

		final Set<BigInteger> projectsId = (Set<BigInteger>) jobExecution.getExecutionContext()
				.get("mantis.loop.projects_to_process");

		if (projectsId != null && !projectsId.isEmpty()) {
			return new FlowExecutionStatus("LOOP");
		} else {
			return new FlowExecutionStatus("END_LOOP");
		}
	}

}
