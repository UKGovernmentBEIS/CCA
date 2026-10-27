import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';

import { FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload } from 'cca-api';

import { FacilityPATReportingSubmittedDetailsComponent } from './facility-pat-reporting-submitted-details.component';

describe('FacilityPATReportingSubmittedDetailsComponent', () => {
  let component: FacilityPATReportingSubmittedDetailsComponent;
  let fixture: ComponentFixture<FacilityPATReportingSubmittedDetailsComponent>;
  let actionStore: RequestActionStore;

  const route = { snapshot: { params: { actionId: '0' } } };

  const state = {
    savingActions: [
      {
        actionCategoryType: 'ENERGY_MANAGEMENT',
        supplyDemandSideMeasure: 'DEMAND_SIDE',
        savingActionsImplemented: 'Created an energy management system',
        reasonsForImplementation: 'It seemed like a good idea at the time',
        implementationDate: '2026-01-01',
        fixedEnergyConsumptionOrCarbonEmissionsImpacted: 'FIXED_AND_VARIABLE',
        energyConsumptionOrCarbonEmissionsImpactedPercentage: '10.0000000',
        expectedExtentOfChangeImplementedPercentage: '20.0000000',
        expectedSavingsFromTheChangeImplementedPercentage: '-586.0000000',
        estimatedChangeInEnergyConsumptionPercentage: '-11.7200000',
      },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityPATReportingSubmittedDetailsComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState({
      action: {
        payload: {
          performanceData: state,
          targetPeriodYear: 2026,
        } as FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(FacilityPATReportingSubmittedDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
