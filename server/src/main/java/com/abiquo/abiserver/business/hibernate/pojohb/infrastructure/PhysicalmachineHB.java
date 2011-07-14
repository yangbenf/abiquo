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

package com.abiquo.abiserver.business.hibernate.pojohb.infrastructure;

// Generated 16-oct-2008 16:52:14 by Hibernate Tools 3.2.1.GA

import java.util.HashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.pojo.infrastructure.Datastore;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;

/**
 * Physicalmachine generated by hbm2java
 */
/**
 * @author xfernandez
 */
public class PhysicalmachineHB implements java.io.Serializable, IPojoHB<PhysicalMachine>
{

    private static final long serialVersionUID = 9075806281973956772L;

    private Integer idPhysicalMachine;

    private RackHB rack;

    private DatacenterHB dataCenter;

    private String name;

    private String description;

    private int ram;

    private int cpu;

    private long hd;

    private int realRam;

    private int realCpu;

    private long realStorage;

    private String vswitchName;

    private String initiatorIQN;
    
    private String ipmiIp;

    private Integer ipmiPort;

    private String ipmiUser;

    private String ipmiPassword;

    private Set<DatastoreHB> datastoresHB;

    public static final int STATE_STOPPED = 0;

    public static final int STATE_PROVISIONED = 1;

    public static final int STATE_NOT_MANAGED = 2;

    public static final int STATE_MANAGED = 3;

    public static final int STATE_HALTED = 4;

    public static final int STATE_UNLICENSED = 5;
    
    public static final int STATE_HA_IN_PROGRESS = 6;
    
    public static final int STATE_DISABLED_FOR_HA = 7;

    /**
     * How many virtual CPU are supported for each physical core, so ''cpuUsed'' always below
     * ''cpu'' * ''cpuRatio''.<br>
     * Default initialized using the configuration file property.
     */
    private int cpuRatio = AbiConfigManager.getInstance().getAbiConfig().getVirtualCpuPerCore();

    private int ramUsed;

    private int cpuUsed;

    private long hdUsed;

    /**
     * This parameter identifies the state of the physicalMachine. 0 - Stopped 1 - Not Provisioned 2
     * - Not managed 3 - Managed 4 - Halted
     */
    private int idState;

    private HypervisorHB hypervisor;

    private Integer idEnterprise;

    /**
     * @return the cpuRatio
     */
    public int getCpuRatio()
    {
        return cpuRatio;
    }

    /**
     * @param cpuRatio the cpuRatio to set
     */
    public void setCpuRatio(final int cpuRatio)
    {
        this.cpuRatio = cpuRatio;
    }

    public Integer getIdPhysicalMachine()
    {
        return idPhysicalMachine;
    }

    public void setIdPhysicalMachine(final Integer idPhysicalMachine)
    {
        this.idPhysicalMachine = idPhysicalMachine;
    }

    public RackHB getRack()
    {
        return rack;
    }

    public void setRack(final RackHB rack)
    {
        this.rack = rack;
    }

    public DatacenterHB getDataCenter()
    {
        return dataCenter;
    }

