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

package com.abiquo.tarantino.plugins.esxi.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

/**
 * Stuff to ESXi connection. Read password from console
 */
class EsxiVim25Util
{
    protected final static Logger logger = LoggerFactory.getLogger(EsxiVim25Util.class);

    
    class EsxiVim25UtilException extends RuntimeException
    {
        private static final long serialVersionUID = -4841957566580698325L;

        public EsxiVim25UtilException(final String msg)
        {
            super(msg);
        }
    }

    private Map<String, String> optsEntered = new HashMap<String, String>();

    private Map<String, OptionSpec> userOpts = new HashMap<String, OptionSpec>();

    private Map<String, OptionSpec> builtInOpts = new HashMap<String, OptionSpec>();

    //private String _cname;

    protected ServiceInstance serviceInstance;

    public EsxiVim25Util(ServiceInstance serviceInstance, OptionSpec[] options,
        Map<String, String> optsEntered)
    {
        builtinOptions();
        
        addOptions(options);
        setOptsEntered(optsEntered);

        logger.debug("Connecting to "+ serviceInstance.toString());
        //_cname = serviceInstance.toString();
        this.serviceInstance = serviceInstance;        
    }

    // / EXTENDED

    public boolean isConnected()
    {
        return (serviceInstance != null && serviceInstance.getServerConnection() != null);
    }

    public void disConnect()
    {
        if (isConnected())
        {
            serviceInstance.getServerConnection().logout();
        }
        else
        {
            logger.warn("not connected, so not disconnect.");
        }
        
    }

    // / EXTENDED

    public ServiceInstance getServiceInstance()
    {
        return serviceInstance;
    }

//    public static EsxiVim25Util init(ServiceInstance serviceInstance, OptionSpec[] options,
//        Map<String, String> optsEntered) throws Exception
//    {
//        EsxiVim25Util cb = new EsxiVim25Util(serviceInstance);
//        cb.addOptions(options);
//        cb.setOptsEntered(optsEntered);
//        return cb;
//    }

    // public static EsxiVim25Util initialize(ServiceInstance serviceInstance,
    // OptionSpec[] userOptions, String[] args) throws Exception
    // {
    // EsxiVim25Util cb = new EsxiVim25Util(serviceInstance);
    // if (userOptions != null)
    // {
    // cb.addOptions(userOptions);
    // cb.parseInput(args);
    // cb.validate();
    // }
    // else
    // {
    // cb.parseInput(args);
    // cb.validate();
    // }
    // return cb;
    // }
    //
    // public static EsxiVim25Util initialize(ServiceInstance serviceInstance, String[] args)
    // throws Exception
    // {
    // EsxiVim25Util cb = initialize(serviceInstance, null, args);
    // return cb;
    // }
    //
    // public void init(ServiceInstance serviceInstance)
    // {
    // builtinOptions();
    // _cname = serviceInstance.toString();
    // this.serviceInstance = serviceInstance;
    // }

    public void addOptions(OptionSpec[] userOptions)
    {
        for (int i = 0; i < userOptions.length; i++)
        {
            if (userOptions[i].getOptionName() != null
                && userOptions[i].getOptionName().length() > 0
                && userOptions[i].getOptionDesc() != null
                && userOptions[i].getOptionDesc().length() > 0
                && userOptions[i].getOptionType() != null
                && userOptions[i].getOptionType().length() > 0
                && (userOptions[i].getOptionRequired() == 0 || userOptions[i].getOptionName()
                    .length() > 1))
            {
                userOpts.put(userOptions[i].getOptionName(), userOptions[i]);
            }
            else
            {
                throw new EsxiVim25UtilException("Option " + userOptions[i].getOptionName()
                    + " definition is not valid");
            }
        }
    }

