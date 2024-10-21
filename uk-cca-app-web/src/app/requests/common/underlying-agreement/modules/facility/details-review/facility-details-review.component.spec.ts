import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { mockUNAReviewRequestTaskState } from '../../../testing';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import { FacilityDetailsReviewComponent } from './facility-details-review.component';

describe('FacilityDetailsReviewComponent', () => {
  let fixture: ComponentFixture<FacilityDetailsReviewComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_53-F00007' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityDetailsReviewComponent> {
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
      imports: [FacilityDetailsReviewComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);

    fixture = TestBed.createComponent(FacilityDetailsReviewComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture).toMatchSnapshot();
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
      facility: {
        facilityId: 'ADS_53-F00007',
        facilityDetails: {
          facilityAddress: {
            city: 'Addres1',
            country: 'GB',
            county: null,
            line1: 'Address',
            line2: 'Apartment 1',
            postcode: '94043',
          },
          uketsId: 'uk identifier',
          isCoveredByUkets: true,
          applicationReason: 'NEW_AGREEMENT',
          name: 'Facility 2',
          previousFacilityId: undefined,
        },
      },
    });
  });
});
