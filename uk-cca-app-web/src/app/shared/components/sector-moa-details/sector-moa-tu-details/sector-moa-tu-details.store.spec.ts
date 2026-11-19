import { TestBed } from '@angular/core/testing';

import { SubsistenceFeesMoaFacilitySearchResultInfoDTO, SubsistenceFeesMoATargetUnitViewService } from 'cca-api';

import { SectorMoaTUDetailsStore } from './sector-moa-tu-details.store';

describe('SectorMoaTUDetailsStore', () => {
  const facility = {
    facilityBusinessId: 'F1',
    markFacilitiesStatus: 'IN_PROGRESS',
  } as SubsistenceFeesMoaFacilitySearchResultInfoDTO;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SubsistenceFeesMoATargetUnitViewService, useValue: {} }],
    });
  });

  const createStore = () => TestBed.runInInjectionContext(() => new SectorMoaTUDetailsStore());

  it('should replace the selection Map instead of mutating it', () => {
    const store = createStore();
    const before = store.state.selectedFacilities;

    store.updateSelectedTUMoAFacilities(facility, true);

    expect(store.state.selectedFacilities).not.toBe(before);
    expect([...store.state.selectedFacilities.keys()]).toEqual(['F1']);
    expect(before.size).toBe(0);
  });

  it('should start a new store with an empty selection after another store selected facilities', () => {
    createStore().updateSelectedTUMoAFacilities(facility, true);

    expect(createStore().state.selectedFacilities.size).toBe(0);
  });
});
