import { TestBed } from '@angular/core/testing';

import { SubsistenceFeesMoaTargetUnitSearchResultInfoDTO, SubsistenceFeesMoAViewService } from 'cca-api';

import { SectorMoaDetailsStore } from './sector-moa-details.store';

describe('SectorMoaDetailsStore', () => {
  const targetUnit = {
    businessId: 'TU1',
    markFacilitiesStatus: 'IN_PROGRESS',
  } as SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SubsistenceFeesMoAViewService, useValue: {} }],
    });
  });

  const createStore = () => TestBed.runInInjectionContext(() => new SectorMoaDetailsStore());

  it('should replace the selection Map instead of mutating it', () => {
    const store = createStore();
    const before = store.state.selectedTUs;

    store.updateSelectedTUs(targetUnit, true);

    expect(store.state.selectedTUs).not.toBe(before);
    expect([...store.state.selectedTUs.keys()]).toEqual(['TU1']);
    expect(before.size).toBe(0);

    store.updateSelectedTUs(targetUnit, false);

    expect(store.state.selectedTUs.size).toBe(0);
  });
});
