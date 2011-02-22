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

package com.abiquo.commons.ampq.vsm;

import java.io.IOException;

import org.testng.annotations.Test;

import com.abiquo.commons.amqp.impl.datacenter.JobsProducer;
import com.abiquo.commons.amqp.impl.datacenter.NotificationCallback;
import com.abiquo.commons.amqp.impl.datacenter.NotificationsConsumer;
import com.abiquo.commons.amqp.impl.datacenter.NotificationsProducer;
import com.abiquo.commons.amqp.impl.datacenter.domain.DatacenterJob;
import com.abiquo.commons.amqp.impl.datacenter.domain.DatacenterNotification;

public class DatacenterTest
{
    public static void main(String[] args) throws IOException
    {
        System.setProperty("abiquo.datacenter.id", "blablabla");
        new DatacenterTest().basic();
    }

    @Test(enabled = false)
    public void basic() throws IOException
    {
        NotificationsConsumer consumer = new NotificationsConsumer();

        consumer.addCallback(new NotificationCallback()
        {
            @Override
            public void onMessage(DatacenterNotification notification)
            {
                System.out.println(notification);
            }
        });

        consumer.start();

        JobsProducer jproducer = new JobsProducer();

        jproducer.openChannel();

        for (int i = 0; i < 10; i++)
        {
            jproducer.publish(new DatacenterJob());

            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        jproducer.closeChannel();

        NotificationsProducer producer = new NotificationsProducer();

        producer.openChannel();

        for (int i = 0; i < 10; i++)
        {
            producer.publish(new DatacenterNotification());

            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        producer.closeChannel();
        consumer.stop();
    }
};
