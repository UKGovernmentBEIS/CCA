import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { getSummaryListData } from '@testing';

import { FacilityPATReportStore } from '../../../../facility-pat-report.store';
import { mockFacilityPATStore } from '../../../testing/mock-data';
import { EntryDetailsComponent } from './entry-details.component';

describe('EntryDetailsComponent', () => {
  let component: EntryDetailsComponent;
  let fixture: ComponentFixture<EntryDetailsComponent>;
  let store: FacilityPATReportStore;

  const route = { snapshot: { params: { entryId: '0' } } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntryDetailsComponent],
      providers: [FacilityPATReportStore, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(FacilityPATReportStore);
    store.setState(mockFacilityPATStore);

    fixture = TestBed.createComponent(EntryDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        [
          'Description',
          'Category',
          'Supply/demand side action',
          'Reason for implementation',
          'Implementation date',
          'Fixed/Variable energy or carbon emissions',
          'Percentage of total energy consumption or carbon affected (%)',
          'Expected extent of implementation (%)',
          'Expected efficiency improvement (%)',
          'Estimated overall impact (%)',
          'Notes',
        ],
        [
          'Created an energy management system',
          'Energy management',
          'Demand side',
          'It seemed like a good idea at the time',
          '01 Jan 2026',
          'Fixed and variable',
          '10',
          '20',
          '-586',
          '-11.72',
          '',
        ],
      ],
    ]);
  });
});
