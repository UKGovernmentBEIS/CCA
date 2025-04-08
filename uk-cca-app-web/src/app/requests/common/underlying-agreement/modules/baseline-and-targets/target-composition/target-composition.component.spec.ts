import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';
import { produce } from 'immer';

import { mockRequestTaskItemDTOABSOLUTE, mockUnaRequestTaskPayloadABSOLUTE } from '../../../testing';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '../../../underlying-agreement.types';
import { TargetCompositionComponent } from './target-composition.component';

describe('TargetCompositionComponent', () => {
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  let store: RequestTaskStore;
  let container: Element;

  beforeEach(async () => {
    const renderResult = await render(TargetCompositionComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS },
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for underlying agreement' },
        { provide: TaskService, useValue: unaTaskService },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemDTOABSOLUTE);

        const payload = produce(mockUnaRequestTaskPayloadABSOLUTE, (p) => {
          delete p.underlyingAgreement.targetPeriod6Details.baselineData;
          delete p.underlyingAgreement.targetPeriod6Details.targets;
          delete p.underlyingAgreement.targetPeriod6Details.targetComposition;
        });

        store.setPayload(payload);
      },
    });

    container = renderResult.container;
  });

  it('should correctly render fields', () => {
    expect(container).toMatchSnapshot('initial-fields');
  });

  it('should not render throughput if NOVEM is selected', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Novem'));
    expect(container).toMatchSnapshot('novem-selected');
  });

  it('should render throughput if ABSOLUTE or RELATIVE is selected', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Absolute'));
    expect(container).toMatchSnapshot('agreementCompositionType selected');
  });

  it('should not render measure fields isMeasured is false', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Absolute'));
    await user.selectOptions(document.getElementById('measurementType'), '0: ENERGY_KWH');
    await user.click(screen.getByText('No'));
    expect(container).toMatchSnapshot('is Measured false');
  });

  it('should render measure fields isMeasured is true', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Absolute'));
    await user.click(screen.getByText('Yes'));
    expect(container).toMatchSnapshot('is Measured true');
  });
});
