/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.virtualfactory.web.listener;

import static com.abiquo.commons.amqp.config.DefaultConfiguration.getHost;
import static com.abiquo.commons.amqp.config.DefaultConfiguration.getPassword;
import static com.abiquo.commons.amqp.config.DefaultConfiguration.getPort;
import static com.abiquo.commons.amqp.config.DefaultConfiguration.getUserName;
import static com.abiquo.commons.amqp.config.DefaultConfiguration.getVirtualHost;
import static com.abiquo.commons.amqp.impl.datacenter.DatacenterConfiguration.getDatacenterDirectExchange;
import static com.abiquo.commons.amqp.impl.datacenter.DatacenterConfiguration.getJobsQueue;
import static com.abiquo.commons.amqp.impl.datacenter.DatacenterConfiguration.getJobsRoutingKey;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.amqp.AMQP;
import akka.amqp.Direct;
import akka.dispatch.Dispatchers;

import com.abiquo.virtualfactory.akka.QueueWorker;

public class AkkaInitializer implements ServletContextListener
{

    private ActorRef consumer;

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        if (consumer != null)
        {
            consumer.stop();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ActorRef worker = Actors.actorOf(QueueWorker.class);

        int initPoolSize =
            Integer.parseInt(System.getProperty("abiquo.virtualfactory.min.workers", "1"));
        int maxPoolSize =
            Integer.parseInt(System.getProperty("abiquo.virtualfactory.max.workers", "1"));

        // TODO check NULL property value
        String datacenterId = System.getProperty("abiquo.datacenter.id");

        worker.setDispatcher(Dispatchers
            .newExecutorBasedEventDrivenDispatcher("workers dispatcher").setCorePoolSize(
                initPoolSize).setMaxPoolSize(maxPoolSize).setKeepAliveTimeInMillis(60000).build());

        AMQP.ConnectionParameters connectionParameters =
            new AMQP.ConnectionParameters(getHost(),
                getPort(),
                getUserName(),
                getPassword(),
                getVirtualHost());
        ActorRef connection = AMQP.newConnection(connectionParameters);

        AMQP.ExchangeParameters exchangeParameters =
            new AMQP.ExchangeParameters(getDatacenterDirectExchange(), Direct.getInstance());

        AMQP.ConsumerParameters consumerParameters =
            new AMQP.ConsumerParameters(getJobsRoutingKey(datacenterId),
                worker,
                getJobsQueue(datacenterId),
                exchangeParameters);
        consumer = AMQP.newConsumer(connection, consumerParameters);
    }
}
