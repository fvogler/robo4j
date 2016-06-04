/*
 * Copyright (C) 2016. Miroslav Kopecky
 * This CoreBusQueue.java is part of robo4j.
 *
 *     robo4j is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     robo4j is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.robo4j.core.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *
 * Created by miroslavkopecky on 03/04/16.
 */
public abstract class CoreBusQueue<TransferType extends QueueFIFOEntry<? extends TransferSignal>>
        extends PriorityBlockingQueue<TransferType> implements TransferQueue<TransferType> {

    private static final Logger logger = LoggerFactory.getLogger(CoreBusQueue.class);
    //Class can be serialized/deserialized at runtime. Sender/Receiver class loading
    private static final long serialVersionUID = 22L;
    private static final int INIT_COUNTER = 0;
    private static final boolean INIT_BUS = true;

    /* Holding the number of consumers - currently consumer should be only ONE */
    private AtomicInteger counter;
    private AtomicBoolean active;
    private LinkedBlockingQueue<TransferType> transfer;
    private ReentrantLock lock;
    /* used to consumer blocking */
    private Condition conditionTrans;

    /* setup for platform bus */
    private int awaitSeconds;

    public CoreBusQueue(int awaitSeconds){
        counter = new AtomicInteger(INIT_COUNTER);
        active = new AtomicBoolean(INIT_BUS);
        lock = new ReentrantLock();
        transfer = new LinkedBlockingQueue<>();
        conditionTrans = lock.newCondition();

        this.awaitSeconds = awaitSeconds;
    }

    public boolean isActive(){
        return active.get();
    }

    public void setActive(){
        active.set(true);
    }

    public void deactivate(){
        this.active.set(false);
    }

    @Override
    public boolean tryTransfer(TransferType e) {
        boolean result = false;
        lock.lock();
        try{
            if(counter.get() == 0)
                result = false;
            else {
                put(e);
                result = true;
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * Busy Spin
     * @param e - command
     * @throws InterruptedException
     */
    @Override
    public void transfer(TransferType e) throws InterruptedException {
        lock.lock();
        try {
            if(counter.get() != 0)
                put(e);
            else
                transfer.add(e);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public boolean tryTransfer(TransferType e, long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        boolean result = false;
        try{
            if(counter.get() != 0){
                put(e);
                result = true;
            } else {
                transfer.add(e);
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    /*
     * Method queue waits for consumer
     */
    @Override
    public boolean hasWaitingConsumer() {
        return counter.get() != 0;
    }

    /*
     * Method returns the number of waiting consumers
     */
    @Override
    public int getWaitingConsumerCount() {
        return counter.get();
    }

    /*
     * returns the first element in queue or is blocked if the queue is empty
     * If there is the commend in "transfer", it takes the 1st element and wake up thread
     * that is waiting for the command else, or it takes 1st element for the queue or is
     * blocked util there is one command int the queue
     */

    @Override
    public TransferType take() throws InterruptedException {
        lock.lock();
        TransferType result = transfer.poll();
        try{
            counter.incrementAndGet();
            int awaitCycle = 0;
            while (Objects.isNull(result) && active.get()){
                conditionTrans.await(awaitSeconds, TimeUnit.SECONDS);
                lock.unlock();
                result = super.take();
                lock.lock();
                awaitCycle++;
            }
            conditionTrans.signalAll();
            synchronized (result){
                result.notifyAll();
            }
        } catch (InterruptedException e){
            logger.error("TAKE error= " + e);
        } finally {
            counter.decrementAndGet();
            lock.unlock();
        }

        return result;
    }

    /*
     * Retrieves but does not remove, the head or result null
     */
    @Override
    public TransferType peek() {
        lock.lock();
        TransferType result = peekCommand();
        try {
            int awaitCycle = 0;
            while (Objects.isNull(result) && active.get()){
                conditionTrans.await(awaitSeconds, TimeUnit.SECONDS);
                lock.unlock();
                result = peekCommand();
                lock.lock();
                awaitCycle++;
            }
            conditionTrans.signalAll();
        } catch (InterruptedException e) {
            logger.info("GOING DOWN ");
        } finally {
            lock.unlock();
        }
        return result;
    }

    protected TransferType peekCommand(){
        TransferType commandMain = super.peek();
        TransferType commandTrans = transfer.peek();
        return Objects.nonNull(commandMain) ? commandMain : commandTrans;
    }

    @Override
    public int size() {
        return (super.size() + transfer.size());
    }



}
