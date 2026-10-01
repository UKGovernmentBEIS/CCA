import { DatePipe, DecimalPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { FacilityPatReportingTypesPipe } from '@shared/pipes';

import { FacilityPerformanceAccountTemplateSavingAction } from 'cca-api';

export function toFacilityPATReportingSummaryData(action: FacilityPerformanceAccountTemplateSavingAction): SummaryData {
  const datePipe = new DatePipe('en-GB');
  const decimalPipe = new DecimalPipe('en-GB');
  const typesPipe = new FacilityPatReportingTypesPipe();

  const factory = new SummaryFactory().addSection('Details');

  if (action?.actionCategoryType !== 'NO_ACTION') {
    factory.addTextAreaRow('Description', action?.savingActionsImplemented);
  }

  factory.addRow('Category', typesPipe.transform(action?.actionCategoryType));

  if (action?.actionCategoryType !== 'NO_ACTION') {
    factory
      .addRow('Supply/demand side action', typesPipe.transform(action?.supplyDemandSideMeasure))
      .addRow('Reason for implementation', action?.reasonsForImplementation)
      .addRow('Implementation date', datePipe.transform(action?.implementationDate, 'dd MMM yyyy'))
      .addRow(
        'Fixed/Variable energy or carbon emissions',
        typesPipe.transform(action?.fixedEnergyConsumptionOrCarbonEmissionsImpacted),
      )
      .addRow(
        'Percentage of total energy consumption or carbon affected (%)',
        `${decimalPipe.transform(action?.energyConsumptionOrCarbonEmissionsImpactedPercentage)}`,
      )
      .addRow(
        'Expected extent of implementation (%)',
        `${decimalPipe.transform(action?.expectedExtentOfChangeImplementedPercentage)}`,
      )
      .addRow(
        'Expected efficiency improvement (%)',
        `${decimalPipe.transform(action?.expectedSavingsFromTheChangeImplementedPercentage)}`,
      )
      .addRow(
        'Estimated overall impact (%)',
        `${decimalPipe.transform(action?.estimatedChangeInEnergyConsumptionPercentage)}`,
      );
  }

  factory.addTextAreaRow('Notes', action?.notes);

  return factory.create();
}
