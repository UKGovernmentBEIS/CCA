import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  TaskItemStatus,
  TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
} from '@requests/common';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { tprFormQuery } from './target-period-reporting-form.selectors';

const tprFormRoutePrefix = 'target-period-reporting-form';

export const targetPeriodReportingFormTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(tprFormQuery.selectPayload)();

  return {
    header: 'Target period reporting (TPR) form',
    sections: getAllTPRFormSections(payload),
  };
};

export function getAllTPRFormSections(
  payload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
): TaskSection[] {
  return [
    {
      title: 'Energy/fuel details',
      tasks: [
        {
          status: payload?.sectionsCompleted?.[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          linkText: 'Provide energy/fuel amount consumed',
          link: `${tprFormRoutePrefix}/energy-fuel-amount`,
        },
      ],
    },
    {
      title: 'Throughput details',
      tasks: [
        {
          status: calculateThroughputDetailsTaskStatus(payload),
          linkText: 'Provide target period throughput details',
          link:
            calculateThroughputDetailsTaskStatus(payload) === TaskItemStatus.CANNOT_START_YET
              ? ''
              : `${tprFormRoutePrefix}/throughput`,
        },
      ],
    },
    {
      title: 'Submit TPR',
      tasks: [
        {
          status: canSubmit(payload) ? TaskItemStatus.NOT_STARTED : TaskItemStatus.CANNOT_START_YET,
          linkText: 'Confirm results and submit',
          link: canSubmit(payload) ? `${tprFormRoutePrefix}/submit` : '',
        },
      ],
    },
  ];
}

function calculateThroughputDetailsTaskStatus(
  payload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
): TaskItemStatus {
  if (
    !payload.sectionsCompleted?.[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK] ||
    payload.sectionsCompleted?.[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK] !== TaskItemStatus.COMPLETED
  ) {
    return TaskItemStatus.CANNOT_START_YET;
  }

  return (
    (payload.sectionsCompleted?.[TPR_FORM_THROUGHPUT_DETAILS_SUBTASK] as TaskItemStatus) ?? TaskItemStatus.NOT_STARTED
  );
}

function canSubmit(payload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload): boolean {
  return (
    calculateThroughputDetailsTaskStatus(payload) === TaskItemStatus.COMPLETED &&
    payload?.sectionsCompleted?.[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED
  );
}
