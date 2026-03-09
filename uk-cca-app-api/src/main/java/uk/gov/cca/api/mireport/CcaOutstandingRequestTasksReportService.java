package uk.gov.cca.api.mireport;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.system.outstandingrequesttasks.OutstandingRequestTasksReportService;

import java.util.Set;

@Service
public class CcaOutstandingRequestTasksReportService implements OutstandingRequestTasksReportService {
    @Override
    public Set<String> getRequestTaskTypesByRoleType(String roleType) {
        return Set.of();
    }
}
