import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByLabelText, getByText } from '@testing';

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
    expect(getByText('New target unit')).toBeTruthy();
    expect(getByText('Responsible person')).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByLabelText('Email address')).toBeTruthy();
    expect(getByLabelText('First name')).toBeTruthy();
    expect(getByLabelText('Last name')).toBeTruthy();
    expect(getByLabelText('Job title (optional)')).toBeTruthy();
    expect(getByLabelText('Phone number')).toBeTruthy();
    expect(getByText('The responsible person address is the same as the operator address')).toBeTruthy();
    expect(getByLabelText('Address line 1')).toBeTruthy();
    expect(getByLabelText('Address line 2 (optional)')).toBeTruthy();
    expect(getByLabelText('Town or city')).toBeTruthy();
    expect(getByLabelText('County (optional)')).toBeTruthy();
    expect(getByLabelText('Postcode')).toBeTruthy();
    expect(getByLabelText('Country')).toBeTruthy();
  });
});
