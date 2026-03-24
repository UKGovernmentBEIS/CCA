import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { mockSentSubsistenceFeesDetails } from '@shared/components';
import { getSummaryListData } from '@testing';

import { SubsistenceFeesRunInfoViewService } from 'cca-api';

import { SentSubsistenceFeesComponent } from './sent-subsistence-fees.component';

describe('SentSubsistenceFeesComponent', () => {
  let component: SentSubsistenceFeesComponent;
  let fixture: ComponentFixture<SentSubsistenceFeesComponent>;
  let subsistenceFeesRunInfoViewService: Partial<jest.Mocked<SubsistenceFeesRunInfoViewService>>;

  beforeEach(async () => {
    subsistenceFeesRunInfoViewService = {
      getSubsistenceFeesRunDetailsById: jest.fn().mockReturnValue(of(mockSentSubsistenceFeesDetails)),
    };

    await TestBed.configureTestingModule({
      imports: [SentSubsistenceFeesComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ runId: 1 }) },
        { provide: SubsistenceFeesRunInfoViewService, useValue: subsistenceFeesRunInfoViewService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SentSubsistenceFeesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const detailsValues = getSummaryListData(fixture.nativeElement);

    expect(detailsValues).toEqual([
      [
        ['Payment request date', 'Payment status', 'Total (GBP)', 'Outstanding (GBP)'],
        ['01 Jan 2025', 'Awaiting payment', '900 (initially 1,000)', '599'],
      ],
    ]);
  });
});
