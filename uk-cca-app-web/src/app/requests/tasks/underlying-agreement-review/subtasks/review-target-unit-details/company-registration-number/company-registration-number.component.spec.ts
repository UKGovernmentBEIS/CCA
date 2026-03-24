import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState } from '@requests/common';
import { getByLabelText, getByText } from '@testing';

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
    expect(getByText('Change', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Does the operator have a company number?', fixture.nativeElement)).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByLabelText('Company number', fixture.nativeElement)).toBeTruthy();
    expect(
      getByLabelText('Yes, the operator is registered and has a company number', fixture.nativeElement),
    ).toBeTruthy();
    expect(getByLabelText('No, the operator does not have a company number', fixture.nativeElement)).toBeTruthy();
  });
});
