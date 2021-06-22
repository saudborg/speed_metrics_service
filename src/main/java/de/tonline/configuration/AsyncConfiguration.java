package de.tonline.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Given the properties, this class create a bean for running async task
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

	public static final String COMPONENT_ID = "asyncExecutor";
	@Value("${async.thread.core.pool.size:3}")
	private int corePoolSize;
	@Value("${async.thread.max.pool.size:3}")
	private int maxPoolSize;
	@Value("${async.thread.queue.capacity:100}")
	private int queueCapacity;

	@Bean(name = COMPONENT_ID)
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.initialize();
		return executor;
	}

}
