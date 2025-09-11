import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { SubsistenceFeesMoaDetailsDTO, SubsistenceFeesMoAViewService } from 'cca-api';

export const SectorMoasDetailsResolver: ResolveFn<SubsistenceFeesMoaDetailsDTO> = (route) => {
  const moaId = +route.paramMap.get('moaId');
  return inject(SubsistenceFeesMoAViewService).getSubsistenceFeesMoaDetailsById(moaId);
};
