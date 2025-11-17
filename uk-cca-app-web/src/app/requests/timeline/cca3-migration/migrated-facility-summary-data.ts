import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { boolToString, MeasurementTypeToOptionTextPipe, MeasurementTypeToUnitPipe } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { Cca3FacilityMigrationData } from 'cca-api';

export function toMigratedFacilitySummaryData(facility: Cca3FacilityMigrationData): SummaryData {
  const decimalPipe = new DecimalPipe('en-GB');
  const datePipe = new GovukDatePipe();
  const measurementTypeToOptionTextPipe = new MeasurementTypeToOptionTextPipe();
  const measurementTypeToUnit = new MeasurementTypeToUnitPipe();

  const factory = new SummaryFactory();

  factory
    .addSection('Facility details')
    .addRow('Facility ID', facility.facilityBusinessId)
    .addRow('Site name', facility.facilityName)
    .addRow(
      'Will this facility participate in the CCA3 (2026-2030) scheme?',
      boolToString(facility.participatingInCca3Scheme),
    )
    .addRow('Scheme participation', facility.participatingInCca3Scheme ? 'Both' : 'CCA2');

  if (facility.participatingInCca3Scheme) {
    // BASELINE DATA
    factory
      .addSection('Details of baseline data')
      .addFileListRow(
        'Target calculator',
        fileUtils.toDownloadableFileFromInfoDTO(
          [{ name: facility?.calculatorFileName, uuid: facility?.calculatorFileUuid }],
          '../../../file-download',
        ),
      )
      .addRow('Baseline start date', datePipe.transform(facility.baselineDate))
      .addTextAreaRow('Reason', facility.explanation)
      .addRow(
        'Energy or carbon units used by the facility',
        measurementTypeToOptionTextPipe.transform(facility.measurementType),
      )
      .addRow(
        `Baseline energy to carbon factor (kgC/${measurementTypeToUnit.transform(facility.measurementType)})`,
        String(facility.energyCarbonFactor),
      )
      .addRow('SRM used', boolToString(facility.usedReportingMechanism))
      .addRow(
        'Baseline total fixed energy (or carbon) value',
        `${decimalPipe.transform(facility.totalFixedEnergy, '1.0-7')} ${measurementTypeToUnit.transform(facility.measurementType)}`,
      )
      .addRow(
        'Baseline total variable energy (or carbon)',
        `${decimalPipe.transform(facility.totalVariableEnergy, '1.0-7')} ${measurementTypeToUnit.transform(facility.measurementType)}`,
      )
      .addRow('Baseline total throughput', decimalPipe.transform(facility.totalThroughput, '1.3'))
      .addRow('Baseline throughput unit', facility.throughputUnit);

    // TARGETS
    factory
      .addSection('Targets')
      .addRow('TP7 (2026) improvement (%)', decimalPipe.transform(facility.tp7Improvement, '1.0-7'))
      .addRow('TP8 (2027 to 2028) improvement (%)', decimalPipe.transform(facility.tp8Improvement, '1.0-7'))
      .addRow('TP9 (2029 to 2030) improvement (%)', decimalPipe.transform(facility.tp9Improvement, '1.0-7'));
  }

  return factory.create();
}
