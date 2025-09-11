import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { mockFacilityDetails } from '../test/mock-data';
import { ChangeCertificationStatusComponent } from './change-certification-status.component';

describe('ChangeCertificationStatusComponent', () => {
  let component: ChangeCertificationStatusComponent;
  let fixture: ComponentFixture<ChangeCertificationStatusComponent>;

  const activatedRouteStub = {
    snapshot: {
      paramMap: convertToParamMap({ certificationPeriod: 'CP6' }),
      data: { facilityDetails: mockFacilityDetails },
    },
    paramMap: of(convertToParamMap({ facilityId: mockFacilityDetails.facilityId })),
  } as unknown as ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeCertificationStatusComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ChangeCertificationStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component instance', () => {
    expect(component).toBeTruthy();
  });

  it('should resolve the correct certificationPeriodId for CP6', () => {
    expect(component.certificationPeriodId).toBe(1);
  });

  it('should expose the correct certificationPeriodDuration label', () => {
    expect(component.certificationPeriodDuration).toContain('Certification Period 6');
  });

  it('should initialize form as valid when status is CERTIFIED and startDate is prepopulated', () => {
    expect(component.form.controls.certificationStatus.value).toBe('CERTIFIED');
    expect(component.form.controls.startDate.value).toBeInstanceOf(Date);
    expect(component.form.valid).toBeTruthy();
  });

  it('should have startDate validators when status is CERTIFIED', () => {
    const startControl = component.form.controls.startDate;
    startControl.setValue('');
    const errors = startControl.errors || {};
    expect(errors['required']).toBeDefined();
  });

  it('should clear startDate validators and make form valid when status changes to DECERTIFIED', () => {
    const startControl = component.form.controls.startDate;
    component.form.controls.certificationStatus.setValue('DECERTIFIED');
    fixture.detectChanges();
    expect(startControl.errors).toBeNull();
    expect(component.form.valid).toBeTruthy();
  });
});
