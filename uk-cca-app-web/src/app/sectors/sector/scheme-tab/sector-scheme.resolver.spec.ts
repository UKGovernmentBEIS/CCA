import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { Mocked } from 'vitest';

import { SectorAssociationSchemesDTO, SectorAssociationSchemeService } from 'cca-api';

import { SectorAssociationSchemeResolver } from './sector-scheme.resolver';

describe('SectorAssociationSchemeResolver', () => {
  let sectorAssociationSchemeService: Mocked<SectorAssociationSchemeService>;

  const sectorScheme: SectorAssociationSchemesDTO = {
    sectorAssociationSchemeMap: {},
  };

  beforeEach(() => {
    sectorAssociationSchemeService = {
      getSectorAssociationSchemeBySectorAssociationId: vi.fn().mockReturnValue(of(sectorScheme)),
    } as unknown as Mocked<SectorAssociationSchemeService>;

    TestBed.configureTestingModule({
      providers: [{ provide: SectorAssociationSchemeService, useValue: sectorAssociationSchemeService }],
    });
  });

  it('should resolve the sector scheme for the route sector id', async () => {
    const route = {
      paramMap: convertToParamMap({ sectorId: '123' }),
    } as ActivatedRouteSnapshot;

    const result = await firstValueFrom(TestBed.runInInjectionContext(() => SectorAssociationSchemeResolver(route)));

    expect(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId).toHaveBeenCalledWith(123);
    expect(result).toBe(sectorScheme);
  });
});
