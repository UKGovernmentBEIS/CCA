import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByLabelText, getByText } from '@testing';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { AdministrativeContactComponent } from './administrative-contact.component';

describe('AdministrativeContactComponent', () => {
  let component: AdministrativeContactComponent;
  let fixture: ComponentFixture<AdministrativeContactComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdministrativeContactComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }, provideHttpClient(), CreateTargetUnitStore],
    }).compileComponents();

    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);

    fixture = TestBed.createComponent(AdministrativeContactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('New target unit')).toBeTruthy();
    expect(getByText('Administrative contact details')).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByLabelText('Email address')).toBeTruthy();
    expect(getByLabelText('First name')).toBeTruthy();
    expect(getByLabelText('Last name')).toBeTruthy();
    expect(getByLabelText('Job title (optional)')).toBeTruthy();
    expect(getByLabelText('Phone number')).toBeTruthy();
    expect(getByText('The administrative contact address is the same as the responsible person address')).toBeTruthy();
    expect(getByLabelText('Address line 1')).toBeTruthy();
    expect(getByLabelText('Address line 2 (optional)')).toBeTruthy();
    expect(getByLabelText('Town or city')).toBeTruthy();
    expect(getByLabelText('County (optional)')).toBeTruthy();
    expect(getByLabelText('Postcode')).toBeTruthy();
    expect(getByLabelText('Country')).toBeTruthy();
  });
});
