package com.cloud.ops.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * The database configuration which enables spring's jpa-auditing for automatically
 * timestamp injection.
 *
 * @author Nathan
 */
@Configuration
@EnableJpaAuditing
public class AuditConfiguration {

  /**
   * @return a simple username as auditor
   */
  @Bean
  public AuditorAware<String> auditorAware() {
      return new AuditorAware<String>() {
          @Override
          public String getCurrentAuditor() {
              Authentication auth = SecurityContextHolder.getContext().getAuthentication();
              if (auth != null && auth.getPrincipal() instanceof User) {
                  return ((User) auth.getPrincipal()).getUsername();
              }
              return null;
          }
      };
  }

}
