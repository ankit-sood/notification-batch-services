package org.notify.india.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="application",ignoreUnknownFields=false)
public class ApplicationProperties {
	private final Batch batch = new Batch();
	
	public Batch getBatch() {
		return batch;
	}
	
	public static class Batch{
		private String inputPath = "";
		
		public String getInputPath() {
			return this.inputPath;
		}
		
		public void setInputPath(String inputPath) {
			this.inputPath = inputPath;
		}
	}
}
