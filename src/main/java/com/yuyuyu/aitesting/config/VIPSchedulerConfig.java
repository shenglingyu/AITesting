package com.yuyuyu.aitesting.config;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class VIPSchedulerConfig {
    ThreadFactory threadFactory=new ThreadFactory() {
        private final AtomicInteger threadNumber=new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread=new Thread(r,"VIPThreadPool-"+threadNumber.getAndIncrement());
            thread.setDaemon(false);//设置为非守护线程
            return thread;
        }
    };
    @Bean
    public Scheduler vipScheduler() {
        ScheduledExecutorService scheduledExecutorService = Executors
                .newScheduledThreadPool(10, threadFactory);
        return Schedulers.from(scheduledExecutorService);
    }
}
