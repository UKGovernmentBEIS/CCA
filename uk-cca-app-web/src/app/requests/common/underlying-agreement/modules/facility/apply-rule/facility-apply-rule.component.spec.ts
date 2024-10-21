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
import { FacilityApplyRuleComponent } from './facility-apply-rule.component';

describe('FacilityApplyRuleComponent', () => {
  let component: FacilityApplyRuleComponent;
  let fixture: ComponentFixture<FacilityApplyRuleComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityApplyRuleComponent> {
    get energyConsumed() {
      return this.getInputValue('#energyConsumed');
    }
    set energyConsumed(value: number) {
      this.setInputValue('#energyConsumed', value);
    }
    get energyConsumedProvision() {
      return this.getInputValue('#energyConsumedProvision');
    }
    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityApplyRuleComponent],
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

    fixture = TestBed.createComponent(FacilityApplyRuleComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.energyConsumed).toEqual('50');
    expect(page.energyConsumedProvision).toEqual('40');
    expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['evidenceFile.xlsx']);
  });

  it('should edit facility extend and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.energyConsumed = 90;
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(FACILITIES_SUBTASK, FacilityWizardStep.APPLY_RULE, route, {
      facility: {
        facilityId: 'ADS_1-F00001',
        apply70Rule: {
          energyConsumed: 90,
          energyConsumedEligible: 100,
          evidenceFile: 'evidenceFile',
        },
      },
      attachments: {
        evidenceFile: 'evidenceFile.xlsx',
      },
    });
  });
});
