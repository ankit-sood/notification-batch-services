package org.notify.india.config;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.notify.india.constants.JobConstants;
import org.notify.india.model.Notification;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	
	@Autowired
	@Qualifier(value="batchEntityManagerFactory")
	private EntityManagerFactory batchEntityManagerFactory;
	
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
	public Step step(ItemReader<Notification> itemReader) throws Exception{
		return this.stepBuilderFactory.get(JobConstants.STEP_NAME)
									  .<Notification,Notification>chunk(2)
									  .reader(itemReader)
									  .processor(itemProcessor())
									  .writer(jpaItemWriter())
									  .build();
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<Notification> itemReader(@Value("#{jobParameters['"+ JobConstants.JOB_PARAM_FILE_NAME +"']}") String fileName){
		return new FlatFileItemReaderBuilder<Notification>().name("notification-item-reader")
										 .resource(new PathResource(Paths.get(applicationProperties.getBatch().getInputPath()+File.separator+fileName)))
										 .linesToSkip(1)
										 .lineMapper(lineMapper())
										 .strict(false)
										 .build();
	}
	
	@Bean
	public LineMapper<Notification> lineMapper(){
		DefaultLineMapper<Notification> mapper = new DefaultLineMapper<>();
		mapper.setFieldSetMapper((fieldSet) -> new Notification(fieldSet.readLong(0),fieldSet.readString(1),fieldSet.readString(2),fieldSet.readString(3)));
		mapper.setLineTokenizer(new DelimitedLineTokenizer(","));
		return mapper;
	}
	
	@Bean("itemProcessor")
	@StepScope
	public ItemProcessor<Notification,Notification> itemProcessor(){
		return new ItemProcessor<Notification, Notification>() {
			@Override
			public Notification process(Notification item) throws Exception {
				if(item.getDestiantionAddress()!=null && item.getMessage()!=null && item.getType()!=null) {
					item.setProcessed(true);
				}
				return item;
			}
		};
	}
	
	@Bean
	@StepScope
	public ItemWriter<Notification> itemWriter(){
		return new ItemWriter<Notification>() {
			@Override
			public void write(List<? extends Notification> items) throws Exception {
				for(Notification notification:items) {
					System.err.println("Writting item: "+ notification.toString());
				}
			}
		};
	}
	
	@Bean
	@StepScope
	public JpaItemWriter<Notification> jpaItemWriter(){
		JpaItemWriter<Notification> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(batchEntityManagerFactory);
		return jpaItemWriter;
	}
}
