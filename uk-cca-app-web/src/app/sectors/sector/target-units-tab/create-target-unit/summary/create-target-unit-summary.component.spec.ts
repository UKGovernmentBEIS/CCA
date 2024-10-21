import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { CreateTargetUnitSummaryComponent } from './create-target-unit-summary.component';

describe('CreateTargetUnitSummaryComponent', () => {
  let component: CreateTargetUnitSummaryComponent;
  let fixture: ComponentFixture<CreateTargetUnitSummaryComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTargetUnitSummaryComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }, provideHttpClient(), CreateTargetUnitStore],
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
    expect(screen.getByText('New target unit')).toBeInTheDocument();
    expect(screen.getByText('Check your answers')).toBeInTheDocument();
  });

  it('should display the correct data sections', () => {
    expect(screen.getByTestId('target-unit-details-list')).toBeInTheDocument();
    expect(screen.getByTestId('operator-address-list')).toBeInTheDocument();
    expect(screen.getByTestId('responsible-person-list')).toBeInTheDocument();
    expect(screen.getByTestId('administrative-contact-list')).toBeInTheDocument();
  });

  it('should contain 18 change links', () => {
    const changeLinks = document.querySelectorAll('a[href="/?change=true"]');
    expect(changeLinks).toHaveLength(18);
  });
});
