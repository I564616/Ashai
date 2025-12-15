package com.sabmiller.sfmc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sabmiller.sfmc.constants.SabmsfmcservicesConstants;
import com.sabmiller.sfmc.enums.SFMCEmailRequestType;

/**
 * POJO Class for SFMC Email Request Data
 */
public class SFMCEmailRequestData
{


    @JsonProperty(value = "to", required = true)
    public SFMCEmailRequestToData to;
    @JsonProperty(value = "options", required = true)
    public SFMCEmailRequestOptionsData options;

    public SFMCEmailRequestData()
    {
        //Default Constructor is empty. Add code if necessary
    }

    /**
     * Overloaded Constructor for initiating Request object with To Data
     * @param toData
     */
    public SFMCEmailRequestData(SFMCEmailRequestToData toData)
    {

        this.to=toData;
        this.options=getDefaultOptionsData();
    }

    public SFMCEmailRequestToData getTo() {
        return to;
    }

    public void setTo(final SFMCEmailRequestToData to) {
        this.to = to;
    }

    public SFMCEmailRequestOptionsData getOptions() {
        return options;
    }

    public void setOptions(final SFMCEmailRequestOptionsData options) {
        this.options = options;
    }


    /**
     *
     * @return
     */
    private SFMCEmailRequestOptionsData getDefaultOptionsData()
    {
        return new SFMCEmailRequestOptionsData(SFMCEmailRequestType.SYNC.toString());
    }
}
