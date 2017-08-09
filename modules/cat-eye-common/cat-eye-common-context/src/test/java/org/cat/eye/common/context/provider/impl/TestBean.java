package org.cat.eye.common.context.provider.impl;

import org.cat.eye.common.context.provider.msg.MessageServiceMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
        init();
    }

    private void init() {
        new Thread(this).start();
    }

    private void printMsg() {
        System.out.println(this.service.getMessage());
    }

    @Override
    public void run() {
        while (true) {
            this.printMsg();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
