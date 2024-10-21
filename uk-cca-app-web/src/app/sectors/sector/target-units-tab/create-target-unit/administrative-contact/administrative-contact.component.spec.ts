import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

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
    expect(screen.getByText('New target unit')).toBeInTheDocument();
    expect(screen.getByText('Administrative contact details')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByLabelText('Email address')).toBeInTheDocument();
    expect(screen.getByLabelText('First name')).toBeInTheDocument();
    expect(screen.getByLabelText('Last name')).toBeInTheDocument();
    expect(screen.getByLabelText('Job title (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Phone number')).toBeInTheDocument();
    expect(
      screen.getByText('The administrative contact address is the same as the responsible person address'),
    ).toBeInTheDocument();
    expect(screen.getByLabelText('Address line 1')).toBeInTheDocument();
    expect(screen.getByLabelText('Address line 2 (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Town or city')).toBeInTheDocument();
    expect(screen.getByLabelText('County (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Postcode')).toBeInTheDocument();
    expect(screen.getByLabelText('Country')).toBeInTheDocument();
  });
});
