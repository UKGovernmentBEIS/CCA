import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { mockRequestTaskState } from '@requests/common';

import { VariationFacilityDetailsFormComponent } from './variation-facility-details-form.component';
import { VARIATION_FACILITY_DETAILS_FORM } from './variation-facility-details-form.provider';

@Component({
  template: `<form [formGroup]="form"><cca-variation-facility-details-form /></form>`,
  imports: [VariationFacilityDetailsFormComponent, ReactiveFormsModule],
})
class TestHostComponent {
  form = new FormGroup({}); // Only used to host the component in the template
}

describe('VariationFacilityDetailsFormComponent', () => {
  let component: VariationFacilityDetailsFormComponent;
  let fixture: ComponentFixture<TestHostComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VariationFacilityDetailsFormComponent],
      providers: [
        RequestTaskStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: VARIATION_FACILITY_DETAILS_FORM,
          deps: [FormBuilder],
          useFactory: (fb: FormBuilder) =>
            fb.group({
              name: new FormControl('Test Facility'),
              facilityId: new FormControl('TEST_ID'),
              isCoveredByUkets: new FormControl(null),
              uketsId: new FormControl(''),
              applicationReason: new FormControl('NEW_AGREEMENT'),
              previousFacilityId: new FormControl(''),
              participatingSchemeVersions: new FormControl([]),
              schemeParticipationChoice: new FormControl(null),
              sameAddress: new FormControl([false]),
              facilityAddress: new FormGroup({
                city: new FormControl(null),
                country: new FormControl(null),
                line1: new FormControl(null),
                line2: new FormControl(null),
                postcode: new FormControl(null),
                county: new FormControl(null),
              }),
            }),
        },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { facilityId: 'ADS_F00001' } } },
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();

    component = fixture.debugElement.query(By.directive(VariationFacilityDetailsFormComponent)).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
