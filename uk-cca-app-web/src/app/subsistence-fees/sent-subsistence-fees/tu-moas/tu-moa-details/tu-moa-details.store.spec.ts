import { TestBed } from '@angular/core/testing';

import { SubsistenceFeesMoaFacilitySearchResultInfoDTO, SubsistenceFeesMoAViewService } from 'cca-api';
import { SubsistenceFeesMoATargetUnitViewService } from 'cca-api';

import { TuMoaDetailsStore } from './tu-moa-details.store';

describe('TuMoaDetailsStore', () => {
  const facility = {
    facilityBusinessId: 'F1',
    markFacilitiesStatus: 'IN_PROGRESS',
  } as SubsistenceFeesMoaFacilitySearchResultInfoDTO;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: SubsistenceFeesMoAViewService, useValue: {} },
        { provide: SubsistenceFeesMoATargetUnitViewService, useValue: {} },
      ],
    });
  });

  const createStore = () => TestBed.runInInjectionContext(() => new TuMoaDetailsStore());

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
