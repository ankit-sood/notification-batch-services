package org.notify.india.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfiguration {

	private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

	private final Environment env;

	public DatabaseConfiguration(Environment env) {
		this.env = env;
	}

	@Bean(name = "batchDataSource")
	@Qualifier(value = "batchDataSource")
	public DataSource batchDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(env.getRequiredProperty("spring.datasource.url"));
		config.setUsername(env.getProperty("spring.datasource.username"));
		config.setPassword(env.getProperty("spring.datasource.password"));
		config.setMinimumIdle(env.getProperty("spring.datasource.min-idle", Integer.class, 2));
		config.setMaximumPoolSize(env.getProperty("spring.datasource.max-active", Integer.class, 100));
		config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
		config.setRegisterMbeans(true);
		return new HikariDataSource(config);
	}

	@Bean(name = "batchH2DataSource")
	@Qualifier(value = "batchH2DataSource")
	@Primary
	public DataSource batchH2DataSource() {
		log.info("Intializing bean of h2DataSource");
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).ignoreFailedDrops(true)
				.addScript("schema.sql").continueOnError(true).build();
	}

	@Bean(name = "batchTransactionManager")
	public PlatformTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
		// return new JpaTransactionManager(batchEntityManagerFactory().getObject());
	}
	
	@Bean
    public MBeanExporter exporter() {
        final MBeanExporter exporter = new MBeanExporter();
        exporter.setExcludedBeans("batchDataSource");
        return exporter;
    }
}
