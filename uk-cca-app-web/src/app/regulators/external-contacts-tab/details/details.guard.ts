import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { CaExternalContactsService } from 'cca-api';

import { viewNotFoundExternalContactError } from '../../errors/business-error';
import { ActiveExternalContactStore } from '../active-external-contact.store';

export function ExternalContactDetailsGuard(route: ActivatedRouteSnapshot) {
  const caExternalContactsService = inject(CaExternalContactsService);
  const store = inject(ActiveExternalContactStore);
  const businessErrorService = inject(BusinessErrorService);
  return caExternalContactsService.getCaExternalContactById(Number(route.paramMap.get('userId'))).pipe(
    tap((ec) => store.setState(ec)),
    map(() => true),
    catchBadRequest(ErrorCodes.EXTCONTACT1000, () => businessErrorService.showError(viewNotFoundExternalContactError)),
  );
}
