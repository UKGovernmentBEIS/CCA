import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { CompaniesHouseDetailsComponent } from './companies-house-details.component';

const mockCompaniesHouseState = {
  details: {
    name: 'COMPANY 73388338 LIMITED',
    registrationNumber: '73388338',
    operatorType: 'Private limited company',
    sicCodes: ['71200'],
    address: {
      line1: 'Companies House',
      line2: 'Crownway',
      city: 'Cardiff',
      postcode: 'CF14 3UZ',
      country: 'United Kingdom',
    },
  },
  error: null,
  address: {
    line1: 'Companies House',
    line2: 'Crownway',
    city: 'Cardiff',
    postcode: 'CF14 3UZ',
    country: 'United Kingdom',
  },
};

const mockTargetUnitDetails = {
  operatorName: 'COMPANY 73388338 LIMITED',
  operatorAddress: {
    line1: 'Companies House',
    line2: 'Crownway',
    city: 'Cardiff',
    postcode: 'CF14 3UZ',
    country: 'United Kingdom',
  },
  responsiblePersonDetails: {
    name: 'John Doe',
    email: 'john.doe@example.com',
    phone: '1234567890',
  },
};

describe('CompaniesHouseDetailsComponent', () => {
  let component: CompaniesHouseDetailsComponent;
  let componentRef: ComponentRef<CompaniesHouseDetailsComponent>;
  let fixture: ComponentFixture<CompaniesHouseDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompaniesHouseDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CompaniesHouseDetailsComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    componentRef.setInput('toggleCompaniesHouseDetails', true);
    componentRef.setInput('companiesHouseState', mockCompaniesHouseState);
    componentRef.setInput('tuDetails', mockTargetUnitDetails);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
