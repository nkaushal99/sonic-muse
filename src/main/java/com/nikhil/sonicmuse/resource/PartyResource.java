package com.nikhil.sonicmuse.resource;

import com.nikhil.sonicmuse.mapper.PartyMapper;
import com.nikhil.sonicmuse.pojo.PartyDTO;
import com.nikhil.sonicmuse.service.PartyService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import software.amazon.awssdk.utils.StringUtils;

@Path("/party")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PartyResource
{
    private final PartyService partyService = PartyService.getInstance();

    @GET
    public PartyDTO getPartyDetails(@QueryParam("id") String partyId)
    {
        if (StringUtils.isBlank(partyId))
            throw new BadRequestException("partyId is blank");

        PartyMapper party = partyService.getPartyMapper(partyId);
        if (party == null)
            throw new NotFoundException("party not found");

        return partyService.createPartyDTO(party);
    }
}