    private void builtinOptions()
    {
        OptionSpec url = new OptionSpec("url", "String", 1, "VI SDK URL to connect to", null);
        OptionSpec userName =
            new OptionSpec("userName", "String", 1, "Username to connect to the host", null);
        OptionSpec password =
            new OptionSpec("password", "String", 1, "password of the corresponding user", null);
        OptionSpec config =
            new OptionSpec("config",
                "String",
                0,
                "Location of the VI perl configuration file",
                null);
        OptionSpec protocol =
            new OptionSpec("protocol", "String", 0, "Protocol used to connect to server", null);
        OptionSpec server = new OptionSpec("server", "String", 0, "VI server to connect to", null);
        OptionSpec portNumber =
            new OptionSpec("portNumber", "String", 0, "Port used to connect to server", "443");
        OptionSpec servicePath =
            new OptionSpec("servicePath",
                "String",
                0,
                "Service path used to connect to server",
                null);
        OptionSpec sessionFile =
            new OptionSpec("sessionFile",
                "String",
                0,
                "File containing session ID/cookie to utilize",
                null);
        OptionSpec help =
            new OptionSpec("help", "String", 0, "Display user information for the script", null);
        OptionSpec ignorecert =
            new OptionSpec("ignorecert",
                "String",
                0,
                "Ignore the server certificate validation",
                null);
        builtInOpts.put("url", url);
        builtInOpts.put("username", userName);
        builtInOpts.put("password", password);
        // builtInOpts.put("password", password);
        builtInOpts.put("config", config);
        builtInOpts.put("protocol", protocol);
        builtInOpts.put("server", server);
        builtInOpts.put("portnumber", portNumber);
        builtInOpts.put("servicepath", servicePath);
        builtInOpts.put("sessionfile", sessionFile);
        builtInOpts.put("help", help);
        builtInOpts.put("ignorecert", ignorecert);
    }

    public void parseInput(String args[]) throws Exception
    {
        try
        {
            getCmdArguments(args);
        }
        catch (Exception e)
        {
            throw new EsxiVim25UtilException("Exception running : " + e);
        }
        Iterator<String> It = optsEntered.keySet().iterator();
        while (It.hasNext())
        {
            String keyValue = It.next().toString();
            String keyOptions = optsEntered.get(keyValue);
            boolean result = checkInputOptions(builtInOpts, keyValue);
            boolean valid = checkInputOptions(userOpts, keyValue);
            if (result == false && valid == false)
            {

                throw new EsxiVim25UtilException("Invalid Input Option '" + keyValue + "'");
            }
            result = checkDatatypes(builtInOpts, keyValue, keyOptions);
            valid = checkDatatypes(userOpts, keyValue, keyOptions);
            if (result == false && valid == false)
            {
                throw new EsxiVim25UtilException("Invalid Input Option '" + keyValue + "'");
            }
        }
    }

    private void getCmdArguments(String args[]) throws Exception
    {
        int len = args.length;
        int i = 0;
        // boolean flag = false;
        if (len == 0)
        {
            throw new EsxiVim25UtilException("usage");
        }
        while (i < args.length)
        {
            String val = "";
            String opt = args[i];
            if (opt.startsWith("--") && optsEntered.containsKey(opt.substring(2)))
            {
                throw new EsxiVim25UtilException("key '" + opt.substring(2) + "' already exists ");
            }
            if (args[i].startsWith("--"))
            {
                if (args.length > i + 1)
                {
                    if (!args[i + 1].startsWith("--"))
                    {
                        val = args[i + 1];
                        optsEntered.put(opt.substring(2), val);
                    }
                    else
                    {
                        optsEntered.put(opt.substring(2), null);
                    }
                }
                else
                {
                    optsEntered.put(opt.substring(2), null);
                }
            }
            i++;
        }
    }

    private boolean checkDatatypes(Map<String, OptionSpec> Opts, String keyValue, String keyOptions)
    {
        boolean valid = false;
        valid = Opts.containsKey(keyValue);
        if (valid)
        {
            OptionSpec oSpec = (OptionSpec) Opts.get(keyValue);
            String dataType = oSpec.getOptionType();
            boolean result = validateDataType(dataType, keyOptions);
            return result;
        }
        else
        {
            return false;
        }
    }

