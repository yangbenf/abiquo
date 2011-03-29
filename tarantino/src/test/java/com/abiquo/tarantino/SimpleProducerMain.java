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

package com.abiquo.tarantino;

import java.io.IOException;

import com.abiquo.commons.amqp.impl.datacenter.JobsProducer;
import com.abiquo.commons.amqp.impl.datacenter.domain.dto.DatacenterJobDto;

public class SimpleProducerMain
{
    /**
     * start tarantino: mvn jetty:run -Dabiquo.datacenter.id=2
     * 
     * @param args Unused
     * @throws IOException If RabbitMQ connection fails
     */
    public static void main(String[] args) throws IOException
    {
        String datacenterId = "2";

        JobsProducer producer = new JobsProducer(datacenterId);
        DatacenterJobDto job = new DatacenterJobDto();
        // job.setOperation(operation) // Set operation!

        producer.openChannel();
        producer.publish(job);
        producer.closeChannel();
    }
}
