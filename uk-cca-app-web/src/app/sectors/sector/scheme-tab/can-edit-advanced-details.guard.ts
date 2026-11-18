import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { map } from 'rxjs';

import { SchemeVersion } from '@shared/types';

import { SectorAssociationSchemeService } from 'cca-api';

export const canEditAdvancedDetailsGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  const sectorId = +route.paramMap.get('sectorId');

  return sectorAssociationSchemeService
    .getSectorAssociationSchemeBySectorAssociationId(sectorId)
    .pipe(
      map((sectorScheme) =>
        sectorScheme.sectorAssociationSchemeMap?.[SchemeVersion.CCA_3]?.editable
          ? true
          : router.createUrlTree(['/sectors', sectorId], { fragment: 'scheme' }),
      ),
    );
};
