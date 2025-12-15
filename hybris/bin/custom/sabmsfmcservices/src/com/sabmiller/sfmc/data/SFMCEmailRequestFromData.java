package com.sabmiller.sfmc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class for Email From Object.
 * no longer needed anymore, controller by SFMC
 */
@Deprecated
public class SFMCEmailRequestFromData  implements java.io.Serializable
{

    @JsonProperty(value = "address", required = true)
    private String address;
    @JsonProperty(value = "name", required = true)
    private String name;

    public SFMCEmailRequestFromData()
    {
        // default constructor
    }

    public SFMCEmailRequestFromData(String address, String name)
    {
        this.address = address;
        this.name = name;
    }


    public void setAddress(final String address)
    {
        this.address = address;
    }


    public String getAddress()
    {
        return address;
    }


    public void setName(final String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }

}