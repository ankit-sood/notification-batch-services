package org.notify.india.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.notify.india.NotificationBatchServicesApplication;
import org.notify.india.constants.JobConstants;
import org.springframework.batch.core.Job;
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
	
	 @Test
	 public void test() {
		 assertNotNull(job);
		 assertEquals(JobConstants.JOB_NAME, job.getName());
	 }

}
