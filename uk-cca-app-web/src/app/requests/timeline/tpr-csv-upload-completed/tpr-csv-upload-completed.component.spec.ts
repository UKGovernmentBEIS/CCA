import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { mockRequestActionStateTPRCSVUpload } from './testing/mock-data';
import { TprCSVUploadCompletedComponent } from './tpr-csv-upload-completed.component';

describe('TprCSVUploadCompletedComponent', () => {
  let component: TprCSVUploadCompletedComponent;
  let fixture: ComponentFixture<TprCSVUploadCompletedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprCSVUploadCompletedComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionStateTPRCSVUpload);

    fixture = TestBed.createComponent(TprCSVUploadCompletedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct summary data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toHaveLength(2);

    expect(summaryValues[0]).toEqual([
      ['Reporting period', 'Report type', 'Uploaded files'],
      ['TP7', 'Final', 'dummy.csv (opens in a new tab)'],
    ]);

    const [headers, values] = summaryValues[1];
    expect(headers).toEqual([
      'Time submitted',
      'Files uploaded',
      'Facilities successful',
      'Facilities failed',
      'Submission summary file',
    ]);

    expect(values[0]).toBe('3 Jul 2026 - 8:28:35');

    expect(values[1]).toBe('1');
    expect(values[2]).toBe('0');
    expect(values[3]).toBe('1');
    expect(values[4]).toBe('Upload_Summary.csv (opens in a new tab)');
  });
});
