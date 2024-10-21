import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';
import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep } from '@requests/common';

import { mockUnaVariationRequestTaskPayload } from '../../../testing/mock-data';
import { FacilityItemUndoComponent } from './facility-item-undo.component';

describe('FacilityItemUndoComponent', () => {
  let component: FacilityItemUndoComponent;
  let fixture: ComponentFixture<FacilityItemUndoComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00002' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityItemUndoComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityItemUndoComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT' as any } });
    store.setPayload(mockUnaVariationRequestTaskPayload);

    fixture = TestBed.createComponent(FacilityItemUndoComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.heading.textContent.trim()).toEqual(
      'Are you sure you want to undo the exclusion of Facility 2 (ADS_1-F00002)?',
    );
  });

  it('should undo item and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(
      MANAGE_FACILITIES_SUBTASK,
      ManageFacilitiesWizardStep.UNDO_FACILITY,
      route,
      {
        facilityId: 'ADS_1-F00002',
      },
    );
  });
});
