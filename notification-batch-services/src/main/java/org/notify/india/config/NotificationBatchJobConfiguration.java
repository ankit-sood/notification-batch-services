package org.notify.india.config;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.notify.india.constants.JobConstants;
import org.notify.india.model.Notification;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

@Configuration
public class NotificationBatchJobConfiguration {
	//Configuration related to jobs
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private JobParametersValidator notificationJobParametersValidator;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
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
	
	@Bean
	public Step step(ItemReader<Notification> itemReader) throws Exception{
		return this.stepBuilderFactory.get(JobConstants.STEP_NAME)
									  .<Notification,Notification>chunk(2)
									  .reader(itemReader)
									  .build();
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<Notification> itemReader(@Value("#{jobParameters['"+ JobConstants.JOB_PARAM_FILE_NAME +"'}") String fileName){
		return new FlatFileItemReaderBuilder<Notification>().name("notification-item-reader")
										 .resource(new PathResource(Paths.get(applicationProperties.getBatch().getInputPath()+File.separator+fileName)))
										 .linesToSkip(1)
										 .lineMapper(lineMapper())
										 .build();
	}
	
	@Bean
	public LineMapper<Notification> lineMapper(){
		DefaultLineMapper<Notification> mapper = new DefaultLineMapper<>();
		mapper.setFieldSetMapper((fieldSet) -> new Notification(fieldSet.readString(0),fieldSet.readString(1),fieldSet.readString(2),fieldSet.readString(3)));
		mapper.setLineTokenizer(new DelimitedLineTokenizer(","));
		return mapper;
	}
	
	@Bean
	@StepScope
	public PassThroughItemProcessor<Notification> passthroughPassThroughItemProcessor(){
		return new PassThroughItemProcessor<>();
	}
	
	@Bean
	@StepScope
	public ItemWriter<Notification> writer(){
		return new ItemWriter<Notification>() {
			@Override
			public void write(List<? extends Notification> items) throws Exception {
				for(Notification notification:items) {
					System.err.println("Writting item: "+ notification.toString());
				}
			}
		};
	}
}
