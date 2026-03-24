import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { SectorMoasReceivedAmountStore } from '../received-amount.store';
import { mockReceivedAmountStoreState } from '../testing/mock-data';
import { HistoryDetailsComponent } from './history-details.component';

describe('HistoryDetailsComponent', () => {
  let component: HistoryDetailsComponent;
  let fixture: ComponentFixture<HistoryDetailsComponent>;
  let sectorMoasReceivedAmountStore: SectorMoasReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryDetailsComponent],
      providers: [
        SectorMoasReceivedAmountStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ detailsId: 1 }) },
      ],
    }).compileComponents();

    sectorMoasReceivedAmountStore = TestBed.inject(SectorMoasReceivedAmountStore);
    sectorMoasReceivedAmountStore.setState(mockReceivedAmountStoreState);

    fixture = TestBed.createComponent(HistoryDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct amount data', () => {
    const detailsValues = getSummaryListData(fixture.nativeElement);

    expect(detailsValues).toEqual([
      [
        ['Amount added (GBP)', 'Comments', 'Uploaded evidence'],
        ['20', 'asdasdasdsad', 'sample_profile1.pngsample_profile.bmp'],
      ],
    ]);
  });
});
