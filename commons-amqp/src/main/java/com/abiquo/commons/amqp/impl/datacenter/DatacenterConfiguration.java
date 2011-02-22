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
    private static final String DATACENTER_ID_PROPERTY = "abiquo.datacenter.id";

    private static DatacenterConfiguration singleton = null;

    private static final String DATACENTER_DIRECT_EXCHANGE = "abiquo.datacenter.direct";

    // JOBS Configuration
    private static final String JOBS_ROUTING_KEY = "abiquo.datacenter.jobs";

    private static final String JOBS_QUEUE = JOBS_ROUTING_KEY;

    // NOTIFICATIONS Configuration
    private static final String NOTIFICATIONS_ROUTING_KEY = "abiquo.datacenter.notifications";

    public static final String NOTIFICATIONS_QUEUE = NOTIFICATIONS_ROUTING_KEY;

    private static String getDatacenterId()
    {
        String id = System.getProperty(DATACENTER_ID_PROPERTY);

        if (id == null)
        {
            throw new IllegalArgumentException("Unable to get the required property "
                + DATACENTER_ID_PROPERTY);
        }

        return id;
    }

    public static String getDatacenterDirectExchange()
    {
        return DATACENTER_DIRECT_EXCHANGE;
    }

    public static String getJobsRoutingKey()
    {
        return JOBS_ROUTING_KEY.concat(".").concat(getDatacenterId());
    }

    public static String getNotificationsRoutingKey()
    {
        return NOTIFICATIONS_ROUTING_KEY;
    }

    public static String getJobsQueue()
    {
        return JOBS_QUEUE.concat(".").concat(getDatacenterId());
    }

    public static String getNotificationsQueue()
    {
        return NOTIFICATIONS_QUEUE;
    }

    public static DatacenterConfiguration getInstance()
    {
        if (singleton == null)
        {
            singleton = new DatacenterConfiguration();
        }

        return singleton;
    }

    @Override
    public void declareBrokerConfiguration(Channel channel) throws IOException
    {
        channel.exchangeDeclare(getDatacenterDirectExchange(), DirectExchange, Durable);

        // Declare configuration for datacenter jobs
        channel.queueDeclare(getJobsQueue(), Durable, NonExclusive, NonAutodelete, null);
        channel.queueBind(getJobsQueue(), getDatacenterDirectExchange(), getJobsRoutingKey());

        // Declare configuration for datacenter job notifications
        channel.queueDeclare(getNotificationsQueue(), Durable, NonExclusive, NonAutodelete, null);
        channel.queueBind(getNotificationsQueue(), getDatacenterDirectExchange(),
            getNotificationsRoutingKey());
    }
}
