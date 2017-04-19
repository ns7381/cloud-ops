package com.cloud.ops.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * The database configuration which enables spring's jpa-auditing for automatically
 * timestamp injection.
 *
 * @author Nathan
 */
@Configuration
@EnableJpaAuditing
public class DatabaseConfiguration {

  /**
   * @return a simple username as auditor
   */
  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> "admin";
  }

}
