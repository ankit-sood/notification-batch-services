package org.notify.india.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(basePackages= {"org.notify.india"})
public class JPARepositoriesConfig {
	private DataSource batchH2DataSource;
	private final Environment env;
	
	public JPARepositoriesConfig(DataSource batchH2DataSource,Environment env) {
		this.batchH2DataSource = batchH2DataSource;
		this.env = env;
	}
	
	@Bean(name = "batchJpaVendorAdapter")
    public JpaVendorAdapter batchJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean(name = "batchEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean batchEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emfBean =
            new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(batchH2DataSource);
        emfBean.setPackagesToScan("org.notify.india");
        emfBean.setBeanName("batchEntityManagerFactory");
        emfBean.setJpaVendorAdapter(batchJpaVendorAdapter());

        Properties jpaProps = new Properties();
		/*
		 * jpaProps.put("hibernate.physical_naming_strategy",
		 * env.getProperty("spring.jpa.hibernate.naming.physical-strategy"));
		 */
        jpaProps.put("hibernate.hbm2ddl.auto", env.getProperty(
            "spring.jpa.hibernate.ddl-auto", "none"));
        jpaProps.put("hibernate.jdbc.fetch_size", env.getProperty(
            "spring.jpa.properties.hibernate.jdbc.fetch_size",
            "200"));

        Integer batchSize = env.getProperty(
            "spring.jpa.properties.hibernate.jdbc.batch_size",
            Integer.class, 100);
        if (batchSize > 0) {
            jpaProps.put("hibernate.jdbc.batch_size", batchSize);
            jpaProps.put("hibernate.order_inserts", "true");
            jpaProps.put("hibernate.order_updates", "true");
        }

        jpaProps.put("hibernate.show_sql", env.getProperty(
            "spring.jpa.properties.hibernate.show_sql", "false"));
        jpaProps.put("hibernate.format_sql",env.getProperty(
            "spring.jpa.properties.hibernate.format_sql", "false"));

        emfBean.setJpaProperties(jpaProps);
        return emfBean;
    }

    @Bean(name = "batchTransactionManager")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(batchEntityManagerFactory().getObject());
    }
}
