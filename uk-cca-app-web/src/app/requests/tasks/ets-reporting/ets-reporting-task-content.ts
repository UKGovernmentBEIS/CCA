import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, EtsReportingTaskPayload } from './ets-reporting.types';
import { canSubmitEtsReporting } from './ets-reporting.validation';

export { ETS_ENERGY_FUEL_DETAILS_SUBTASK } from './ets-reporting.types';

export const etsReportingTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as EtsReportingTaskPayload;

  return {
    header: 'UK emissions trading scheme (UK ETS) reporting - Submit form',
    sections: getEtsReportingSections(payload),
  };
};

export function getEtsReportingSections(payload: EtsReportingTaskPayload): TaskSection[] {
  const energyFuelStatus = payload?.sectionsCompleted?.[ETS_ENERGY_FUEL_DETAILS_SUBTASK] ?? TaskItemStatus.NOT_STARTED;
  const canSubmit = canSubmitEtsReporting(payload);

  return [
    {
      title: 'Energy/fuel details',
      tasks: [
        {
          status: energyFuelStatus,
          linkText: 'Provide UK ETS fuel amount consumed',
          link: 'ets-reporting/energy-fuel-amount',
        },
      ],
    },
    {
      title: 'Submit report',
      tasks: [
        {
          status: canSubmit ? TaskItemStatus.NOT_STARTED : TaskItemStatus.CANNOT_START_YET,
          linkText: 'Submit your UK ETS report',
          link: canSubmit ? 'ets-reporting/submit' : '',
        },
      ],
    },
  ];
}
