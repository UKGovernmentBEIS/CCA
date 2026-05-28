import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText, queryByText } from '@testing';

import { mockNonComplianceDetailsState } from '../../tasks/non-compliance-details/testing/mock-data';
import { NonComplianceSummaryComponent } from './non-compliance-summary.component';

describe('NonComplianceSummaryComponent', () => {
  let fixture: ComponentFixture<NonComplianceSummaryComponent>;
  let store: RequestTaskStore;

  const createComponent = (isEditable = false) => {
    store.setState({ ...mockNonComplianceDetailsState, isEditable });

    fixture = TestBed.createComponent(NonComplianceSummaryComponent);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceSummaryComponent],
      providers: [
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
  });

  it('should render populated non-compliance summary sections', () => {
    createComponent();

    expect(getByText('Summary', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Non-compliance details', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Type of non-compliance', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Relevant workflows', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Relevant facilities', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Enforcement details', fixture.nativeElement)).toBeTruthy();
  });

  it('should hide change links when the task is not editable', () => {
    createComponent(false);

    expect(queryByText('Change', fixture.nativeElement)).toBeNull();
  });

  it('should show change links when the task is editable', () => {
    createComponent(true);

    expect(getByText('Change', fixture.nativeElement)).toBeTruthy();
  });
});
