package com.yingda.lkj.timer;

import com.sun.istack.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author hood  2020/4/7
 */
@Component
@EnableScheduling
public class Scheduler {

    /**
     * Scheduling跟socket冲突了，这么写解决
     * https://blog.csdn.net/kzcming/article/details/102390593
     */
    @Bean
    @Nullable
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolScheduler = new ThreadPoolTaskScheduler();
        threadPoolScheduler.setThreadNamePrefix("SockJS-");
        threadPoolScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        threadPoolScheduler.setRemoveOnCancelPolicy(true);
        return threadPoolScheduler;
    }

    /**
     * <p>根据deviceMeasurementPlan(设备维护计划)定时生成测量计划</p>
     * <p>每天23:00执行</p>
     */
    @Scheduled(cron = "0 00 23 ? * *")
    public void createMeasurementTask() {
    }

}
