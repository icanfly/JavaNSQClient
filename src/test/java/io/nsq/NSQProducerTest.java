package io.nsq;

import io.nsq.exceptions.NSQException;
import io.nsq.lookup.NSQLookup;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class NSQProducerTest {

    @Test
    public void testProduceOneMsg() throws NSQException, TimeoutException, InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        NSQLookup lookup = new NSQLookup();
        lookup.addAddr("localhost", 4161);

        NSQProducer producer = new NSQProducer().addAddress("localhost", 4150, 1);
        producer.start();
        String msg = randomString();
        producer.produce("test3", msg.getBytes());

        NSQConsumer consumer = new NSQConsumer(lookup, "test3", "testconsumer", (message) -> {
            LogManager.getLogger(this).info("Processing message: " + new String(message.getMessage()));
            counter.incrementAndGet();
            message.finished();
        });
        consumer.start();
        while (counter.get() == 0) {
            Thread.sleep(500);
        }
        assertEquals(1, counter.get());
        consumer.close();
    }

    private String randomString() {
        return "Message" + new Date().getTime();
    }
}