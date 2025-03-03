package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.mapper.AttendeeMapper;
import com.nikhil.sonicmuse.mapper.PartyMapper;
import com.nikhil.sonicmuse.pojo.AttendeeDTO;
import com.nikhil.sonicmuse.pojo.PartyDTO;
import com.nikhil.sonicmuse.repository.AttendeeRepository;
import com.nikhil.sonicmuse.repository.PartyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PartyService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyService.class);

    private static PartyService instance;

    private final PartyRepository partyRepository = PartyRepository.getInstance();
    private final AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();

    private PartyService()
    {
    }

    public static PartyService getInstance()
    {
        if (instance == null)
            instance = new PartyService();
        return instance;
    }

    public PartyDTO getPartyDetails(String partyId)
    {
        PartyMapper partyMapper = getPartyMapper(partyId);
        if (partyMapper == null)
            return null;
        return createPartyDTO(partyMapper);
    }

    public PartyMapper getPartyMapper(String partyId)
    {
        return partyRepository.findPartyById(partyId);
    }

    public PartyMapper getPartyCreateIfAbsent(String partyId)
    {
        PartyMapper partyMapper = partyRepository.findPartyById(partyId);
        if(partyMapper == null)
        {
            PartyDTO partyDTO = new PartyDTO();
            partyDTO.setId(partyId);
            partyMapper = createParty(partyDTO);
        }
        return partyMapper;
    }

    public PartyMapper createParty(PartyDTO partyDTO)
    {
        PartyMapper partyMapper = new PartyMapper();
        if (partyDTO.getId() != null)
            partyMapper.setId(partyDTO.getId());
//        partyMapper.setAttendeeIds(partyDTO.getAttendees());
        partyMapper.setHostId(partyDTO.getHostId());
        return partyMapper;
    }

    private PartyDTO createPartyDTO(PartyMapper partyMapper)
    {
        Set<AttendeeDTO> attendeeDTOSet = partyMapper.getAttendeeIds().stream()
            .map(attendeeRepository::findAttendeeById)
            .map(AttendeeMapper::getDTO)
            .collect(Collectors.toSet());

        PartyDTO partyDTO = new PartyDTO();
        partyDTO.setId(partyMapper.getId());
        partyDTO.setAttendees(attendeeDTOSet);
        partyDTO.setHostId(partyMapper.getHostId());
        return partyDTO;
    }

    public List<PartyDTO> getAllParties()
    {
        SdkIterable<PartyMapper> allParties = partyRepository.findAllParties();
        return allParties.stream().map(this::createPartyDTO).toList();
    }

    public void deleteParty(String partyId)
    {
        PartyMapper partyMapper = partyRepository.findPartyById(partyId);
        partyRepository.delete(partyMapper);
    }

    public void saveParty(PartyMapper partyMapper)
    {
        partyRepository.put(partyMapper);
    }

}
