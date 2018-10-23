package com.ttyc.security.demo.thread;

import com.ttyc.security.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
public class UserThread implements Callable<User> {
    private Long id;

    @Override
    public User call() throws Exception {
        System.out.println("params is " + id);
        User user = new User();
        Lock lock = null;
        try {
            lock = new ReentrantLock();
            boolean isGeted = lock.tryLock(3, TimeUnit.SECONDS);
            if (isGeted) {
                user.setId(id);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return user;
    }
}