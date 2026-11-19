import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { BuyOutSurplusTransactionDetailHistoryDTO } from 'cca-api';

import { Cca3ChangeHistoryComponent } from './cca3-change-history.component';

describe('Cca3ChangeHistoryComponent', () => {
  let fixture: ComponentFixture<Cca3ChangeHistoryComponent>;
  let http: HttpTestingController;

  const history: BuyOutSurplusTransactionDetailHistoryDTO[] = [
    {
      id: 1,
      submitter: 'Regulator user',
      submissionDate: '2027-05-02T15:20:00',
      payload: {
        type: 'PAYMENT_STATUS_CHANGED',
        paymentStatus: 'UNDER_APPEAL',
        dateOfAppeal: '2027-05-01',
        paymentDate: '2027-04-01',
        comments: 'Appeal received.\nAwaiting a decision.',
      },
    },
    {
      id: 3,
      submitter: 'Regulator user',
      submissionDate: '2027-06-02T15:20:00',
      payload: {
        type: 'PAYMENT_STATUS_CHANGED',
        paymentStatus: 'PAID',
        paymentDate: '2027-06-01',
        dateOfAppeal: '2027-05-01',
        evidenceFiles: { 'evidence-uuid': 'Evidence 1.pdf' },
      },
    },
    {
      id: 2,
      submitter: 'Amount reviewer',
      submissionDate: '2027-05-03T15:20:00',
      payload: { type: 'AMOUNT_CHANGED', amount: '1250', comments: 'Amount corrected' },
    },
    {
      id: 4,
      submitter: 'batch run ID BOS-TP7005',
      submissionDate: '2027-09-01T15:20:00',
      payload: { type: 'PAYMENT_STATUS_CHANGED', paymentStatus: 'TERMINATED' },
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cca3ChangeHistoryComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ transactionId: '1001', facilityBusinessId: 'AIC-F00040' }, null, {
            transactionDetails: { id: 1001, transactionCode: 'CCA070021' },
            facilityDetails: { id: 2001, facilityBusinessId: 'AIC-F00040', siteName: 'Facility name 40' },
          }),
        },
      ],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Cca3ChangeHistoryComponent);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  function respond(entries = history) {
    const request = http.expectOne('/api/v1.0/buy-out-surplus/transaction-details/2001/history');
    expect(request.request.method).toBe('GET');
    request.flush(entries);
    fixture.detectChanges();
  }

  function items() {
    return Array.from<HTMLElement>(fixture.nativeElement.querySelectorAll('govuk-accordion-item'));
  }

  function rows(item: HTMLElement) {
    return Array.from(item.querySelectorAll('.govuk-summary-list__row')).map((row) => {
      const value = row.querySelector('dd').cloneNode(true) as HTMLElement;
      value.querySelectorAll('.govuk-visually-hidden').forEach((text) => text.remove());
      return [row.querySelector('dt').textContent.trim(), value.textContent.trim()];
    });
  }

  function expandedCount() {
    return fixture.nativeElement.querySelectorAll('.govuk-accordion__section--expanded').length;
  }

  function toggleAll() {
    fixture.nativeElement.querySelector('.govuk-accordion__show-all').click();
    fixture.detectChanges();
  }

  it('loads only this facility transaction history and displays the identifying summary', () => {
    respond();
    expect(fixture.nativeElement.querySelector('h1').textContent).toContain('Change history');
    expect(fixture.nativeElement.textContent).toContain('AIC-F00040');
    expect(rows(fixture.nativeElement.querySelector('cca-summary'))).toEqual([
      ['Transaction ID', 'CCA070021'],
      ['Site name', 'Facility name 40'],
    ]);
  });

  it('orders events newest first, distinguishes batch runs, and starts with all entries collapsed', () => {
    respond();
    expect(
      items().map((item) => item.querySelector('.govuk-accordion__section-heading-text-focus').textContent.trim()),
    ).toEqual([
      'Status changed by batch run ID BOS-TP7005',
      'Status changed by “Regulator user”',
      'Amount changed by “Amount reviewer”',
      'Status changed by “Regulator user”',
    ]);
    expect(items()[0].textContent).toContain('1 Sept 2027');
    expect(expandedCount()).toBe(0);
    expect(fixture.nativeElement.querySelector('.govuk-accordion__show-all').textContent).toContain(
      'Show all sections',
    );
  });

  it.each([undefined, null, '', '   '])('uses a fallback when the submitter is %j', (submitter) => {
    respond([
      { ...history[0], submitter },
      { ...history[2], submitter },
    ]);

    expect(
      items().map((item) => item.querySelector('.govuk-accordion__section-heading-text-focus').textContent.trim()),
    ).toEqual(['Amount changed by “Unknown user”', 'Status changed by “Unknown user”']);
  });

  it('shows only status for termination and conditional dates, comments and evidence for manual updates', () => {
    respond();
    toggleAll();
    const [terminated, paid, amount, appeal] = items();
    expect(rows(terminated)).toEqual([['Status', 'Terminated']]);
    expect(rows(paid)).toEqual([
      ['Status', 'Paid'],
      ['Date when the payment was received in the bank account', '1 Jun 2027'],
      ['Comments', 'Not provided'],
      ['Uploaded evidence', 'Evidence 1.pdf'],
    ]);
    expect(paid.querySelector('a').getAttribute('href')).toContain(
      '/file-evidences-download/2001/attachment/evidence-uuid',
    );
    expect(rows(amount)).toEqual([
      ['Current amount (GBP)', '1,250.00'],
      ['Comments', 'Amount corrected'],
      ['Uploaded evidence', 'Not provided'],
    ]);
    expect(rows(appeal)).toEqual([
      ['Status', 'Under appeal'],
      ['Date on which the appeal was lodged', '1 May 2027'],
      ['Comments', 'Appeal received.\nAwaiting a decision.'],
      ['Uploaded evidence', 'Not provided'],
    ]);
    expect(appeal.querySelector('.pre-line').textContent.trim()).toBe('Appeal received.\nAwaiting a decision.');
    expect(fixture.nativeElement.textContent).not.toMatch(/Edit|Delete/);
  });

  it('shows Hide all only while every entry is open, including after individual toggles', () => {
    respond();
    const control = fixture.nativeElement.querySelector('.govuk-accordion__show-all');
    items().forEach((item) => item.querySelector<HTMLButtonElement>('.govuk-accordion__section-button').click());
    fixture.detectChanges();
    expect(expandedCount()).toBe(4);
    expect(control.textContent).toContain('Hide all sections');

    items()[0].querySelector<HTMLButtonElement>('.govuk-accordion__section-button').click();
    fixture.detectChanges();
    expect(expandedCount()).toBe(3);
    expect(control.textContent).toContain('Show all sections');

    toggleAll();
    expect(expandedCount()).toBe(4);
    toggleAll();
    expect(expandedCount()).toBe(0);
  });

  it('starts collapsed again when the history page is revisited', () => {
    respond();
    toggleAll();
    fixture.destroy();
    fixture = TestBed.createComponent(Cca3ChangeHistoryComponent);
    respond();
    expect(expandedCount()).toBe(0);
  });

  it('does not create initial history entries or show empty controls', () => {
    expect(fixture.nativeElement.textContent).not.toContain('There is no change history yet.');
    respond([]);
    expect(fixture.nativeElement.textContent).toContain('There is no change history yet.');
    expect(fixture.nativeElement.querySelector('govuk-accordion')).toBeNull();
  });

  it.each(['AWAITING_PAYMENT', 'AWAITING_REFUND', 'REFUNDED', 'NOT_REQUIRED'] as const)(
    'retains genuine %s changes without payment or appeal dates',
    (paymentStatus) => {
      respond([
        {
          ...history[0],
          payload: {
            type: 'PAYMENT_STATUS_CHANGED',
            paymentStatus,
            paymentDate: '2027-05-01',
            dateOfAppeal: '2027-05-01',
          },
        },
      ]);
      expect(rows(items()[0]).map(([key]) => key)).toEqual(['Status', 'Comments', 'Uploaded evidence']);
    },
  );

  it('preserves amount precision and downloadable evidence on amount changes', () => {
    respond([
      {
        ...history[2],
        payload: {
          type: 'AMOUNT_CHANGED',
          amount: '9007199254740993.25',
          evidenceFiles: { 'amount-uuid': 'Amount.pdf' },
        },
      },
    ]);
    expect(rows(items()[0])[0]).toEqual(['Current amount (GBP)', '9,007,199,254,740,993.25']);
    expect(items()[0].querySelector('a').getAttribute('href')).toContain(
      '/file-evidences-download/2001/attachment/amount-uuid',
    );
  });
});
