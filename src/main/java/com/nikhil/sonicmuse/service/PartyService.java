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

import java.util.HashSet;
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

    public PartyMapper getPartyMapper(String partyId)
    {
        return partyRepository.findPartyById(partyId);
    }

    public PartyMapper createParty(String hostId)
    {
        PartyMapper partyMapper = new PartyMapper();
        partyMapper.setHostId(hostId);

        Set<String> attendees = new HashSet<>();
        attendees.add(hostId);
        partyMapper.setAttendeeIds(attendees);

        return partyMapper;
    }

    public PartyDTO createPartyDTO(PartyMapper partyMapper)
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
        if (partyMapper.isMarkedForDeletion())
        {
            partyRepository.delete(partyMapper);
            return;
        }
        partyRepository.put(partyMapper);
    }

}
