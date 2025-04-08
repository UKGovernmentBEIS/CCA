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
import { FacilityExtentComponent } from './facility-extent.component';

describe('FacilityExtentComponent', () => {
  let component: FacilityExtentComponent;
  let fixture: ComponentFixture<FacilityExtentComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityExtentComponent> {
    get areActivitiesClaimedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="areActivitiesClaimed"]');
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.cca-multi-file-upload__message');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityExtentComponent],
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

    fixture = TestBed.createComponent(FacilityExtentComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.areActivitiesClaimedRadios[0].checked).toBeTruthy();
    expect(page.areActivitiesClaimedRadios[1].checked).toBeFalsy();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'manufacturingProcessFile.xlsx',
      'processFlowFile.xlsx',
      'annotatedSitePlansFile.xlsx',
      'eligibleProcessFile.xlsx',
      'activitiesDescriptionFile.xlsx',
    ]);
  });

  it('should edit facility extend and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.areActivitiesClaimedRadios[1].click();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(FACILITIES_SUBTASK, FacilityWizardStep.EXTENT, route, {
      facilityId: 'ADS_1-F00001',
      facilityExtent: {
        manufacturingProcessFile: 'manufacturingProcessFile',
        processFlowFile: 'processFlowFile',
        annotatedSitePlansFile: 'annotatedSitePlansFile',
        eligibleProcessFile: 'eligibleProcessFile',
        areActivitiesClaimed: false,
      },
    });
  });
});
