import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO, mockTargetUnitDetails } from '../../../testing';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import { FacilityDetailsComponent } from './facility-details.component';

describe('FacilityDetailsComponent', () => {
  let component: FacilityDetailsComponent;
  let fixture: ComponentFixture<FacilityDetailsComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route = {
    snapshot: {
      params: { facilityId: 'ADS_1-F00001' },
      pathFromRoot: [],
    },
  };

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityDetailsComponent> {
    get isCoveredByUketsRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="isCoveredByUkets"]');
    }
    get applicationReasonRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="applicationReason"]');
    }
    get sameAddress() {
      return this.query<HTMLInputElement>('input[name$="sameAddress"]');
    }

    get line1() {
      return this.getInputValue('#facilityAddress.line1');
    }
    get line2() {
      return this.getInputValue('#facilityAddress.line2');
    }
    get city() {
      return this.getInputValue('#facilityAddress.city');
    }
    get county() {
      return this.getInputValue('#facilityAddress.county');
    }
    get postcode() {
      return this.getInputValue('#facilityAddress.postcode');
    }
    get country() {
      return this.getInputValue('#facilityAddress.country');
    }

    set uketsId(value: string) {
      this.setInputValue('#uketsId', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }
    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityDetailsComponent],
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

    fixture = TestBed.createComponent(FacilityDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.isCoveredByUketsRadios[0].checked).toBeFalsy();
    expect(page.isCoveredByUketsRadios[1].checked).toBeTruthy();

    expect(page.applicationReasonRadios[0].checked).toBeTruthy();
    expect(page.applicationReasonRadios[1].checked).toBeFalsy();

    expect(page.sameAddress.checked).toBeFalsy();
    expect(page.line1).toEqual('Facility Line1');
    expect(page.line2).toEqual('Facility Line2');
    expect(page.city).toEqual('Facility City');
    expect(page.county).toEqual('');
    expect(page.postcode).toEqual('Facility 14');
    expect(page.country).toEqual('GR');
  });

  it('should edit details and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.sameAddress.click();
    page.isCoveredByUketsRadios[0].click();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'UK ETS installation identifier cannot be blank',
    ]);

    page.uketsId = 'uk identifier';
    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(FACILITIES_SUBTASK, FacilityWizardStep.DETAILS, route, {
      facilityId: 'ADS_1-F00001',
      facilityDetails: {
        name: 'Facility 1',
        facilityAddress: mockTargetUnitDetails.address,
        uketsId: 'uk identifier',
        isCoveredByUkets: true,
        applicationReason: 'NEW_AGREEMENT',
        previousFacilityId: null,
      },
    });
  });
});
