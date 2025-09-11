import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../testing/mock-data';
import { FacilitySubmittedComponent } from './facility-submitted.component';

describe('FacilityComponent', () => {
  let fixture: ComponentFixture<FacilitySubmittedComponent>;
  let store: RequestActionStore;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilitySubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(FacilitySubmittedComponent);
    fixture.detectChanges();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot('facility-summary');
  });
});
