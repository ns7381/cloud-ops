package com.cloud.ops.common.utils;

/**
 * Created by Administrator on 2017/2/5.
 */
public abstract class ThreadWithEntity<T> extends Thread {
    private T entity;


    public ThreadWithEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        run(entity);
    }

    public abstract void run(T entity);
}
