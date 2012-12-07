/*
 * Buddycloud Channel Server
 * http://buddycloud.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.buddycloud.channelserver.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.xmpp.packet.Packet;

public abstract class QueueConsumer {

    private static final Logger LOGGER = Logger.getLogger(QueueConsumer.class);
    
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final BlockingQueue<Packet> queue;
    
    public QueueConsumer(BlockingQueue<Packet> queue) {
        this.queue = queue;
    }

    public void start() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Packet packet = queue.take();
                        consume(packet);
                    } catch (InterruptedException e) {
                        LOGGER.error(e);
                    }
                }
            }
        });
    }
    
    protected abstract void consume(Packet p);
    
}
