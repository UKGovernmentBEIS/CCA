import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

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
    expect(screen.getByText('New target unit')).toBeInTheDocument();
    expect(screen.getByText('Operator address')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByLabelText('Address line 1')).toBeInTheDocument();
    expect(screen.getByLabelText('Address line 2 (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Town or city')).toBeInTheDocument();
    expect(screen.getByLabelText('County (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Postcode')).toBeInTheDocument();
    expect(screen.getByLabelText('Country')).toBeInTheDocument();
  });
});
