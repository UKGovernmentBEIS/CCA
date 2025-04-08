import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SubsistenceFeesComponent } from './subsistence-fees.component';
import { SubsistenceFeesStore } from './subsistence-fees.store';
import { subsistenceFeesStateMockData } from './tests/mock-data';

describe('SubsistenceFeesComponent', () => {
  let component: SubsistenceFeesComponent;
  let fixture: ComponentFixture<SubsistenceFeesComponent>;
  let subsistenceFeesStore: SubsistenceFeesStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubsistenceFeesComponent],
      providers: [
        SubsistenceFeesStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    subsistenceFeesStore = TestBed.inject(SubsistenceFeesStore);
    subsistenceFeesStore.setState(subsistenceFeesStateMockData);

    fixture = TestBed.createComponent(SubsistenceFeesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display title', () => {
    const title = document.querySelector('h1');
    expect(title.textContent).toBe('Subsistence fees payment requests');
  });

  it('should display `New payment request button`', () => {
    const button = document.querySelector('button');
    expect(button.textContent.trim()).toBe('New payment request');
  });

  it('should display charge date warning', () => {
    subsistenceFeesStore.setState({
      ...subsistenceFeesStore.state,
      isValidChargeDate: false,
    });

    fixture.detectChanges();

    const warning = document.querySelector('strong.govuk-warning-text__text');
    expect(warning.textContent.trim()).toBe('New payment requests will become available after 1 Apr 2025');
  });

  it('should display payment request in progress warning', () => {
    subsistenceFeesStore.setState({
      ...subsistenceFeesStore.state,
      runInProgress: true,
      isValidChargeDate: true,
    });

    fixture.detectChanges();

    const warning = document.querySelector('strong.govuk-warning-text__text');
    expect(warning.textContent.trim()).toBe(
      'Payment request run is in progress, you cannot initiate a new one until it has finished',
    );
  });
});
