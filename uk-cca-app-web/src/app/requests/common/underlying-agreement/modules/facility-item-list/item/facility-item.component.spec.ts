import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskItemDTO } from './../../../testing';
import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep } from './../../../underlying-agreement.types';
import { FacilityItemComponent } from './facility-item.component';

describe('FacilityItemComponent', () => {
  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    const { fixture } = await render(FacilityItemComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaTaskService },
      ],
      configureTestBed: (testbed) => {
        const store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemDTO);
      },
    });
    fixture.detectChanges();
  });

  it('should show form values', () => {
    expect(screen.getByLabelText('Site name')).toHaveValue('Facility 1');
  });

  it('should change item and save', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Save and continue'));
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    expect(taskServiceSpy).toHaveBeenCalledWith(
      MANAGE_FACILITIES_SUBTASK,
      ManageFacilitiesWizardStep.EDIT_FACILITY,
      route,
      {
        name: 'Facility 1',
        facilityId: 'ADS_1-F00001',
        status: 'NEW',
      },
    );
  });
});