    private boolean validateDataType(String dataType, String keyValue)
    {
        try
        {
            if (dataType.equalsIgnoreCase("Boolean"))
            {
                if (keyValue.equalsIgnoreCase("true") || keyValue.equalsIgnoreCase("false"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (dataType.equalsIgnoreCase("Integer"))
            {
                Integer.parseInt(keyValue);
                return true;
            }
            else if (dataType.equalsIgnoreCase("Float"))
            {
                Float.parseFloat(keyValue);
                return true;
            }
            else if (dataType.equalsIgnoreCase("Long"))
            {
                Long.parseLong(keyValue);
                return true;
            }
            else
            {
                // DO NOTHING
            }
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    private boolean checkInputOptions(Map<String, OptionSpec> checkOptions, String value)
    {
        boolean valid = false;
        valid = checkOptions.containsKey(value);
        return valid;
    }

    public void validate() throws Exception
    {
        validate(null, null);
    }

    public void validate(Object className, String functionName) throws Exception
    {
        // boolean flag = false;
        if (optsEntered.isEmpty())
        {
            throw new EsxiVim25UtilException("---help");
        }
        if (optsEntered.get("help") != null)
        {
            throw new EsxiVim25UtilException("why brother?");
        }
        if (isSessionOptionSet("help"))
        {
            throw new EsxiVim25UtilException("why brother?");
        }

        Vector<String> vec = getValue(builtInOpts);
        for (int i = 0; i < vec.size(); i++)
        {
            if (optsEntered.get(vec.get(i)) == null)
            {
                String missingArg = vec.get(i);
                if (missingArg.equalsIgnoreCase("password"))
                {
                    throw new EsxiVim25UtilException("Password not provided.");
                    // String password = readPassword("Enter password: ");
                    // optsEntered.put("password", password);
                }
                else
                {
                    throw new EsxiVim25UtilException("----ERROR: " + vec.get(i)
                        + " not specified \n");
                }
            }
        }
        vec = getValue(userOpts);
        for (int i = 0; i < vec.size(); i++)
        {
            if (optsEntered.get(vec.get(i)) == null)
            {
                throw new EsxiVim25UtilException("----ERROR: " + vec.get(i) + " not specified \n");
            }
        }
        if ((optsEntered.get("sessionfile") == null)
            && ((optsEntered.get("username") == null) && (optsEntered.get("password") == null)))
        {
            throw new EsxiVim25UtilException("Must have one of command options 'sessionfile' or a 'username' and 'password' pair\n");
        }
    }

    /*
     * taking out value of a particular key in the hashmapi.e checking for required =1 options
     */
    private Vector<String> getValue(Map<String, OptionSpec> checkOptions)
    {
        Iterator<String> It = checkOptions.keySet().iterator();
        Vector<String> vec = new Vector<String>();
        while (It.hasNext())
        {
            String str = It.next().toString();
            OptionSpec oSpec = (OptionSpec) checkOptions.get(str);
            if (oSpec.getOptionRequired() == 1)
            {
                vec.add(str);
            }
        }
        return vec;
    }

    public boolean isSessionOptionSet(String option)
    {
        boolean valid = false;
        Iterator<String> It = optsEntered.keySet().iterator();
        while (It.hasNext())
        {
            String keyVal = It.next().toString();
            if (option.equals(keyVal))
            {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * Gets the option from the VMWare session. XXX return null if not found
     * 
     * @param key, the name of the option to get.
     * @return the option value, null if not provided.
     */
    public String getSessionOption(String key)
    {
        if (optsEntered.get(key) != null)
        {
            return optsEntered.get(key).toString();
        }
        else if (checkInputOptions(builtInOpts, key))
        {
            if (((OptionSpec) builtInOpts.get(key)).getOptionDefault() != null)
            {
                String str = ((OptionSpec) builtInOpts.get(key)).getOptionDefault();
                return str;
            }
            else
            {
                return null;
            }
        }
        else if (checkInputOptions(userOpts, key))
        {
            if (((OptionSpec) userOpts.get(key)).getOptionDefault() != null)
            {
                String str = ((OptionSpec) userOpts.get(key)).getOptionDefault();
                return str;
            }
            else
            {
                return null;
            }
        }
        else
        {
            logger.warn(String.format("undefined variable : %s", key));
        }
        return null;
    }

    /**
     * @return web service url
     */
    public String getServiceUrl() throws Exception
    {
        // return _args[ARG_URL];
        return getSessionOption("url");
    }

    /**
     * @return web service username
     */
    public String getUsername() throws Exception
    {
        // return _args[ARG_USER];
        return getSessionOption("username");
    }

    /**
     * @return web service password
     */
    public String getPassword() throws Exception
    {
        /*
         * if (_args.length > ARG_PASSWD) { return _args[ARG_PASSWD]; } else { return ""; }
         */
        return getSessionOption("password");
    }

    public Map<String, OptionSpec> getBuiltInOpts()
    {
        return builtInOpts;
    }

    public void setBuiltInOpts(Map<String, OptionSpec> builtInOpts)
    {
        this.builtInOpts = builtInOpts;
    }

    public Map<String, String> getOptsEntered()
    {
        return optsEntered;
    }

    public void setOptsEntered(Map<String, String> optsEntered)
    {
        this.optsEntered = optsEntered;
    }
}
