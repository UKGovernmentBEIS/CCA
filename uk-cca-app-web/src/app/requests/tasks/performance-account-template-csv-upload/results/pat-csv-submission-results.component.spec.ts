import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';
import { Mocked } from 'vitest';

import { RequestTaskItemDTO, RequestTaskPayload, TasksService } from 'cca-api';

import { PatCsvSubmissionResultsComponent } from './pat-csv-submission-results.component';

describe('PatCsvSubmissionResultsComponent', () => {
  let component: PatCsvSubmissionResultsComponent;
  let fixture: ComponentFixture<PatCsvSubmissionResultsComponent>;
  let store: RequestTaskStore;
  let authStore: AuthStore;

  const tasksService: Partial<Mocked<TasksService>> = {
    processRequestTaskAction: vi.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatCsvSubmissionResultsComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setRequestTaskItem({
      requestTask: {
        id: 477,
        type: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT',
        payload: {
          payloadType: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD',
          sendEmailNotification: true,
          sectorAssociationInfo: {
            id: 17,
            acronym: 'ADS_17',
            name: 'Aerospace_17',
            competentAuthority: 'ENGLAND',
          },
          performanceAccountTemplateDataUpload: {
            targetYear: '2026',
            files: ['386a3ce0-06de-44df-b00c-6f9fe70e5b7f', 'c890e149-568a-4692-ad74-fd449a8e0412'],
          },
          processingStatus: 'COMPLETED',
          results: {
            totalFilesUploaded: 2,
            facilitiesSucceeded: 2,
            facilitiesFailed: 4,
            uploadSummaryFile: '4eb6c72d-1dc2-4d26-90db-153c3e2331af',
            submittedDate: '2027-01-01T00:00:00Z',
          },
          facilityReports: {
            '1': {
              facilityId: 31,
              facilityBusinessId: 'ADS_17-F00003',
              accountId: 28,
              savingActions: [
                {
                  actionCategoryType: 'ENERGY_MANAGEMENT',
                  supplyDemandSideMeasure: 'DEMAND_SIDE',
                  savingActionsImplemented: 'Created an energy management system',
                  reasonsForImplementation: 'It seemed like a good idea at the time',
                  implementationDate: '2026-01-01',
                  fixedEnergyConsumptionOrCarbonEmissionsImpacted: 'FIXED_AND_VARIABLE',
                  energyConsumptionOrCarbonEmissionsImpactedPercentage: '10.0',
                  expectedExtentOfChangeImplementedPercentage: '20.0',
                  expectedSavingsFromTheChangeImplementedPercentage: '-586.0',
                  estimatedChangeInEnergyConsumptionPercentage: '-11.72',
                },
              ],
              succeeded: true,
              errors: [],
            },
            '2': {
              facilityId: 32,
              facilityBusinessId: 'ADS_17-F00004',
              accountId: 28,
              savingActions: [
                {
                  actionCategoryType: 'PROCESS_OPTIMISATION',
                  supplyDemandSideMeasure: 'DEMAND_SIDE',
                  savingActionsImplemented: 'Process improvements',
                  reasonsForImplementation: 'Tweaked a few settings to make the process more efficient',
                  implementationDate: '2023-07-01',
                  fixedEnergyConsumptionOrCarbonEmissionsImpacted: 'FIXED_AND_VARIABLE',
                  energyConsumptionOrCarbonEmissionsImpactedPercentage: '10.0',
                  expectedExtentOfChangeImplementedPercentage: '20.0',
                  expectedSavingsFromTheChangeImplementedPercentage: '30.0',
                  estimatedChangeInEnergyConsumptionPercentage: '0.6',
                  notes: 'We could only implement this measure half way through TP6',
                },
              ],
              succeeded: false,
              errors: ['Validation error'],
            },
          },
          csvRowErrors: [
            {
              filename: 'PAT template V2.1 perc not decimal.csv',
              message:
                "Text 'Enter the approximate date that the measure was fully implemente...' could not be parsed, unparsed text found at index 0",
              facilityBusinessId:
                'Enter the facility ID of the site reporting each measure. \n\nThis must exactly match the facility identifier(s) in your underlying agreement to ensure the data can be uploaded',
              rowNumber: 1,
            },
            {
              filename: 'PAT template V2.1 perc not decimal.csv',
              message: "Text 'Implementation Date (DD/MM/YYYY)' could not be parsed, unparsed text found at index 0",
              facilityBusinessId: 'Facility Identifier',
              rowNumber: 2,
            },
            {
              filename: 'PAT template V2.1 perc not decimal.csv',
              message:
                '- Contradictory information submitted for facility - correct and resubmit | facilityBusinessId - must not be null | actionCategoryType - must not be null',
              rowNumber: 5,
            },
            {
              filename: 'ADS_1-TPRUL-3_Summary.csv',
              message: 'Row must contain exactly 11 columns',
              rowNumber: 1,
            },
          ],
          uploadAttachments: {
            '386a3ce0-06de-44df-b00c-6f9fe70e5b7f': 'ADS_1-TPRUL-3_Summary.csv',
            '4eb6c72d-1dc2-4d26-90db-153c3e2331af': 'Upload_Summary.csv',
            'c890e149-568a-4692-ad74-fd449a8e0412': 'PAT template V2.1 perc not decimal.csv',
          },
        },
        assignable: true,
        assigneeUserId: '60a3163b-3775-433a-a3d7-96496779a6f6',
        assigneeFullName: 'sec-adm1 user',
        startDate: '2026-08-27T11:41:15.42351Z',
      },
      userAssignCapable: true,
    } as RequestTaskItemDTO);
    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ userId: '60a3163b-3775-433a-a3d7-96496779a6f6', roleType: 'SECTOR_USER' });

    fixture = TestBed.createComponent(PatCsvSubmissionResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct summary data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);
    const [keys, values] = summaryValues[0];

    expect(keys).toEqual([
      'Time submitted',
      'Files uploaded',
      'Facilities successful',
      'Facilities failed',
      'Submission summary file',
    ]);

    expect(values[0]).toContain('1 Jan 2027');
    expect(values[1]).toBe('2');
    expect(values[2]).toBe('2');
    expect(values[3]).toBe('4');
    expect(values[4]).toContain('Upload_Summary.csv');
  });

  it('should display an error message when errorMessage exists', async () => {
    store.setPayload({
      errorMessage: 'SUBMISSION_RESULTS_CSV_FAILED',
    } as RequestTaskPayload);

    fixture.detectChanges();

    const errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).not.toBeNull();
    expect(errorSummary.nativeElement.textContent).toContain('There is a problem');
    expect(errorSummary.nativeElement.textContent).toContain('Files have been uploaded');
  });

  it('should render the Finish task button', () => {
    const finishButton = fixture.debugElement.query(By.css('button'));
    expect(finishButton).not.toBeNull();
    expect(finishButton.nativeElement.textContent.trim()).toBe('Finish task');
  });

  it('should render the warning message', () => {
    const warning = fixture.debugElement.query(By.css('.govuk-warning-text'));
    expect(warning).not.toBeNull();
    expect(warning.nativeElement.textContent).toContain("You need to click on 'Finish task' to finalize your task.");
  });
});
