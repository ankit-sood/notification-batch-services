package org.notify.india.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.notify.india.NotificationBatchServicesApplication;
import org.notify.india.constants.JobConstants;
import org.notify.india.model.Notification;
import org.notify.india.repository.PatientRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationBatchServicesApplication.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,StepScopeTestExecutionListener.class,TransactionalTestExecutionListener.class})
@ActiveProfiles("dev")
@Transactional
public class NotificationBatchJobConfigurationTest {

	@Autowired
	private Job job;

	@Autowired
	private FlatFileItemReader<Notification> itemReader;
	
	@Autowired
	private ItemProcessor<Notification,Notification> itemProcessor;
	
	@Autowired
	private JpaItemWriter<Notification> jpaItemWriter;
	
	@Autowired
	private PatientRepository patientRepository;

	private JobParameters jobParameters;
	
	@Before
	public void setup() {
		Map<String, JobParameter> parameterMap = new HashMap<>();
		parameterMap.put(JobConstants.JOB_PARAM_FILE_NAME, new JobParameter("email.csv"));
		jobParameters = new JobParameters(parameterMap);
	}
	
	@Test
	public void testJobBean() {
		assertNotNull(job);
		assertEquals(JobConstants.JOB_NAME, job.getName());
	}
	
	@Test
	public void testItemReader() {
		assertNotNull(itemReader);
		StepExecution stepExecution = (StepExecution) MetaDataInstanceFactory.createStepExecution(jobParameters);
		int count = 0;
		try {
			count = StepScopeTestUtils.doInStepScope(stepExecution,()->{
				int notificationCount = 0;
				Notification notification;
				try {
					itemReader.open(stepExecution.getExecutionContext());
					while((notification = itemReader.read())!=null) {
						assertNotNull(notification);
						assertNotNull(notification.getId());
						assertNotNull(notification.getMessage());
						assertNotNull(notification.getType());
						assertNotNull(notification.getDestiantionAddress());
						notificationCount++;
					}
				}finally {
					try {
						itemReader.close();
					}catch(Exception e){
						fail(e.toString());
					}
				}
				return notificationCount;
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(1, count);
	}
	
	@Test
	public void testItemProcessor() throws Exception {
		assertNotNull(itemProcessor);
		Notification notification = new Notification(12l,"Welcome to India","email","soodankit1993@gmail.com");
		notification = itemProcessor.process(notification);
		//notification = itemProcessor.apply(notification);
		assertEquals(true, notification.isProcessed());
	}
	
	@Test
	public void testItemWriter() {
		assertNotNull(jpaItemWriter);
		StepExecution stepExecution = (StepExecution) MetaDataInstanceFactory.createStepExecution();
		try {
			List<Notification> notificationList = new ArrayList<>();
			notificationList.add(new Notification(12l,"Welcome to India","email","soodankit1993@gmail.com",true));
			StepScopeTestUtils.doInStepScope(stepExecution,()->{
					jpaItemWriter.write(notificationList);
					return null;
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(patientRepository.findAll().size()>0);
	}

}
