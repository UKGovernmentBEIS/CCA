import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import { ActivatedRouteStub } from '@netz/common/testing';
import { StatusPipe } from '@shared/pipes';

import { SubsistenceFeesMoAFacilityViewService } from 'cca-api';

import { MarkingHistoryComponent } from './marking-history.component';

describe('MarkingHistoryComponent', () => {
  let component: MarkingHistoryComponent;
  let fixture: ComponentFixture<MarkingHistoryComponent>;
  let debugElement: HTMLElement;
  let subsistenceFeesMoAFacilityViewService: Partial<jest.Mocked<SubsistenceFeesMoAFacilityViewService>>;

  const history = {
    facilityBusinessId: 'ADS_50-F00001',
    siteName: 'fac52-2-1',
    markingStatusHistoryList: [
      {
        submitter: 'Regulator England',
        submissionDate: '2025-05-16T17:04:17.72588Z',
        paymentStatus: 'CANCELLED',
      },
      {
        submitter: 'Regulator England',
        submissionDate: '2025-05-15T12:49:12.838904Z',
        paymentStatus: 'IN_PROGRESS',
      },
      {
        submitter: 'Regulator England',
        submissionDate: '2025-05-15T12:11:02.335412Z',
        paymentStatus: 'COMPLETED',
      },
    ],
  };

  beforeEach(async () => {
    subsistenceFeesMoAFacilityViewService = {
      getSubsistenceFeesMoaFacilityMarkingStatusHistoryInfo: jest.fn().mockReturnValue(of(history)),
    };

    await TestBed.configureTestingModule({
      imports: [MarkingHistoryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ moaFacilityId: 1 }) },
        { provide: SubsistenceFeesMoAFacilityViewService, useValue: subsistenceFeesMoAFacilityViewService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MarkingHistoryComponent);
    component = fixture.componentInstance;
    debugElement = fixture.nativeElement;
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display approppriate content', () => {
    const statusPipe = new StatusPipe();
    const govukDatePipe = new GovukDatePipe();

    const caption = debugElement.querySelector('.govuk-caption-l');
    expect(caption.textContent.trim()).toBe(history.facilityBusinessId);

    const heading = debugElement.querySelector('.govuk-heading-l');
    expect(heading.textContent.trim()).toBe(history.siteName);

    const listHeadings = debugElement.querySelectorAll('.govuk-heading-s');
    expect(listHeadings[0].textContent.trim()).toBe(
      `Facility marked as ${statusPipe.transform(history.markingStatusHistoryList[0].paymentStatus)} by ${history.markingStatusHistoryList[0].submitter}`,
    );
    expect(listHeadings[1].textContent.trim()).toBe(
      `Facility marked as ${statusPipe.transform(history.markingStatusHistoryList[1].paymentStatus)} by ${history.markingStatusHistoryList[1].submitter}`,
    );
    expect(listHeadings[2].textContent.trim()).toBe(
      `Facility marked as ${statusPipe.transform(history.markingStatusHistoryList[2].paymentStatus)} by ${history.markingStatusHistoryList[2].submitter}`,
    );

    const listDates = debugElement.querySelectorAll('p[data-testid="date-info"]');
    expect(listDates[0].textContent.trim()).toBe(
      `${govukDatePipe.transform(history.markingStatusHistoryList[0].submissionDate, 'datetime')}`,
    );
    expect(listDates[1].textContent.trim()).toBe(
      `${govukDatePipe.transform(history.markingStatusHistoryList[1].submissionDate, 'datetime')}`,
    );
    expect(listDates[2].textContent.trim()).toBe(
      `${govukDatePipe.transform(history.markingStatusHistoryList[2].submissionDate, 'datetime')}`,
    );
  });
});
