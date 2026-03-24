import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByLabelText, getByText } from '@testing';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { CompanyRegistrationNumberComponent } from './company-registration-number.component';

describe('CompanyRegistrationNumberComponent', () => {
  let component: CompanyRegistrationNumberComponent;
  let fixture: ComponentFixture<CompanyRegistrationNumberComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;
  let router: Router;
  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyRegistrationNumberComponent, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }, provideHttpClient(), CreateTargetUnitStore],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');
    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);

    fixture = TestBed.createComponent(CompanyRegistrationNumberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('New target unit')).toBeTruthy();
    expect(getByText('Does the operator have a company number?')).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByLabelText('Company number')).toBeTruthy();
    expect(getByLabelText('Yes, the operator is registered and has a company number')).toBeTruthy();
    expect(getByLabelText('No, the operator does not have a company number')).toBeTruthy();
  });

  it('should update the target unit state when the operator has no company number', () => {
    const form = component['form'];

    form.controls.isCompanyRegistrationNumber.setValue(false);
    form.controls.registrationNumberMissingReason.setValue('Not registered');

    component.onSubmitCompanyRegistrationNumber();

    expect(createTargetUnitStore.state.isCompanyRegistrationNumber).toBe(false);
    expect(createTargetUnitStore.state.companyRegistrationNumber).toBeUndefined();
    expect(createTargetUnitStore.state.registrationNumberMissingReason).toBe('Not registered');
    expect(createTargetUnitStore.state.name).toBeNull();
    expect(createTargetUnitStore.state.sicCodes).toEqual([]);
    expect(router.navigate).toHaveBeenCalledWith(['..', 'target-unit-details'], { relativeTo: route });
  });
});
