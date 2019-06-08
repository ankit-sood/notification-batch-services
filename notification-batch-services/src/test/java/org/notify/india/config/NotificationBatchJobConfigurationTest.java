package org.notify.india.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.notify.india.NotificationBatchServicesApplication;
import org.notify.india.constants.JobConstants;
import org.notify.india.model.Notification;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationBatchServicesApplication.class)
@ActiveProfiles("dev")
public class NotificationBatchJobConfigurationTest {

	@Autowired
	private Job job;

	@Autowired
	private FlatFileItemReader<Notification> itemReader;
	
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


}
