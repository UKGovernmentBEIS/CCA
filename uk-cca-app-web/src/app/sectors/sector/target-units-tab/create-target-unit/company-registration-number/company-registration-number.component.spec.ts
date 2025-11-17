import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { CompanyRegistrationNumberComponent } from './company-registration-number.component';

describe('CompanyRegistrationNumberComponent', () => {
  let component: CompanyRegistrationNumberComponent;
  let fixture: ComponentFixture<CompanyRegistrationNumberComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyRegistrationNumberComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        provideHttpClient(),
        CreateTargetUnitStore,
      ],
    }).compileComponents();

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
    expect(screen.getByText('New target unit')).toBeInTheDocument();
    expect(screen.getByText('Does the applicant have a company number?')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByLabelText('Company number')).toBeInTheDocument();
    expect(screen.getByLabelText('Yes, the applicant is registered and has a company number')).toBeInTheDocument();
    expect(screen.getByLabelText('No, the applicant does not have a company number')).toBeInTheDocument();
  });
});
