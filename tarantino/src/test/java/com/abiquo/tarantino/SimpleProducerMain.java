package com.abiquo.tarantino;

import java.io.IOException;
import java.util.UUID;

import com.abiquo.commons.amqp.impl.datacenter.JobsProducer;
import com.abiquo.commons.amqp.impl.datacenter.domain.DatacenterJob;

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
        DatacenterJob job = new DatacenterJob();
        job.dummy = UUID.randomUUID().toString();

        producer.openChannel();
        producer.publish(job);
        producer.closeChannel();
    }
}
