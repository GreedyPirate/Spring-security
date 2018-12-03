package com.ttyc.security.demo.thread;

import com.ttyc.security.demo.model.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadTest {

    @Test
    public void testUser() {
        ExecutorService service =
                new ThreadPoolExecutor(10, 20, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        List<Future<User>> results = new ArrayList<>();
        for (long i = 0; i < 100; i++) {
            Future<User> result = service.submit(new UserThread(i));
            results.add(result);
        }
        results.stream().forEach((result) -> {
            try {
                User user = result.get();
                System.out.println(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        service.shutdown();
    }
}
