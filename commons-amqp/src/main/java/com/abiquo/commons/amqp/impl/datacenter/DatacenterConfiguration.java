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

package com.abiquo.commons.amqp.impl.datacenter;

import java.io.IOException;

import com.abiquo.commons.amqp.config.DefaultConfiguration;
import com.rabbitmq.client.Channel;

public class DatacenterConfiguration extends DefaultConfiguration
{
    private String datacenterId;

    // Exchange configurations
    private static final String DATACENTER_DIRECT_EXCHANGE = "abiquo.datacenter.direct";

    // Jobs Configuration
    private static final String JOBS_ROUTING_KEY = "abiquo.datacenter.jobs";

    private static final String JOBS_QUEUE = JOBS_ROUTING_KEY;

    // Notifications Configuration
    private static final String NOTIFICATIONS_ROUTING_KEY = "abiquo.datacenter.notifications";

    public static final String NOTIFICATIONS_QUEUE = NOTIFICATIONS_ROUTING_KEY;

    public static String getDatacenterDirectExchange()
    {
        return DATACENTER_DIRECT_EXCHANGE;
    }

    public static String getJobsRoutingKey(final String datacenterId)
    {
        return JOBS_ROUTING_KEY.concat(".").concat(datacenterId);
    }

    public static String getNotificationsRoutingKey()
    {
        return NOTIFICATIONS_ROUTING_KEY;
    }

    public static String getJobsQueue(final String datacenterId)
    {
        return JOBS_QUEUE.concat(".").concat(datacenterId);
    }

    public static String getNotificationsQueue()
    {
        return NOTIFICATIONS_QUEUE;
    }

    public DatacenterConfiguration()
    {
        this.datacenterId = null;
    }

    public DatacenterConfiguration(final String datacenterId)
    {
        this.datacenterId = datacenterId;
    }

    @Override
    public void declareBrokerConfiguration(Channel channel) throws IOException
    {
        channel.exchangeDeclare(getDatacenterDirectExchange(), DirectExchange, Durable);

        // Declare configuration for datacenter job notifications
        channel.queueDeclare(getNotificationsQueue(), Durable, NonExclusive, NonAutodelete, null);
        channel.queueBind(getNotificationsQueue(), getDatacenterDirectExchange(),
            getNotificationsRoutingKey());

        if (datacenterId != null)
        {
            // Declare configuration for datacenter jobs
            channel.queueDeclare(getJobsQueue(datacenterId), Durable, NonExclusive, NonAutodelete,
                null);
            channel.queueBind(getJobsQueue(datacenterId), getDatacenterDirectExchange(),
                getJobsRoutingKey(datacenterId));
        }
    }
}
