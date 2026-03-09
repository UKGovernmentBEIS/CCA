import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState } from '@requests/common';
import { screen } from '@testing-library/dom';

import { CompanyRegistrationNumberComponent } from './company-registration-number.component';

const mockState = {
  ...mockRequestTaskState,
  payload: {
    underlyingAgreement: {
      underlyingAgreementTargetUnitDetails: {
        operatorName: 'Test Operator',
        operatorType: 'LIMITED_COMPANY',
        isCompanyRegistrationNumber: true,
        companyRegistrationNumber: '12345678',
        subsectorAssociationId: 1,
      },
    },
  },
  requestTaskId: 123,
  sectionsCompleted: {},
};

describe('CompanyRegistrationNumberComponent', () => {
  let component: CompanyRegistrationNumberComponent;
  let fixture: ComponentFixture<CompanyRegistrationNumberComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyRegistrationNumberComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }, provideHttpClient()],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockState);

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
