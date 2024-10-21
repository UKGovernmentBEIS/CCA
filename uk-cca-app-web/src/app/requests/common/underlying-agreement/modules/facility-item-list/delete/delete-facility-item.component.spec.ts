import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep } from '../../../underlying-agreement.types';
import { mockUnaRequestTaskPayload } from './../../../testing';
import { DeleteFacilityItemComponent } from './delete-facility-item.component';

describe('DeleteFacilityItemComponent', () => {
  let component: DeleteFacilityItemComponent;
  let fixture: ComponentFixture<DeleteFacilityItemComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<DeleteFacilityItemComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DeleteFacilityItemComponent],
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
    store.setPayload(mockUnaRequestTaskPayload);

    fixture = TestBed.createComponent(DeleteFacilityItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to delete the Facility 1?');
  });

  it('should delete item and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(
      MANAGE_FACILITIES_SUBTASK,
      ManageFacilitiesWizardStep.DELETE_FACILITY,
      route,
      {
        name: 'Facility 1',
        facilityId: 'ADS_1-F00001',
        status: 'NEW',
      },
    );
  });
});
