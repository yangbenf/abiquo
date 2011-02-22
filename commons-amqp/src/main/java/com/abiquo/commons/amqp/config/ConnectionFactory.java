package com.abiquo.commons.amqp.config;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class ConnectionFactory
{
    private static ConnectionFactory singleton = null;

    private com.rabbitmq.client.ConnectionFactory factory;

    private Connection connection;

    public static ConnectionFactory getInstance()
    {
        if (singleton == null)
        {
            singleton = new ConnectionFactory();
        }

        return singleton;
    }

    private ConnectionFactory()
    {
        factory = new com.rabbitmq.client.ConnectionFactory();

        factory.setHost(DefaultConfiguration.getHost());
        factory.setPort(DefaultConfiguration.getPort());
        factory.setUsername(DefaultConfiguration.getUserName());
        factory.setPassword(DefaultConfiguration.getPassword());
        factory.setVirtualHost(DefaultConfiguration.getVirtualHost());

        connection = null;
    }

    public Channel createChannel() throws IOException
    {
        if (connection == null)
        {
            connection = factory.newConnection();
        }

        return connection.createChannel();
    }
}
