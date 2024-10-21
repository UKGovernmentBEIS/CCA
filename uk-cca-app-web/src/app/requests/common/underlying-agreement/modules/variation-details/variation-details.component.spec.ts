import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { VARIATION_DETAILS_SUBTASK, VariationDetailsWizardStep } from '../../underlying-agreement.types';
import { mockVariationRequestTaskItemDTO } from './../../testing';
import { VariationDetailsComponent } from './variation-details.component';

describe('VariationDetailsComponent', () => {
  let component: VariationDetailsComponent;
  let fixture: ComponentFixture<VariationDetailsComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<VariationDetailsComponent> {
    get facilityChanges() {
      return this.queryAll<HTMLInputElement>('input[name$="facilityChanges"]');
    }
    get baselineChanges() {
      return this.queryAll<HTMLInputElement>('input[name$="baselineChanges"]');
    }
    get targetCurrencyChanges() {
      return this.queryAll<HTMLInputElement>('input[name$="targetCurrencyChanges"]');
    }
    get otherChanges() {
      return this.queryAll<HTMLInputElement>('input[name$="otherChanges"]');
    }
    get reason() {
      return this.getInputValue('#reason');
    }
    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VariationDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockVariationRequestTaskItemDTO);

    fixture = TestBed.createComponent(VariationDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.facilityChanges.length).toEqual(7);
    expect(page.facilityChanges[0].checked).toBeFalsy();
    expect(page.facilityChanges[1].checked).toBeFalsy();
    expect(page.facilityChanges[2].checked).toBeFalsy();
    expect(page.facilityChanges[3].checked).toBeFalsy();
    expect(page.facilityChanges[4].checked).toBeFalsy();
    expect(page.facilityChanges[5].checked).toBeFalsy();
    expect(page.facilityChanges[6].checked).toBeTruthy();

    expect(page.baselineChanges.length).toEqual(7);
    expect(page.baselineChanges[0].checked).toBeFalsy();
    expect(page.baselineChanges[1].checked).toBeFalsy();
    expect(page.baselineChanges[2].checked).toBeFalsy();
    expect(page.baselineChanges[3].checked).toBeFalsy();
    expect(page.baselineChanges[4].checked).toBeFalsy();
    expect(page.baselineChanges[5].checked).toBeFalsy();
    expect(page.baselineChanges[6].checked).toBeFalsy();

    expect(page.targetCurrencyChanges.length).toEqual(2);
    expect(page.targetCurrencyChanges[0].checked).toBeFalsy();
    expect(page.targetCurrencyChanges[1].checked).toBeTruthy();

    expect(page.otherChanges.length).toEqual(1);
    expect(page.otherChanges[0].checked).toBeFalsy();

    expect(page.reason).toEqual('Variation reason');
  });

  it('should edit details and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.facilityChanges[0].click();
    page.facilityChanges[1].click();
    page.otherChanges[0].click();
    page.reason = 'Variation reason changed';
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(VARIATION_DETAILS_SUBTASK, VariationDetailsWizardStep.DETAILS, route, {
      modifications: [
        'AMEND_OPERATOR_OR_ORGANISATION_NAME',
        'AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS',
        'AMEND_70_PERCENT_RULE_EVALUATION',
        'CHANGE_THROUGHPUT_UNIT',
        'ANY_CHANGES_NOT_COVERED',
      ],
      reason: 'Variation reason changed',
    });
  });
});
