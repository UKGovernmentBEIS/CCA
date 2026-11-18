import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { firstValueFrom, Observable, of } from 'rxjs';

import { Mocked } from 'vitest';

import { SectorAssociationSchemesDTO, SectorAssociationSchemeService } from 'cca-api';

import { canEditAdvancedDetailsGuard } from './can-edit-advanced-details.guard';

describe('canEditAdvancedDetailsGuard', () => {
  let sectorAssociationSchemeService: Mocked<SectorAssociationSchemeService>;
  let router: Mocked<Router>;
  let redirectUrlTree: UrlTree;

  beforeEach(() => {
    redirectUrlTree = {} as UrlTree;
    sectorAssociationSchemeService = {
      getSectorAssociationSchemeBySectorAssociationId: vi.fn(),
    } as unknown as Mocked<SectorAssociationSchemeService>;
    router = {
      createUrlTree: vi.fn().mockReturnValue(redirectUrlTree),
    } as unknown as Mocked<Router>;

    TestBed.configureTestingModule({
      providers: [
        { provide: SectorAssociationSchemeService, useValue: sectorAssociationSchemeService },
        { provide: Router, useValue: router },
      ],
    });
  });

  it('should allow access when the CCA3 scheme is editable', async () => {
    sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId.mockReturnValue(
      of(createSectorScheme(true)),
    );

    const result = await runGuard();

    expect(result).toBe(true);
    expect(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId).toHaveBeenCalledWith(123);
  });

  it('should redirect back to the sector Scheme tab when the CCA3 scheme is not editable', async () => {
    sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId.mockReturnValue(
      of(createSectorScheme(false)),
    );

    const result = await runGuard();

    expect(result).toBe(redirectUrlTree);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/sectors', 123], { fragment: 'scheme' });
  });

  function runGuard(): Promise<boolean | UrlTree> {
    const route = {
      paramMap: convertToParamMap({ sectorId: '123' }),
    } as ActivatedRouteSnapshot;

    return firstValueFrom(
      TestBed.runInInjectionContext(
        () => canEditAdvancedDetailsGuard(route, {} as RouterStateSnapshot) as Observable<boolean | UrlTree>,
      ),
    );
  }

  function createSectorScheme(editable: boolean): SectorAssociationSchemesDTO {
    return {
      sectorAssociationSchemeMap: {
        CCA_3: {
          id: 1,
          umbrellaAgreement: null,
          schemeVersion: 'CCA_3',
          editable,
        },
      },
    };
  }
});
