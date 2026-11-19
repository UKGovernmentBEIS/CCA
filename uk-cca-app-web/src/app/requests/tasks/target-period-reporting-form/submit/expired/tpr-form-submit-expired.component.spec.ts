import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByRole, getByText } from '@testing';
import { produce } from 'immer';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../testing/mock-data';
import { TprFormSubmitExpiredComponent } from './tpr-form-submit-expired.component';

describe('TprFormSubmitExpiredComponent', () => {
  let component: TprFormSubmitExpiredComponent;
  let fixture: ComponentFixture<TprFormSubmitExpiredComponent>;

  const setReportType = (reportType: 'INTERIM' | 'FINAL') =>
    TestBed.inject(RequestTaskStore).setState(
      produce(mockTprRequestTaskStateThroughputTotalsOnly, (draft) => {
        (
          draft.requestTaskItem.requestTask.payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
        ).reportType = reportType;
      }),
    );

  const createComponent = async () => {
    fixture = TestBed.createComponent(TprFormSubmitExpiredComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprFormSubmitExpiredComponent],
      providers: [RequestTaskStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    setReportType('INTERIM');
    await createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the interim report expired message', () => {
    expect(getByText('Target period report expired', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'The task to submit the interim target period report for this facility has now expired.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should render the final report expired message', async () => {
    setReportType('FINAL');
    await createComponent();

    expect(
      getByText(
        'The task to submit the target period report for this facility has now expired.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should render a return to dashboard link', () => {
    const link = getByRole('link', { name: 'Return to: Dashboard' }, fixture.nativeElement);

    expect(link).toBeTruthy();
    expect(link.textContent?.trim()).toBe('Return to: Dashboard');
  });
});
