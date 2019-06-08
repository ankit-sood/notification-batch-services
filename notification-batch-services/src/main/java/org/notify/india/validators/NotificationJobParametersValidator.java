package org.notify.india.validators;

import org.notify.india.constants.JobConstants;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("notificationJobParametersValidator")
public class NotificationJobParametersValidator implements JobParametersValidator{

	@Override
	public void validate(JobParameters parameters) throws JobParametersInvalidException {
		String fileName = parameters.getString(JobConstants.JOB_PARAM_FILE_NAME);
		if(StringUtils.isEmpty(fileName)) {
			throw new JobParametersInvalidException("notification-job-file-name is required.");
		}
	}

}
