/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPool {

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);


        //schedule to run after sometime
        System.out.println("Current Time = " + new Date());

        FancyNumberAuction worker = new FancyNumberAuction("do heavy processing");
        scheduledThreadPool.schedule(worker, 24, TimeUnit.HOURS);


        scheduledThreadPool.shutdown();
        while (!scheduledThreadPool.isTerminated()) {
            //wait for all tasks to finish
        }
        System.out.println("Finished all threads");
    }
}