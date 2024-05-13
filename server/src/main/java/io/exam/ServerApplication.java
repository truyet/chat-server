package io.exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScan(basePackages = {"io.exam"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "io.exam.chat.config || io.exam.auth.config"))
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

}