    public void setDataCenter(final DatacenterHB dataCenter)
    {
        this.dataCenter = dataCenter;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public int getRam()
    {
        return ram;
    }

    public void setRam(final int ram)
    {
        this.ram = ram;
    }

    public int getCpu()
    {
        return cpu;
    }

    public void setCpu(final int i)
    {
        cpu = i;
    }

    public long getHd()
    {
        return hd;
    }

    public void setHd(final long hd)
    {
        this.hd = hd;
    }

    public int getRealRam()
    {
        return realRam;
    }

    public void setRealRam(final int realRam)
    {
        this.realRam = realRam;
    }

    public int getRealCpu()
    {
        return realCpu;
    }

    public void setRealCpu(final int realCpu)
    {
        this.realCpu = realCpu;
    }

    /**
     * @return the realStorage
     */
    public long getRealStorage()
    {
        return realStorage;
    }

    /**
     * @param realStorage the realStorage to set
     */
    public void setRealStorage(final long realStorage)
    {
        this.realStorage = realStorage;
    }

    // used
    public int getRamUsed()
    {
        return ramUsed;
    }

    public void setRamUsed(final int ram)
    {
        ramUsed = ram;
    }

    public int getCpuUsed()
    {
        return cpuUsed;
    }

    public void setCpuUsed(final int cpu)
    {
        cpuUsed = cpu;
    }

    public long getHdUsed()
    {
        return hdUsed;
    }

    public void setHdUsed(final long hd)
    {
        hdUsed = hd;
    }

    public HypervisorHB getHypervisor()
    {
        return hypervisor;
    }

    public void setHypervisor(final HypervisorHB hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    public int getIdState()
    {
        return idState;
    }

    public void setIdState(final int idState)
    {
        this.idState = idState;
    }

    /**
     * @param vswitchName the vswitchName to set
     */
    public void setVswitchName(final String vswitchName)
    {
        this.vswitchName = vswitchName;
    }

    /**
     * @return the vswitchName
     */
    public String getVswitchName()
    {
        return vswitchName;
    }

    /**
     * @return the initiatorIQN
     */
    public String getInitiatorIQN()
    {
        return initiatorIQN;
    }

    /**
     * @param initiatorIQN, the new initiatorIQN to set
     */
    public void setInitiatorIQN(final String initiatorIQN)
    {
        this.initiatorIQN = initiatorIQN;
    }

    /**
     * @param datastoresHB the datastoresHB to set
     */
    public void setDatastoresHB(final Set<DatastoreHB> datastoresHB)
    {
        this.datastoresHB = datastoresHB;
    }

    /**
     * @return the datastoresHB
     */
    public Set<DatastoreHB> getDatastoresHB()
    {
        return datastoresHB;
    }

    /**
     * @param idEnterprise the idEnterprise to set
     */
    public void setIdEnterprise(Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    /**
     * @return the idEnterprise
     */
    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }
    
    public String getIpmiIp()
    {
        return ipmiIp;
    }

    public void setIpmiIp(final String ipmiIp)
    {
        this.ipmiIp = ipmiIp;
    }

    public Integer getIpmiPort()
    {
        return ipmiPort;
    }

    public void setIpmiPort(final Integer ipmiPort)
    {
        this.ipmiPort = ipmiPort;
    }

    public String getIpmiUser()
    {
        return ipmiUser;
    }

    public void setIpmiUser(final String ipmiUser)
    {
        this.ipmiUser = ipmiUser;
    }

    public String getIpmiPassword()
    {
        return ipmiPassword;
    }

    public void setIpmiPassword(final String ipmiPassword)
    {
        this.ipmiPassword = ipmiPassword;
    }

    @Override
    public PhysicalMachine toPojo()
    {
        PhysicalMachine physicalMachine = new PhysicalMachine();

        physicalMachine.setDataCenter(getDataCenter().toPojo());
        physicalMachine.setCpuRatio(cpuRatio);
        physicalMachine.setCpu(cpu);
        physicalMachine.setCpuUsed(cpuUsed);
        physicalMachine.setDescription(description);
        physicalMachine.setHd(hd);
        physicalMachine.setRealCpu(realCpu);
        physicalMachine.setRealRam(realRam);
        physicalMachine.setRealStorage(realStorage);
        physicalMachine.setHdUsed(hdUsed);
        physicalMachine.setId(getIdPhysicalMachine());
        physicalMachine.setName(name);
        physicalMachine.setAssignedTo(rack == null ? null : (Rack) rack.toPojo());
        physicalMachine.setRam(ram);
        physicalMachine.setRamUsed(ramUsed);
        physicalMachine.setIdState(idState);
        physicalMachine.setVswitchName(vswitchName);
        physicalMachine.setInitiatorIQN(initiatorIQN);
        physicalMachine.setIpmiIp(ipmiIp);
        physicalMachine.setIpmiPort(ipmiPort);
        physicalMachine.setIpmiUser(ipmiUser);
        physicalMachine.setIpmiPassword(ipmiPassword);
        Set<Datastore> datastores = new HashSet<Datastore>();
        if (datastoresHB != null)
        {
            for (DatastoreHB datastoreHB : this.getDatastoresHB())
            {
                datastores.add(datastoreHB.toPojo());
            }
            physicalMachine.setDatastores(datastores);
        }
        physicalMachine.setIdEnterprise(getIdEnterprise());

        if (rack != null)
        {
            physicalMachine.setRack(rack.toPojo());
        }
        
        if (hypervisor != null)
        {
        	physicalMachine.setHypervisor(hypervisor.toPojo(physicalMachine));
        }

        return physicalMachine;
    }
}
