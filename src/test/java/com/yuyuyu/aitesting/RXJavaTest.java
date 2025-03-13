package com.yuyuyu.aitesting;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLOutput;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RXJavaTest {
    @Test
    public void test() throws InterruptedException {
        //创建数据流
        //每个一段时间产生一个新的数据流
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(x -> x + 1)
                .subscribeOn(Schedulers.io());//指定执行操作所做的线程池
        //订阅flowble流
        //打印对应的数字
        flowable.observeOn(Schedulers.io())
                .doOnNext(item-> System.out.println(item.toString()))
                .subscribe();
        //主线程休眠，以便观测到结果
        Thread.sleep(10000L);
    }
}
