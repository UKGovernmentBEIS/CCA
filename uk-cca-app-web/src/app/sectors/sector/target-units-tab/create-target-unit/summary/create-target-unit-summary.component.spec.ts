import { provideHttpClient } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { CountryService } from '@shared/services';
import { getByTestId, getByText } from '@testing';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { CreateTargetUnitSummaryComponent } from './create-target-unit-summary.component';

describe('CreateTargetUnitSummaryComponent', () => {
  let component: CreateTargetUnitSummaryComponent;
  let fixture: ComponentFixture<CreateTargetUnitSummaryComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  const route = new ActivatedRouteStub();

  const mockCountryService = {
    countries: signal([
      { code: 'GB', name: 'United Kingdom', officialName: 'United Kingdom' },
      { code: 'GR', name: 'Greece', officialName: 'Greece' },
    ]),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTargetUnitSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: CountryService, useValue: mockCountryService },
        provideHttpClient(),
        CreateTargetUnitStore,
      ],
    }).compileComponents();

    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);

    fixture = TestBed.createComponent(CreateTargetUnitSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('New target unit')).toBeTruthy();
    expect(getByText('Check your answers')).toBeTruthy();
  });

  it('should display the correct data sections', () => {
    expect(getByTestId('target-unit-details-list')).toBeTruthy();
    expect(getByTestId('operator-address-list')).toBeTruthy();
    expect(getByTestId('responsible-person-list')).toBeTruthy();
    expect(getByTestId('administrative-contact-list')).toBeTruthy();
  });

  it('should contain 17 change links', () => {
    const changeLinks = document.querySelectorAll('a[href$="?change=true"]');
    expect(changeLinks).toHaveLength(17);
  });
});
