package no.hvl.dat250.h2020.group5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class PublisherRunnner implements CommandLineRunner {

  private final TaskExecutor taskExecutor;
  @Autowired Publisher publisher;

  public PublisherRunnner(@Qualifier("taskExecutor") TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  @Override
  public void run(String... args) throws Exception {
    taskExecutor.execute(publisher);
  }
}
