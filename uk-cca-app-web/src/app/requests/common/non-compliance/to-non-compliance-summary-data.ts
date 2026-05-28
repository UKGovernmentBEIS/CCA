import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';

import { NonComplianceDetails } from 'cca-api';

import { boolToString } from '../utils';
import { formatRelevantFacility, formatRelevantWorkflow } from './format-relevant-items';
import { NON_COMPLIANCE_TYPE_LABELS } from './non-compliance-type-labels';

export function toNonComplianceSummaryData(
  details: NonComplianceDetails,
  allRelevantWorkflows: Record<string, string>,
  allRelevantFacilities: Record<string, string>,
  isEditable: boolean,
): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const factory = new SummaryFactory();

  const nonComplianceTypeLabel = details?.nonComplianceType
    ? NON_COMPLIANCE_TYPE_LABELS[details.nonComplianceType]
    : '';

  const relevantWorkflows = (details?.relevantWorkflows ?? []).map((workflowId) =>
    formatRelevantWorkflow(workflowId, allRelevantWorkflows),
  );

  const relevantFacilities = (details?.relevantFacilities ?? []).map((facility) =>
    formatRelevantFacility(facility, allRelevantFacilities),
  );

  factory
    .addSection('Non-compliance details')
    .addRow('Type of non-compliance', nonComplianceTypeLabel, {
      change: isEditable,
      changeLink: '../provide-details',
    })
    .addRow('When did the operator become non-compliant?', formatDate(details?.nonCompliantDate, datePipe), {
      change: isEditable,
      changeLink: '../provide-details',
    })
    .addRow('When did the operator become compliant?', formatDate(details?.compliantDate, datePipe), {
      change: isEditable,
      changeLink: '../provide-details',
    })
    .addTextAreaRow('Comments', details?.comment ?? '', {
      change: isEditable,
      changeLink: '../provide-details',
    })

    .addSection('Relevant items of non-compliance')
    .addRow('Relevant workflows', relevantWorkflows, {
      change: isEditable,
      changeLink: '../choose-relevant-workflows',
    })
    .addRow('Relevant facilities', relevantFacilities, {
      change: isEditable,
      changeLink: '../choose-relevant-facilities',
    })

    .addSection('Enforcement details')
    .addRow(
      'Will you be issuing an Enforcement Response Notice?',
      boolToString(details?.isEnforcementResponseNoticeRequired),
      {
        change: isEditable,
        changeLink: '../issue-enforcement',
      },
    );

  if (details?.isEnforcementResponseNoticeRequired === false) {
    factory.addTextAreaRow(
      'Explain why you will not be issuing an Enforcement Response Notice',
      details?.explanation ?? '',
      {
        change: isEditable,
        changeLink: '../issue-enforcement',
      },
    );
  }

  return factory.create();
}

function formatDate(date: string | null | undefined, datePipe: DatePipe): string {
  if (!date) {
    return '';
  }

  return datePipe.transform(date, 'dd MMM yyyy') ?? '';
}
