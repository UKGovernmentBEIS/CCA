import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import { FacilityEligibilityDetailsComponent } from './facility-eligibility-details.component';

describe('FacilityContactDetailsComponent', () => {
  let component: FacilityEligibilityDetailsComponent;
  let fixture: ComponentFixture<FacilityEligibilityDetailsComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityEligibilityDetailsComponent> {
    get isConnectedToExistingFacilityRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="isConnectedToExistingFacility"]');
    }
    get agreementTypeSelect(): string {
      return this.getInputValue('#agreementType');
    }
    get erpAuthorisationExistsRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="erpAuthorisationExists"]');
    }
    get regulatorNameSelect(): string {
      return this.getInputValue('#regulatorName');
    }
    get authorisationNumber() {
      return this.getInputValue('#authorisationNumber');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityEligibilityDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(FacilityEligibilityDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.isConnectedToExistingFacilityRadios[0].checked).toBeTruthy();
    expect(page.isConnectedToExistingFacilityRadios[1].checked).toBeFalsy();

    expect(page.agreementTypeSelect).toEqual('ENVIRONMENTAL_PERMITTING_REGULATIONS');

    expect(page.erpAuthorisationExistsRadios[0].checked).toBeTruthy();
    expect(page.erpAuthorisationExistsRadios[1].checked).toBeFalsy();

    expect(page.authorisationNumber).toEqual('authorisation');
    expect(page.regulatorNameSelect).toEqual('ENVIRONMENT_AGENCY');
  });

  it('should edit eligibility details and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.isConnectedToExistingFacilityRadios[1].click();
    page.erpAuthorisationExistsRadios[1].click();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(FACILITIES_SUBTASK, FacilityWizardStep.ELIGIBILITY_DETAILS, route, {
      facility: {
        facilityId: 'ADS_1-F00001',
        eligibilityDetailsAndAuthorisation: {
          isConnectedToExistingFacility: false,
          agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
          erpAuthorisationExists: false,
        },
      },
      attachments: {},
    });
  });
});
