/**
 *
 */
package mantisbtsync.core.jobs.projects.tasklets;

import java.math.BigInteger;
import java.util.Set;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author thor
 *
 */
public class ProjectsExtractorTasklet implements Tasklet {

	/**
	 * {@inheritDoc}
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(final StepContribution contribution,
			final ChunkContext chunkContext) throws Exception {

		final Set<BigInteger> projectsId = (Set<BigInteger>) chunkContext.getStepContext()
				.getJobExecutionContext()
				.get("mantis.loop.projects_to_process");

		if (projectsId != null && !projectsId.isEmpty()) {
			final BigInteger id = projectsId.iterator().next();
			projectsId.remove(id);
			chunkContext.getStepContext().getStepExecution().getExecutionContext()
			.put("mantis.loop.project_id", id);
		} else {
			chunkContext.getStepContext().getStepExecution().getExecutionContext()
			.remove("mantis.loop.project_id");
		}

		return RepeatStatus.FINISHED;
	}

}
