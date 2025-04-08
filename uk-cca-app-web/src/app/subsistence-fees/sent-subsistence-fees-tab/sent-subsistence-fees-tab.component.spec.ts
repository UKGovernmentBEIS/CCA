import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesStore } from '../subsistence-fees.store';
import { subsistenceFeesStateMockData } from '../tests/mock-data';
import { SentSubsistenceFeesTabComponent } from './sent-subsistence-fees-tab.component';

describe('SentSubsistenceFeesTabComponent', () => {
  let component: SentSubsistenceFeesTabComponent;
  let fixture: ComponentFixture<SentSubsistenceFeesTabComponent>;
  let subsistenceFeesStore: SubsistenceFeesStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SentSubsistenceFeesTabComponent],
      providers: [
        SubsistenceFeesStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    subsistenceFeesStore = TestBed.inject(SubsistenceFeesStore);
    subsistenceFeesStore.setState(subsistenceFeesStateMockData);

    fixture = TestBed.createComponent(SentSubsistenceFeesTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate message when no data available', () => {
    subsistenceFeesStore.setState({
      ...subsistenceFeesStore.state,
      totalSubsistenceFeesRunItems: 0,
      subsistenceFeesRuns: [],
    });

    fixture.detectChanges();

    expect(screen.getByText('No payment requests have been sent yet.')).toBeInTheDocument();
    expect(
      screen.getByText('More information will be available when one or more payment requests have been sent.'),
    ).toBeInTheDocument();
  });

  it('should populate with correct data', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(
      subsistenceFeesStateMockData.subsistenceFeesRuns.length + 1,
    );
  });
});
