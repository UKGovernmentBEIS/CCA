import { ActivatedRoute } from '@angular/router';

import { SchemeVersion } from '@shared/types';

import {
  SectorAssociationSchemeDTO,
  SectorAssociationSchemesDTO,
  SubsectorAssociationSchemeDTO,
  SubsectorAssociationSchemesDTO,
  TargetCommitmentDTO,
} from 'cca-api';

export type Cca3Scheme = SectorAssociationSchemeDTO | SubsectorAssociationSchemeDTO;

export function getCca3SchemeFromRoute(route: ActivatedRoute): Cca3Scheme {
  const scheme = (route.snapshot.data.sectorScheme ?? route.snapshot.data.subSector) as
    | SectorAssociationSchemesDTO
    | SubsectorAssociationSchemesDTO;

  return getCca3Scheme(scheme);
}

export function getCca3Scheme(scheme: SectorAssociationSchemesDTO | SubsectorAssociationSchemesDTO): Cca3Scheme {
  if (!scheme) return null;

  if ('sectorAssociationSchemeMap' in scheme) {
    return scheme.sectorAssociationSchemeMap?.[SchemeVersion.CCA_3];
  }

  return scheme.subsectorAssociationSchemeMap?.[SchemeVersion.CCA_3];
}

export function sortTargetCommitments(targetCommitments: TargetCommitmentDTO[] = []): TargetCommitmentDTO[] {
  return [...targetCommitments].sort(
    (a, b) => targetPeriodSortValue(a.targetPeriod) - targetPeriodSortValue(b.targetPeriod),
  );
}

function targetPeriodSortValue(targetPeriod: string): number {
  return Number(targetPeriod.trim().substring(0, 4).replace(/\D/g, ''));
}
