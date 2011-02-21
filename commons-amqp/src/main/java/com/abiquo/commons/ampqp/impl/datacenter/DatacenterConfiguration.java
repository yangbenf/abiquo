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

package com.abiquo.commons.ampqp.impl.datacenter;

import java.io.IOException;

import com.abiquo.commons.amqp.config.DefaultConfiguration;
import com.rabbitmq.client.Channel;

public class DatacenterConfiguration extends DefaultConfiguration
{
    private static final String DATACENTER_EXCHANGE = "abq.datacenter.jobs";

    private static final String DATACENTER_ROUTING_KEY = "abq.datacenter.";

    private static final String DATACENTER_QUEUE = DATACENTER_ROUTING_KEY;

    private static final String ID_PROPERTY = "abiquo.datacenter.id";

    private static DatacenterConfiguration singleton = null;

    private static String getDatacenterId()
    {
        String id = System.getProperty(ID_PROPERTY);

        if (id == null)
        {
            throw new IllegalArgumentException("Unable to get the required property " + ID_PROPERTY);
        }

        return id;
    }

    public static String getExchangeName()
    {
        return DATACENTER_EXCHANGE;
    }

    public static String getRoutingKey()
    {
        return DATACENTER_ROUTING_KEY + getDatacenterId();
    }

    public static String getQueueName()
    {
        return DATACENTER_QUEUE + getDatacenterId();
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
        channel.exchangeDeclare(getExchangeName(), DirectExchange, Durable);

        channel.queueDeclare(getQueueName(), Durable, NonExclusive, NonAutodelete, null);
        channel.queueBind(getQueueName(), getExchangeName(), getRoutingKey());
    }
}
