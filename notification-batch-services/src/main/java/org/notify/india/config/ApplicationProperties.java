package org.notify.india.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="application",ignoreUnknownFields=false)
public class ApplicationProperties {
	private final Batch batch = new Batch();
	
	public Batch getBatch() {
		return batch;
	}
	
	public static class Batch{
		private String inputPath = "D:/batchfiles/csv";
		
		public String getInputPath() {
			return this.inputPath;
		}
		
		public void setInputPath(String inputPath) {
			this.inputPath = inputPath;
		}
	}
}
