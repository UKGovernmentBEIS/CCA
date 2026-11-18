import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByLabelText, getByText } from '@testing';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { OperatorAddressComponent } from './operator-address.component';

describe('OperatorAddressComponent', () => {
  let component: OperatorAddressComponent;
  let fixture: ComponentFixture<OperatorAddressComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorAddressComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }, provideHttpClient(), CreateTargetUnitStore],
    }).compileComponents();

    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);

    fixture = TestBed.createComponent(OperatorAddressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('New target unit')).toBeTruthy();
    expect(getByText('Operator address')).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByLabelText('Address line 1')).toBeTruthy();
    expect(getByLabelText('Address line 2 (optional)')).toBeTruthy();
    expect(getByLabelText('Town or city')).toBeTruthy();
    expect(getByLabelText('County (optional)')).toBeTruthy();
    expect(getByLabelText('Postcode')).toBeTruthy();
    expect(getByLabelText('Country')).toBeTruthy();
  });
});
