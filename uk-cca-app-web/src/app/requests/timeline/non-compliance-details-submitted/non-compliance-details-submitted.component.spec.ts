import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { NonComplianceDetailsSubmittedComponent } from './non-compliance-details-submitted.component';
import { nonComplianceDetailsSubmittedActionStateMock } from './tests/mock-data';

describe('NonComplianceDetailsSubmittedComponent', () => {
  let component: NonComplianceDetailsSubmittedComponent;
  let fixture: ComponentFixture<NonComplianceDetailsSubmittedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceDetailsSubmittedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(nonComplianceDetailsSubmittedActionStateMock);

    fixture = TestBed.createComponent(NonComplianceDetailsSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        [
          'Type of non-compliance',
          'When did the operator become non-compliant?',
          'When did the operator become compliant?',
          'Comments',
        ],
        ['failure to provide the Performance Report', '3 Mar 2020', '2 Feb 2020', 'sgfxcfb'],
      ],
      [
        ['Relevant tasks or workflows', 'Relevant facilities'],
        ['ADS_1-T00037-VAR-1ADS_1-T00037-VAR-2ADS_1-T00037-VAR-3', 'ADS_1-F00035erwywyweyer'],
      ],
      [
        [
          'Will you be issuing an Enforcement Response Notice?',
          'Why you will not be issuing an Enforcement Response Notice',
        ],
        ['No', 'fsfgs'],
      ],
    ]);
  });
});
