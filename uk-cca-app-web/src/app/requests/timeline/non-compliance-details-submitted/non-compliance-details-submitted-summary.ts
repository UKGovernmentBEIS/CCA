import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';

import { NonComplianceDetails } from 'cca-api';

import { NON_COMPLIANCE_TYPE_LABELS } from '../../tasks/non-compliance-details/non-compliance-type-labels';

export function toNonComplianceDetailsSubmittedSummaryData(details: NonComplianceDetails): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const summary = new SummaryFactory();

  summary
    .addSection('Details')
    .addRow('Type of non-compliance', NON_COMPLIANCE_TYPE_LABELS[details.nonComplianceType])
    .addRow('When did the operator become non-compliant?', datePipe.transform(details.nonCompliantDate, 'd MMM yyyy'))
    .addRow('When did the operator become compliant?', datePipe.transform(details.compliantDate, 'd MMM yyyy'))
    .addRow('Comments', details.comment);

  summary.addSection('Relevant items of non-compliance');

  if (details.relevantWorkflows?.length) {
    summary.addRow('Relevant tasks or workflows', details.relevantWorkflows);
  }

  if (details.relevantFacilities?.length) {
    summary.addRow(
      'Relevant facilities',
      details.relevantFacilities.map((facility) => facility.facilityBusinessId),
    );
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
