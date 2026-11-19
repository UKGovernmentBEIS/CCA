import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilityDetails } from '../testing/mock-data';
import EditFacilitySurplusComponent from './edit-surplus.component';

describe('EditFacilitySurplusComponent', () => {
  let fixture: ComponentFixture<EditFacilitySurplusComponent>;
  let component: EditFacilitySurplusComponent;
  let http: HttpTestingController;
  let route: ActivatedRouteStub;

  beforeEach(async () => {
    route = new ActivatedRouteStub({ facilityId: 1 }, null, {
      facilityDetails: {
        ...mockFacilityDetails,
        surplusDetails: { schemeVersion: 'CCA_3', surplusGained: '1627', hasHistory: true },
      },
    });
    await TestBed.configureTestingModule({
      imports: [EditFacilitySurplusComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: route,
        },
      ],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(EditFacilitySurplusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  function submit() {
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    fixture.detectChanges();
  }

  it('should show the current amount and explanation hint', () => {
    expect(fixture.nativeElement.textContent).toContain('1,627');
    expect(fixture.nativeElement.textContent).toContain('Explain why you are changing the amount of surplus gained');
  });

  it('should block submission with empty fields', () => {
    submit();
    expect(fixture.nativeElement.textContent).toContain('Enter a surplus amount');
    expect(fixture.nativeElement.textContent).toContain('Enter a comment');
    http.expectNone((request) => request.method === 'POST');
  });

  it('should display every digit of the current amount and compare it without rounding', () => {
    TestBed.inject(ActivatedRoute).snapshot.data.facilityDetails.surplusDetails.surplusGained = '9007199254740993';
    fixture.destroy();
    fixture = TestBed.createComponent(EditFacilitySurplusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('dd').textContent.trim()).toBe('9,007,199,254,740,993');
    component.form().setValue({ surplus: '9007199254740993', comments: 'Correction' });
    expect(component.form().controls.surplus.hasError('sameAsCurrent')).toBe(true);
    component.form().controls.surplus.setValue('9007199254740992');
    expect(component.form().valid).toBe(true);
  });

  it.each(['-1', '1.5', 'abc', '1e3', '1627', '01627'])('should reject invalid or unchanged surplus %s', (surplus) => {
    component.form().setValue({ surplus, comments: 'Correcting the surplus amount' });
    expect(component.form().controls.surplus.invalid).toBe(true);
    submit();
    http.expectNone((request) => request.method === 'POST');
  });

  it.each(['', '   ', 'a'.repeat(10001)])('should reject missing or excessive comments', (comments) => {
    component.form().setValue({ surplus: '10', comments });
    expect(component.form().controls.comments.invalid).toBe(true);
  });

  it.each(['0', '2000', '9007199254740993'])(
    'should send surplus %s unchanged to the facility endpoint and wait for success',
    (surplus) => {
      const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
      const comments = 'Correcting the surplus amount';
      component.form().setValue({ surplus, comments });
      submit();

      const batchCheck = http.expectOne((req) => req.url.endsWith('/v1.0/requests/workflows'));
      expect(batchCheck.request.body).toEqual(
        expect.objectContaining({
          requestTypes: ['BUY_OUT_SURPLUS_RUN', 'BUY_OUT_SURPLUS_FACILITY_RUN'],
          requestStatuses: ['IN_PROGRESS'],
        }),
      );
      http.expectNone((req) => req.url.endsWith('/buy-out-surplus/'));
      batchCheck.flush({ requestDetails: [], total: 0 });

      const request = http.expectOne((req) => req.url.endsWith('/v1.0/facilities/1/buy-out-surplus/'));
      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({ newSurplusGained: surplus, comments });
      expect(navigate).not.toHaveBeenCalled();

      request.flush(null, { status: 204, statusText: 'No Content' });
      expect(navigate).toHaveBeenCalledWith(['confirmation'], { relativeTo: TestBed.inject(ActivatedRoute) });
    },
  );

  it.each(['BUY_OUT_SURPLUS_RUN', 'BUY_OUT_SURPLUS_FACILITY_RUN'])(
    'should block an adjustment during %s and allow retry when the run finishes',
    (requestType) => {
      const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
      component.form().setValue({ surplus: '0', comments: 'Correction' });
      submit();
      http
        .expectOne((req) => req.url.endsWith('/v1.0/requests/workflows'))
        .flush({
          requestDetails: [{ requestType, requestStatus: 'IN_PROGRESS' }],
          total: 1,
        });
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain(
        'You cannot make an adjustment while a Batch Run is in progress.',
      );
      expect(component.form().getRawValue()).toEqual({ surplus: '0', comments: 'Correction' });
      http.expectNone((req) => req.url.endsWith('/buy-out-surplus/'));
      expect(navigate).not.toHaveBeenCalled();

      submit();
      http.expectOne((req) => req.url.endsWith('/v1.0/requests/workflows')).flush({ requestDetails: [], total: 0 });
      http
        .expectOne((req) => req.url.endsWith('/buy-out-surplus/'))
        .flush(null, { status: 204, statusText: 'No Content' });
      fixture.detectChanges();
      expect(fixture.nativeElement.querySelector('[role="alert"]')).toBeNull();
      expect(navigate).toHaveBeenCalled();
    },
  );
});
