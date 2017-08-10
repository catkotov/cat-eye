package org.cat.eye.common.context.provider.impl;

import org.cat.eye.common.context.provider.msg.MessageServiceMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 *
 * Created by Kotov on 19.09.2016.
 */
@Component
public class TestBean implements Runnable {

    private MessageServiceMock service;

    @Autowired
    TestBean(MessageServiceMock service) {
        this.service = service;
    }

    @PostConstruct
    private void init() {
        Thread t = new Thread(this);
        t.start();
    }

    private void printMsg() {
        System.out.print(this.service.getMessage());
    }

    @Override
    public void run() {

        int i = 1;

        while (true) {
            this.printMsg();
            System.out.println(" " + i);
            i++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
