import { inject, Injectable } from '@angular/core';

import { of, tap } from 'rxjs';

import { SignalStore } from '@netz/common/store';

import { FacilityAuditControllerService, FacilityAuditUpdateDTO } from 'cca-api';

const initialState: FacilityAuditUpdateDTO = {
  auditRequired: false,
  reasons: [],
  comments: '',
};

@Injectable()
export class FacilityAuditStore extends SignalStore<FacilityAuditUpdateDTO> {
  private readonly facilityAuditControllerService = inject(FacilityAuditControllerService);

  constructor() {
    super(initialState);
  }

  init(facilityId: number) {
    return this.facilityAuditControllerService.getFacilityAuditViewByFacilityId(facilityId).pipe(
      tap((auditDTO) => {
        const audit: FacilityAuditUpdateDTO = {
          auditRequired: auditDTO.auditRequired,
          reasons: auditDTO.reasons ?? [],
          comments: auditDTO.comments ?? '',
        };

        this.updateState(audit);
      }),
    );
  }

  updateAudit(audit: FacilityAuditUpdateDTO, facilityId: number) {
    if (audit.auditRequired && (audit.reasons.length === 0 || audit.comments.length === 0)) {
      this.updateState({ auditRequired: audit.auditRequired });
      return of(this.state);
    }

    return this.facilityAuditControllerService.editFacilityAuditDetailsByFacilityId(facilityId, audit).pipe(
      tap((updatedAudit) => {
        this.updateState(updatedAudit);
      }),
    );
  }
}
