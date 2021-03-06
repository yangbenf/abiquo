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

package com.abiquo.api.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.util.PagedList;

@Parent(DatacentersResource.class)
@Path(DatacenterResource.DATACENTER_PARAM)
@Controller
public class DatacenterResource extends AbstractResource
{

    public static final String DATACENTER = "datacenter";

    public static final String DATACENTER_PARAM = "{" + DATACENTER + "}";

    public static final String HYPERVISORS_PATH = "hypervisors";

    public static final String ENTERPRISES = "enterprises";

    public static final String ENTERPRISES_PATH = "action/enterprises";

    public static final String UPDATE_RESOURCES = "updateUsedResources";

    public static final String UPDATE_RESOURCES_PATH = "action/updateUsedResources";

    public static final String ENTERPRISES_REL = "enterprises";

    public static final String NETWORK = "network";

    @Autowired
    DatacenterService service;

    @Autowired
    NetworkService netService;

    @Autowired
    InfrastructureService infraService;

    @Context
    UriInfo uriInfo;

    @GET
    public DatacenterDto getDatacenter(@PathParam(DATACENTER) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);

        return createTransferObject(datacenter, restBuilder);
    }

    @PUT
    public DatacenterDto modifyDatacenter(final DatacenterDto datacenterDto,
        @PathParam(DATACENTER) final Integer datacenterId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter datacenter = createPersistenceObject(datacenterDto);
        datacenter = service.modifyDatacenter(datacenterId, datacenter);

        return createTransferObject(datacenter, restBuilder);
    }

    @GET
    @Path(ENTERPRISES_PATH)
    public EnterprisesDto getEnterprises(@PathParam(DATACENTER) final Integer datacenterId,
        @QueryParam(START_WITH) @Min(0) final Integer startwith,
        @QueryParam(NETWORK) Boolean network,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) @Min(1) final Integer limit,
        @Context final IRESTBuilder restBuilder) throws Exception

    {
        Integer firstElem = startwith == null ? 0 : startwith;
        Integer numElem = limit == null ? DEFAULT_PAGE_LENGTH : limit;
        if (network == null)
        {
            network = false;
        }

        Datacenter datacenter = service.getDatacenter(datacenterId);
        List<Enterprise> enterprises =
            service
                .findEnterprisesByDatacenterWithNetworks(datacenter, network, firstElem, numElem);
        EnterprisesDto enterprisesDto = new EnterprisesDto();
        for (Enterprise e : enterprises)
        {
            enterprisesDto.add(EnterpriseResource.createTransferObject(e, restBuilder));
        }
        enterprisesDto.setTotalSize(((PagedList) enterprises).getTotalResults());
        enterprisesDto.addLinks(buildEnterprisesLinks(uriInfo.getAbsolutePath().toString(),
            (PagedList) enterprises, network, numElem));
        return enterprisesDto;

    }

    @GET
    @Path(HYPERVISORS_PATH)
    public HypervisorTypesDto getAvailableHypervisors(
        @PathParam(DATACENTER) final Integer datacenterId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);

        Set<HypervisorType> types = service.getHypervisorTypes(datacenter);

        HypervisorTypesDto dto = new HypervisorTypesDto();
        dto.setCollection(new ArrayList<HypervisorType>(types));

        return dto;
    }

    // FIXME: Not allowed right now
    @DELETE
    public void deleteDatacenter(@PathParam(DATACENTER) final Integer datacenterId)
    {
        service.removeDatacenter(datacenterId);
    }

    @PUT
    @Path(UPDATE_RESOURCES_PATH)
    public void updateUsedResources(@PathParam(DATACENTER) final Integer datacenterId)
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);
        List<Rack> racks = service.getRacks(datacenter);
        for (Rack rack : racks)
        {
            List<Machine> machines = infraService.getMachines(rack);
            for (Machine machine : machines)
            {
                infraService.updateUsedResourcesByMachine(machine);
            }
        }

    }

    public static DatacenterDto addLinks(final IRESTBuilder builder, final DatacenterDto datacenter)
    {
        datacenter.setLinks(builder.buildDatacenterLinks(datacenter));

        return datacenter;
    }

    public static DatacenterDto createTransferObject(final Datacenter datacenter,
        final IRESTBuilder builder) throws Exception
    {
        DatacenterDto dto =
            ModelTransformer.transportFromPersistence(DatacenterDto.class, datacenter);
        dto = addLinks(builder, dto);
        return dto;
    }

    // Create the persistence object.
    public static Datacenter createPersistenceObject(final DatacenterDto datacenter)
        throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Datacenter.class, datacenter);
    }

    private List<RESTLink> buildEnterprisesLinks(final String Path, final PagedList< ? > list,
        final Boolean network, final Integer numElem)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        links.add(new RESTLink("first", Path));

        if (list.getCurrentElement() != 0)
        {
            Integer previous = list.getCurrentElement() - list.getPageSize();
            previous = previous < 0 ? 0 : previous;

            links.add(new RESTLink("prev", Path + "?" + NETWORK + "=" + network.toString() + '&'
                + AbstractResource.START_WITH + "=" + previous + '&' + AbstractResource.LIMIT + "="
                + numElem));
        }
        Integer next = list.getCurrentElement() + list.getPageSize();
        if (next < list.getTotalResults())
        {
            links.add(new RESTLink("next", Path + "?" + NETWORK + "=" + network.toString() + '&'
                + AbstractResource.START_WITH + "=" + next + '&' + AbstractResource.LIMIT + "="
                + numElem));
        }

        Integer last = list.getTotalResults() - list.getPageSize();
        if (last < 0)
        {
            last = 0;
        }
        links.add(new RESTLink("last", Path + "?" + NETWORK + "=" + network.toString() + '&'
            + AbstractResource.START_WITH + "=" + last + '&' + AbstractResource.LIMIT + "="
            + numElem));
        return links;
    }

}
