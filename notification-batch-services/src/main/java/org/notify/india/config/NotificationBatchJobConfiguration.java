package org.notify.india.config;

import org.notify.india.constants.JobConstants;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationBatchJobConfiguration {
	//Configuration related to jobs
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private JobParametersValidator notificationJobParametersValidator;
	
	@Bean
	JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
		postProcessor.setJobRegistry(jobRegistry);
		return postProcessor;
	}
	
	@Bean
	public Job notificationBatchJob(Step step) throws Exception{
		return this.jobBuilderFactory.get(JobConstants.JOB_NAME)
									 .validator(notificationJobParametersValidator)
									 .start(step)
									 .build();
	}
	
	//configuration related to step
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step step() throws Exception{
		return this.stepBuilderFactory.get(JobConstants.STEP_NAME).tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.err.print("Hello All!!!");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
}
