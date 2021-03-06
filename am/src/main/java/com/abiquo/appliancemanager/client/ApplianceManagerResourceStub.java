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

package com.abiquo.appliancemanager.client;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.ws.rs.WebApplicationException;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.uri.UriEncoder;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.appliancemanager.util.URIResolver;

public class ApplianceManagerResourceStub
{
    private RestClient client;

    private RestClient clientTimeout;

    private final String serviceUri;

    /**
     * Timeout only of ''slow nfs filesystem access'' (getting the repository usage or refresh the
     * available packages)
     */
    private final static Integer CLIENT_TIMEOUT_MS = Integer.parseInt(System.getProperty(
        "abiquo.appliancemanager.timeout", "5000")); // default 5seconds

    public ApplianceManagerResourceStub(final String serviceUri)
    {
        super();
        this.serviceUri = serviceUri;
        this.client = new RestClient();

        ClientConfig confTimeout = new ClientConfig();
        confTimeout.readTimeout(CLIENT_TIMEOUT_MS);
        this.clientTimeout = new RestClient(confTimeout);
    }

    Resource ovfPackage(final String idEnterprise, final String idOVF)
    {
        Map<String, String> params;
        try
        {
            params = new HashMap<String, String>()
            {
                {
                    // FIXME ABICLOUDPREMIUM-1798
                    put("erepo", idEnterprise);
                    put("ovfpi", URLEncoder.encode(idOVF, "UTF-8"));
                }
            };
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Can not encode", e);
        }

        String url = URIResolver.resolveURI(serviceUri, "erepos/{erepo}/ovfs/{ovfpi}", params);

        Resource resource = client.resource(url);

        return resource;
    }

    Resource ovfPackages(final String idEnterprise)
    {
        final String url =
            URIResolver.resolveURI(serviceUri, "erepos/{erepo}/ovfs",
                Collections.singletonMap("erepo", idEnterprise));

        Resource resource = client.resource(url);

        return resource;
    }

    Resource ovfPackagesTimeout(final String idEnterprise)
    {
        final String url =
            URIResolver.resolveURI(serviceUri, "erepos/{erepo}/ovfs",
                Collections.singletonMap("erepo", idEnterprise));

        Resource resource = clientTimeout.resource(url);

        return resource;
    }

    Resource repository(final String idEnterprise)
    {
        return repository(idEnterprise, false);
    }

    /**
     * Timeout
     */
    Resource repository(final String idEnterprise, final boolean checkCanWrite)
    {
        String url =
            URIResolver.resolveURI(serviceUri, "erepos/{erepo}",
                Collections.singletonMap("erepo", idEnterprise));

        if (checkCanWrite)
        {
            Map<String, String[]> queryParams = new HashMap<String, String[]>();
            queryParams.put("checkCanWrite", new String[] {String.valueOf(checkCanWrite)});
            url = UriHelper.appendQueryParamsToPath(url, queryParams, false);
        }

        Resource resource = clientTimeout.resource(url);

        return resource;
    }

    Resource repositories()
    {
        final String url = String.format("%s/%s", serviceUri, "erepos");

        Resource resource = client.resource(url);

        return resource;
    }

    Resource check()
    {
        final String url = String.format("%s/%s", serviceUri, "check");

        Resource resource = client.resource(url);

        return resource;
    }

}
