package com.abiquo.virtualfactory.model.jobs.builder;

import com.abiquo.virtualfactory.model.jobs.HypervisorConnection;

public class VirtualFactoryJobBuilder
{

    protected HypervisorConnection connection;

    public VirtualFactoryJobBuilder connection(String hypervisorID, String hypervisortype,
        String ip, String port, String protocol, String loginUser, String loginPasswoed)
    {
        connection = new HypervisorConnection();
        connection.setHypervisorID(hypervisorID);
        connection.setHypervisortype(hypervisortype);
        connection.setIp(ip);
        connection.setPort(port);
        connection.setLoginUser(loginUser);
        connection.setLoginPassword(loginPasswoed);

        return this;
    }

}
