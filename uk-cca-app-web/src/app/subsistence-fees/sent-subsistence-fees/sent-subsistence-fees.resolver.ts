import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { SubsistenceFeesRunDetailsDTO, SubsistenceFeesRunInfoViewService } from 'cca-api';

export const SentSubsistenceFeesDetailsResolver: ResolveFn<SubsistenceFeesRunDetailsDTO> = (route) => {
  const runId = +route.paramMap.get('runId');
  return inject(SubsistenceFeesRunInfoViewService).getSubsistenceFeesRunDetailsById(runId);
};
