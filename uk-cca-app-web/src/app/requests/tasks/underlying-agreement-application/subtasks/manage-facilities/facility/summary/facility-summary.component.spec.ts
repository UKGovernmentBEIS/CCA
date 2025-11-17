import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import FacilitySummaryComponent from './facility-summary.component';

describe('FacilitySummaryComponent', () => {
  let component: FacilitySummaryComponent;
  let fixture: ComponentFixture<FacilitySummaryComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  const facilityId = 'ADS_1-F00001';
  const taskId = '123';

  beforeEach(async () => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [FacilitySummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { facilityId, taskId } }, relativeTo: {} },
        },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 20,
          type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
            accountReferenceData: {
              targetUnitAccountDetails: {},
              sectorAssociationDetails: {
                subsectorAssociationName: 'SUBSECTOR_2',
                schemeDataMap: {
                  ['CCA_3']: { sectorMeasurementType: 'ENERGY_KWH', sectorThroughputUnit: 'kWh' },
                },
              },
            },
            underlyingAgreement: {
              facilities: [
                {
                  facilityId,
                  facilityDetails: {
                    name: 'Facility 1',
                    isCoveredByUkets: false,
                    applicationReason: 'NEW_AGREEMENT',
                    participatingSchemeVersions: ['CCA_3'],
                    facilityAddress: {
                      line1: 'Facility Line1',
                      line2: 'Facility Line2',
                      city: 'Facility City',
                      postcode: 'Facility 14',
                      country: 'GR',
                    },
                  },
                  facilityContact: {
                    firstName: 'FacilityFirst',
                    lastName: 'FacilityLast',
                    email: 'facility@email.com',
                    address: {
                      line1: 'Facility Contact Line1',
                      line2: 'Facility Contact Line2',
                      city: 'Facility Contact City',
                      postcode: 'Facility Contact 14',
                      country: 'GR',
                    },
                    phoneNumber: {
                      countryCode: '44',
                      number: '1234567890',
                    },
                  },
                  eligibilityDetailsAndAuthorisation: {
                    isConnectedToExistingFacility: true,
                    connectedFacilityId: 'ADS_1-F11111',
                    agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
                    erpAuthorisationExists: true,
                    erpAuthorisationNumber: 'authorisation',
                    regulatorName: 'ENVIRONMENT_AGENCY',
                  },
                  facilityExtent: {
                    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
                    processFlowFile: 'processFlowFile.xlsx',
                    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
                    eligibleProcessFile: 'eligibleProcessFile.xlsx',
                    areActivitiesClaimed: true,
                    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
                  },
                  apply70Rule: {
                    energyConsumed: 50,
                    energyConsumedEligible: 70,
                    threeSeventhsProvision: 40,
                    evidenceFile: 'evidenceFile.xlsx',
                  },
                  cca3BaselineAndTargets: {
                    targetComposition: {
                      calculatorFile: '8b68ec7b-1d1a-48a7-bffc-c3b0d4f25972',
                      measurementType: 'ENERGY_KWH',
                      agreementCompositionType: 'NOVEM',
                    },
                    baselineData: {
                      isTwelveMonths: false,
                      baselineDate: '2022-01-01',
                      explanation: 'asdsadsadda',
                      greenfieldEvidences: ['ea4953cc-2323-4080-9123-feaa78cd6114'],
                      energy: '70',
                      usedReportingMechanism: true,
                      energyCarbonFactor: '90',
                    },
                    facilityBaselineEnergyConsumption: {
                      totalFixedEnergy: '100',
                      hasVariableEnergy: true,
                      variableEnergyType: 'TOTALS',
                      baselineVariableEnergy: '200',
                      totalThroughput: '50',
                      throughputUnit: 'tonnes',
                      variableEnergyConsumptionDataByProduct: [],
                    },
                    facilityTargets: {
                      improvements: {
                        TP7: '25',
                        TP8: '35',
                        TP9: '45',
                      },
                    },
                  },
                },
              ],
            },
            sectionsCompleted: {
              underlyingAgreementTargetUnitDetails: 'IN_PROGRESS',
            },
            underlyingAgreementAttachments: {
              manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
              processFlowFile: 'processFlowFile.xlsx',
              annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
              eligibleProcessFile: 'eligibleProcessFile.xlsx',
              activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
              evidenceFile: 'evidenceFile.xlsx',
            },
          } as any,
          assignable: true,
          assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
          assigneeFullName: 'sector user',
          startDate: '2024-08-05T15:47:22.695292Z',
        },
      },
      relatedTasks: [],
      timeline: [],
      taskReassignedTo: 'abc',
      isEditable: true,
    });

    fixture = TestBed.createComponent(FacilitySummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display facility details when data is available', () => {
    expect(fixture).toMatchSnapshot();
  });
});
