import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState, TARGET_UNIT_DETAILS_SUBMIT_FORM, TasksApiService } from '@requests/common';

import { SectorAssociationSchemeService } from 'cca-api';

import { TargetUnitDetailsComponent } from './target-unit-details.component';

const mockState = {
  ...mockRequestTaskState,
  payload: {
    underlyingAgreement: {
      underlyingAgreementTargetUnitDetails: {
        operatorName: 'Test Operator',
        operatorType: 'LIMITED_COMPANY',
        isCompanyRegistrationNumber: true,
        companyRegistrationNumber: '12345678',
        subsectorAssociationId: 1,
      },
    },
  },
  requestTaskId: 123,
  sectionsCompleted: {},
};

const mockForm = new FormGroup({
  operatorName: new FormControl('Test Operator'),
  operatorType: new FormControl('LIMITED_COMPANY'),
  isCompanyRegistrationNumber: new FormControl(true),
  companyRegistrationNumber: new FormControl('12345678'),
  registrationNumberMissingReason: new FormControl(null),
  subsectorAssociationId: new FormControl(1),
});

describe('TargetUnitDetailsComponent', () => {
  let component: TargetUnitDetailsComponent;
  let fixture: ComponentFixture<TargetUnitDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;

  const route = new ActivatedRouteStub();

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const mockSectorService = {
    getSectorAssociationSchemeBySectorAssociationId: jest.fn().mockReturnValue(
      of({
        subsectorAssociationSchemes: [{ id: 1, subsectorAssociation: { name: 'Test Subsector' } }],
      }),
    ),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetUnitDetailsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: SectorAssociationSchemeService, useValue: mockSectorService },
        { provide: TARGET_UNIT_DETAILS_SUBMIT_FORM, useValue: mockForm },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    tasksApiService = TestBed.inject(TasksApiService);

    store.setState(mockState);

    fixture = TestBed.createComponent(TargetUnitDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
    fixture.destroy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain('Target unit details');
  });

  it('should submit form and navigate to check-your-answers', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');
    const navigateSpy = jest.spyOn(router, 'navigate');

    const continueButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    continueButton.nativeElement.click();

    component.onSubmit();

    expect(onSubmitSpy).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should update form and submit with new values', () => {
    mockForm.patchValue({
      operatorName: 'Updated Operator',
      operatorType: 'PARTNERSHIP',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
  });
});
