import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { ResponsiblePersonComponent } from './responsible-person.component';

describe('ResponsiblePersonComponent', () => {
  let component: ResponsiblePersonComponent;
  let fixture: ComponentFixture<ResponsiblePersonComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsiblePersonComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }, provideHttpClient(), CreateTargetUnitStore],
    }).compileComponents();

    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);

    fixture = TestBed.createComponent(ResponsiblePersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('New target unit')).toBeInTheDocument();
    expect(screen.getByText('Responsible person')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByLabelText('Email address')).toBeInTheDocument();
    expect(screen.getByLabelText('First name')).toBeInTheDocument();
    expect(screen.getByLabelText('Last name')).toBeInTheDocument();
    expect(screen.getByLabelText('Job title (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Phone number')).toBeInTheDocument();
    expect(screen.getByText('The responsible person address is the same as the operator address')).toBeInTheDocument();
    expect(screen.getByLabelText('Address line 1')).toBeInTheDocument();
    expect(screen.getByLabelText('Address line 2 (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Town or city')).toBeInTheDocument();
    expect(screen.getByLabelText('County (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Postcode')).toBeInTheDocument();
    expect(screen.getByLabelText('Country')).toBeInTheDocument();
  });
});
