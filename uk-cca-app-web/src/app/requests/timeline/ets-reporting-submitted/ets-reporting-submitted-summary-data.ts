import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { boolToString } from '@shared/utils';

import { EmissionsTradingSchemeDataSubmittedRequestActionPayload } from 'cca-api';

export function toEtsReportingSubmittedSummary(
  payload: EmissionsTradingSchemeDataSubmittedRequestActionPayload,
): SummaryData {
  const govukDatePipe = new GovukDatePipe();
  const details = payload?.details;
  const performanceData = payload?.performanceData;

  // FacilityDetails validates participation if uketsId is non-null; the action preserves that ID.
  const summary = new SummaryFactory()
    .addSection('')
    .addRow('Participates in UK ETS', performanceData?.uketsId != null ? `Yes, ${performanceData.uketsId}` : 'No')
    .addSection('Details')
    .addRow('Reporting year', details?.targetPeriodYear?.toString())
    .addRow('Created', govukDatePipe.transform(details?.creationDate))
    .addRow('Submitted', govukDatePipe.transform(details?.submissionDate, 'datetime'))
    .addSection('Ultra-small emitter (USE) in UK ETS')
    .addRow(
      'Is this facility registered as an ultra-small emitter (USE) in UK ETS?',
      boolToString(performanceData?.isUltraSmallEmitter),
    );

  if (performanceData?.isUltraSmallEmitter === true) {
    summary.addRow(
      'Do you want to voluntarily provide details of the fuels used in the year?',
      boolToString(payload?.calculatedResults != null),
    );
  }

  return summary.create();
}
