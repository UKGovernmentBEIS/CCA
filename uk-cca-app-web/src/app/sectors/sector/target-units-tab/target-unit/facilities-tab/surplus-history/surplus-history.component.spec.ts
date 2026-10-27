import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SurplusHistoryDTO } from 'cca-api';

import { mockFacilityDetails } from '../testing/mock-data';
import FacilitySurplusHistoryComponent from './surplus-history.component';

describe('FacilitySurplusHistoryComponent', () => {
  let fixture: ComponentFixture<FacilitySurplusHistoryComponent>;
  let http: HttpTestingController;

  const history: SurplusHistoryDTO[] = [
    { surplusGained: '200', submitter: 'batch run ID BOS-TP7001', submissionDate: '2027-05-01T15:20:00' },
    {
      surplusGained: '1250',
      submitter: 'Regulator user',
      submissionDate: '2027-06-21T15:20:00',
      comments: 'Correction to the surplus.\nSupporting explanation.',
    },
    { surplusGained: '1600', submitter: 'batch run ID BOS-TP8003', submissionDate: '2028-09-01T15:20:00' },
    {
      surplusGained: '0',
      submitter: 'Regulator user',
      submissionDate: '2027-05-02T15:20:00',
      comments: 'Reset surplus',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilitySurplusHistoryComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ facilityId: 1 }, null, {
            facilityDetails: { ...mockFacilityDetails, facilityId: 1, facilityBusinessId: 'AIC-F00040' },
          }),
        },
      ],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(FacilitySurplusHistoryComponent);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  function respond(entries = history) {
    const request = http.expectOne((req) => req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/history'));
    expect(request.request.method).toBe('GET');
    request.flush(entries);
    fixture.detectChanges();
  }

  function expandedCount() {
    return fixture.nativeElement.querySelectorAll('.govuk-accordion__section--expanded').length;
  }

  function click(selector: string) {
    fixture.nativeElement.querySelector(selector).click();
    fixture.detectChanges();
  }

  it('should display the facility caption and newest-first manual and batch entries, collapsed by default', () => {
    respond();
    expect(fixture.nativeElement.querySelector('h1').textContent).toContain('Surplus gained change history');
    expect(fixture.nativeElement.textContent).toContain('AIC-F00040');
    const headings = Array.from<HTMLElement>(
      fixture.nativeElement.querySelectorAll('.govuk-accordion__section-heading-text-focus'),
    );
    expect(headings.map((heading) => heading.textContent.trim())).toEqual([
      'Amount changed by batch run ID BOS-TP8003',
      'Amount changed by “Regulator user”',
      'Amount changed by “Regulator user”',
      'Amount changed by batch run ID BOS-TP7001',
    ]);
    expect(fixture.nativeElement.textContent).toContain('1 Sept 2028');
    expect(fixture.nativeElement.querySelector('.govuk-accordion__show-all').textContent).toContain(
      'Show all sections',
    );
    expect(expandedCount()).toBe(0);
  });

  it('should show all details and return to Show all as soon as one entry is collapsed', () => {
    respond();
    click('.govuk-accordion__show-all');
    expect(expandedCount()).toBe(4);
    expect(fixture.nativeElement.querySelector('.govuk-accordion__show-all').textContent).toContain(
      'Hide all sections',
    );

    const entries = Array.from<HTMLElement>(fixture.nativeElement.querySelectorAll('govuk-accordion-item'));
    expect(entries.map((entry) => entry.querySelector('dd').textContent.trim())).toEqual([
      '1,600',
      '1,250',
      '0',
      '200',
    ]);
    expect(entries[0].textContent).not.toContain('Comments');
    expect(entries[1].querySelector('.pre-line').textContent).toBe(history[1].comments);

    click('.govuk-accordion__section-button');
    expect(expandedCount()).toBe(3);
    expect(fixture.nativeElement.querySelector('.govuk-accordion__show-all').textContent).toContain(
      'Show all sections',
    );
    click('.govuk-accordion__show-all');
    expect(expandedCount()).toBe(4);
    click('.govuk-accordion__show-all');
    expect(expandedCount()).toBe(0);
  });

  it('should expand an individual entry and start collapsed when revisiting', () => {
    respond();
    click('.govuk-accordion__section-button');
    expect(expandedCount()).toBe(1);
    fixture.destroy();
    fixture = TestBed.createComponent(FacilitySurplusHistoryComponent);
    respond();
    expect(expandedCount()).toBe(0);
  });

  it('should preserve large amounts and system comments', () => {
    respond([{ ...history[2], surplusGained: '9007199254740993', comments: 'TP8 surplus recalculated' }]);
    click('.govuk-accordion__show-all');
    expect(fixture.nativeElement.querySelector('dd').textContent.trim()).toBe('9,007,199,254,740,993');
    expect(fixture.nativeElement.querySelector('.pre-line').textContent).toBe('TP8 surplus recalculated');
  });

  it('should only display the empty message once an empty history is returned', () => {
    expect(fixture.nativeElement.textContent).not.toContain('There are no surplus gained changes.');
    respond([]);
    expect(fixture.nativeElement.textContent).toContain('There are no surplus gained changes.');
    expect(fixture.nativeElement.querySelector('govuk-accordion')).toBeNull();
  });

  it('should cancel the previous facility history request when resolved data changes', () => {
    const firstRequest = http.expectOne((req) => req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/history'));
    (TestBed.inject(ActivatedRoute) as unknown as ActivatedRouteStub).setResolveMap({
      facilityDetails: { ...mockFacilityDetails, facilityId: 2, facilityBusinessId: 'AIC-F00041' },
    });
    fixture.detectChanges();

    expect(firstRequest.cancelled).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('AIC-F00041');
    expect(fixture.nativeElement.textContent).not.toContain('AIC-F00040');
    http.expectOne((req) => req.url.endsWith('/v1.0/facilities/2/buy-out-surplus/history')).flush(history);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('govuk-accordion-item')).toHaveLength(history.length);
  });

  it('should sort by instant across offsets and put undated entries last', () => {
    respond([
      { surplusGained: '1', submitter: 'Undated first' },
      { surplusGained: '2', submitter: 'Earlier', submissionDate: '2027-10-31T01:30:00+01:00' },
      { surplusGained: '3', submitter: 'Undated second', submissionDate: null },
      { surplusGained: '4', submitter: 'Later', submissionDate: '2027-10-31T01:15:00Z' },
    ]);

    const entries = Array.from<HTMLElement>(fixture.nativeElement.querySelectorAll('govuk-accordion-item'));
    expect(entries.map((entry) => entry.querySelector('dd').textContent.trim())).toEqual(['4', '2', '1', '3']);
  });
});
