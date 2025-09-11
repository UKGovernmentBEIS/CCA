import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { mockSentSubsistenceFeesDetails, mockTargetUnitMoas } from '@shared/components';

import { SubsistenceFeesRunInfoViewService } from 'cca-api';

import { TuMoasComponent } from './tu-moas.component';

describe('TuMoasComponent', () => {
  let component: TuMoasComponent;
  let fixture: ComponentFixture<TuMoasComponent>;
  let subsistenceFeesRunInfoViewService: Partial<jest.Mocked<SubsistenceFeesRunInfoViewService>>;

  beforeEach(async () => {
    subsistenceFeesRunInfoViewService = {
      getSubsistenceFeesRunDetailsById: jest.fn().mockReturnValue(of(mockSentSubsistenceFeesDetails)),
      getSubsistenceFeesRunMoas: jest
        .fn()
        .mockReturnValue(of({ subsistenceFeesMoas: mockTargetUnitMoas, total: mockTargetUnitMoas.length })),
    };

    await TestBed.configureTestingModule({
      imports: [TuMoasComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: SubsistenceFeesRunInfoViewService, useValue: subsistenceFeesRunInfoViewService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TuMoasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate with correct number of table rows', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().totalItems + 1);
  });
});
