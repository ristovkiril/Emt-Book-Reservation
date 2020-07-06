package mk.ukim.finki.emt.sharedkernel.infra.eventlog;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import mk.ukim.finki.emt.sharedkernel.domain.base.DomainEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class DomainEventLogService {

    private final StoredDomainEventRepository storedDomainEventRepository;
    private final ObjectMapper objectMapper;

    DomainEventLogService(StoredDomainEventRepository storedDomainEventRepository,
                          ObjectMapper objectMapper) {
        this.storedDomainEventRepository = storedDomainEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void append(@NonNull DomainEvent domainEvent) {
        var storedEvent = new StoredDomainEvent(domainEvent, objectMapper);
        System.out.println("vo domain event log service kaj so se zacuvuva event");
        storedDomainEventRepository.saveAndFlush(storedEvent);
    }

    @NonNull
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<StoredDomainEvent> retrieveLog(long lastProcessedEventId) {
        var max = storedDomainEventRepository.findHighestDomainEventId();
        if (max == null) {
            max = 0L;
        }
        return storedDomainEventRepository.findEventsBetween(lastProcessedEventId,max);
    }
}
