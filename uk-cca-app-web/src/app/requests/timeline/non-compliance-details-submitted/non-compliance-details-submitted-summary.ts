import { DatePipe } from '@angular/common';

import { formatRelevantFacility, formatRelevantWorkflow, NON_COMPLIANCE_TYPE_LABELS } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';

import { NonComplianceDetails } from 'cca-api';

export function toNonComplianceDetailsSubmittedSummaryData(
  details: NonComplianceDetails,
  allRelevantWorkflows: Record<string, string> = {},
  allRelevantFacilities: Record<string, string> = {},
): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const summary = new SummaryFactory();
  const relevantWorkflows = details.relevantWorkflows?.map((workflowId) =>
    formatRelevantWorkflow(workflowId, allRelevantWorkflows),
  );
  const relevantFacilities = details.relevantFacilities?.map((facility) =>
    formatRelevantFacility(facility, allRelevantFacilities),
  );

  summary
    .addSection('Details')
    .addRow('Type of non-compliance', NON_COMPLIANCE_TYPE_LABELS[details.nonComplianceType])
    .addRow('When did the operator become non-compliant?', datePipe.transform(details.nonCompliantDate, 'd MMM yyyy'))
    .addRow('When did the operator become compliant?', datePipe.transform(details.compliantDate, 'd MMM yyyy'))
    .addRow('Comments', details.comment);

  summary.addSection('Relevant items of non-compliance');

  if (relevantWorkflows?.length) {
    summary.addRow('Relevant tasks or workflows', relevantWorkflows);
  }

  if (relevantFacilities?.length) {
    summary.addRow('Relevant facilities', relevantFacilities);
  }

  summary
    .addSection('Enforcement response details')
    .addRow(
      'Will you be issuing an Enforcement Response Notice?',
      details.isEnforcementResponseNoticeRequired ? 'Yes' : 'No',
    );

  if (!details.isEnforcementResponseNoticeRequired && details.explanation) {
    summary.addRow('Why you will not be issuing an Enforcement Response Notice', details.explanation);
  }

  return summary.create();
}
