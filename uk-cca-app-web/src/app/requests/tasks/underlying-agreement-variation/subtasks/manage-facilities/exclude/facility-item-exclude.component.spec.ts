import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep } from '@requests/common';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskItemDTO, mockUnderlyingAgreementVariation } from '../../../testing/mock-data';
import { FacilityItemExcludeComponent } from './facility-item-exclude.component';

describe('FacilityItemExcludeComponent', () => {
  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00002' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    const { fixture } = await render(FacilityItemExcludeComponent, {
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
    expect(screen.getByText('Exclude Facility 2 facility')).toBeInTheDocument();

    expect(screen.getByLabelText('Day')).toHaveValue('29');
    expect(screen.getByLabelText('Month')).toHaveValue('3');
    expect(screen.getByLabelText('Year')).toHaveValue('2021');
  });

  it('should change item and save', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Save and continue'));
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    expect(taskServiceSpy).toHaveBeenCalledWith(
      MANAGE_FACILITIES_SUBTASK,
      ManageFacilitiesWizardStep.EXCLUDE_FACILITY,
      route,
      {
        excludedDate: new Date(mockUnderlyingAgreementVariation.facilities[1].excludedDate),
        facilityId: 'ADS_1-F00002',
      },
    );
  });
});
