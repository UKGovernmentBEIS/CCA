import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { underlyingAgreementActivationQuery } from '../../../underlying-agreement-activation/+state/una-activation.selectors';
import { cca3MigrationAccountActivationQuery } from '../../+state/cca3-migration-account-activation.selectors';

export const provideEvidenceRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const details = store.select(cca3MigrationAccountActivationQuery.selectCca3MigrationAccountActivationDetails)();

  const sectionCompleted = store.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK
  ];

  if (!sectionCompleted && !details) return createUrlTreeFromSnapshot(route, ['details']);

  if (sectionCompleted === TaskItemStatus.IN_PROGRESS && details) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return createUrlTreeFromSnapshot(route, ['summary']);
};
