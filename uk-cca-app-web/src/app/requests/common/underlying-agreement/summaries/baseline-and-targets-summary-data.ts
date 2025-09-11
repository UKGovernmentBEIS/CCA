import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  BaselineData,
  SchemeData,
  TargetComposition,
  TargetPeriod6Details,
  Targets,
  UnderlyingAgreementReviewDecision,
} from 'cca-api';

import { boolToString } from '../../utils';
import { AgreementCompositionTypePipe, MeasurementTypeToUnitPipe } from '../pipes';
import { MeasurementTypeToOptionTextPipe } from '../pipes';
import { getBaselineUnits, getMeasurementAndThroughputUnits } from '../target-periods';
import { BaseLineAndTargetsStep } from '../types';
import { addDecisionSummaryData } from './decision-summary-data';

type BaselineAndTargetsSummaryMetadata = {
  isTp5Period: boolean;
  baselineExists: boolean;
  isEditable: boolean;
  attachments: { submit: Record<string, string>; review: Record<string, string> };
  downloadUrl: string;
};

export function toBaselineAndTargetsSummaryData(
  isTp5Period: boolean,
  baselineExists: boolean,
  sectorSchemeData: SchemeData,
  targetPeriodDetails: TargetPeriod6Details,
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl?: string,
) {
  const factory = new SummaryFactory();

  if (!isTp5Period) {
    return targetPeriodSummaryData(
      factory,
      sectorSchemeData,
      targetPeriodDetails,
      attachments,
      isEditable,
      multiFileDownloadUrl,
    ).create();
  }

  factory
    .addSection('', `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`)
    .addRow('Are you providing baseline and target information for TP5 (2021 to 2022)?', boolToString(baselineExists), {
      change: isEditable,
      appendChangeParam: true,
    });

  if (baselineExists === false) return factory.create();

  targetPeriodSummaryData(
    factory,
    sectorSchemeData,
    targetPeriodDetails,
    attachments,
    isEditable,
    multiFileDownloadUrl,
  );

  return factory.create();
}

export function toBaselineAndTargetsSummaryDataWithDecision(
  sectorSchemeData: SchemeData,
  targetPeriodDetails: TargetPeriod6Details,
  decision: UnderlyingAgreementReviewDecision,
  { isTp5Period, attachments, baselineExists, downloadUrl, isEditable }: BaselineAndTargetsSummaryMetadata,
) {
  let factory = new SummaryFactory();

  if (!isTp5Period) {
    factory = targetPeriodSummaryData(
      factory,
      sectorSchemeData,
      targetPeriodDetails,
      attachments.submit,
      isEditable,
      downloadUrl,
    );

    return addDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
  } else {
    factory
      .addSection('', `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`)
      .addRow(
        'Are you providing baseline and target information for TP5 (2021 to 2022)?',
        boolToString(baselineExists),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );

    if (!baselineExists) {
      return addDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
    }

    factory = targetPeriodSummaryData(
      factory,
      sectorSchemeData,
      targetPeriodDetails,
      attachments.submit,
      isEditable,
      downloadUrl,
    );

    return addDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
  }
}

function targetPeriodSummaryData(
  factory: SummaryFactory,
  sectorSchemeData: SchemeData,
  targetPeriod6Details: TargetPeriod6Details,
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl?: string,
): SummaryFactory {
  const targetComposition = targetPeriod6Details?.targetComposition;
  const baselineData = targetPeriod6Details?.baselineData;
  const targets = targetPeriod6Details?.targets;

  addTargetCompositionSection(
    factory,
    sectorSchemeData,
    targetComposition,
    attachments,
    isEditable,
    multiFileDownloadUrl,
  );

  addBaselineDataSection(factory, baselineData, targetComposition, attachments, isEditable, multiFileDownloadUrl);
  addTargetsSection(factory, targets, targetComposition, isEditable);

  return factory;
}

function addTargetCompositionSection(
  factory: SummaryFactory,
  sectorSchemeData: SchemeData,
  targetComposition: TargetComposition,
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl: string,
): void {
  const measurementPipe = new MeasurementTypeToOptionTextPipe();
  const agreementTypePipe = new AgreementCompositionTypePipe();
  const decimalPipe = new DecimalPipe('en-GB');

  factory
    .addSection('Target composition', `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`)
    .addFileListRow(
      'Target calculator file',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([targetComposition?.calculatorFile], attachments),
        multiFileDownloadUrl,
      ),
      { change: isEditable },
    )
    .addRow(
      'Energy or carbon units used by the sector',
      measurementPipe.transform(sectorSchemeData?.sectorMeasurementType),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    )
    .addRow(
      'Energy or carbon units used by the target unit',
      measurementPipe.transform(targetComposition?.measurementType),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    )
    .addRow(
      'Target type for agreement composition',
      agreementTypePipe.transform(targetComposition?.agreementCompositionType),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );

  if (targetComposition?.agreementCompositionType !== 'NOVEM') {
    if (sectorSchemeData?.sectorThroughputUnit) {
      factory
        .addRow('The umbrella agreement throughput has a unit of', sectorSchemeData.sectorThroughputUnit)
        .addRow(
          'Is the target unit’s throughput measured in units that differ from those of the umbrella agreement?',
          boolToString(targetComposition?.isTargetUnitThroughputMeasured),
          {
            change: isEditable,
            appendChangeParam: true,
          },
        );

      if (targetComposition?.isTargetUnitThroughputMeasured) {
        factory
          .addRow('Target unit throughput has a unit of', targetComposition?.throughputUnit, {
            change: isEditable,
            appendChangeParam: true,
          })
          .addRow('Conversion factor', decimalPipe.transform(targetComposition?.conversionFactor, '1.0-7'), {
            change: isEditable,
            appendChangeParam: true,
          });
      }
    } else {
      factory.addRow('Target unit throughput has a unit of', targetComposition?.throughputUnit);
    }

    if (sectorSchemeData?.sectorThroughputUnit && targetComposition.isTargetUnitThroughputMeasured) {
      factory.addFileListRow(
        'Upload evidence',
        fileUtils.toDownloadableFiles(
          fileUtils.extractAttachments(targetComposition?.conversionEvidences, attachments),
          multiFileDownloadUrl,
        ),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );
    }
  }
}

function addBaselineDataSection(
  factory: SummaryFactory,
  baselineData: BaselineData,
  targetComposition: TargetComposition,
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl?: string,
): void {
  const datePipe = new GovukDatePipe();
  const decimalPipe = new DecimalPipe('en-GB');
  const measurementTypeToUnit = new MeasurementTypeToUnitPipe();

  factory
    .addSection('Details of baseline data', `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`)
    .addRow(
      'Is at least 12 months of consecutive baseline data available?',
      boolToString(baselineData?.isTwelveMonths),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    )
    .addRow(
      baselineData?.isTwelveMonths === true
        ? 'Start date of baseline'
        : 'Enter the date that 12 months of data will be available.',
      datePipe.transform(baselineData?.baselineDate),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    )
    .addTextAreaRow(
      baselineData?.isTwelveMonths === true
        ? 'Explain why you are using a different baseline year'
        : 'Explain how the target unit fits the greenfield criteria',
      baselineData?.explanation,
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );

  if (baselineData?.isTwelveMonths === false) {
    factory.addFileListRow(
      'Evidence',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(baselineData?.greenfieldEvidences, attachments),
        multiFileDownloadUrl,
      ),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  factory
    .addRow(
      `Baseline ${measurementTypeToUnit.transform(targetComposition.measurementType)} for the target facility`,
      decimalPipe.transform(baselineData?.energy, '1.0-7'),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    )
    .addRow(
      'Have you used the special reporting mechanism to adjust the baseline throughput for any of the facilities in the target unit using combined heat and power (CHP)?',
      boolToString(baselineData?.usedReportingMechanism),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    )
    .addRow(
      `Baseline throughput ${targetComposition?.throughputUnit != null ? '(' + targetComposition.throughputUnit + ')' : ''}`,
      decimalPipe.transform(baselineData?.throughput, '1.0-7'),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );

  if (targetComposition?.agreementCompositionType === 'RELATIVE') {
    factory.addRow(
      `Performance (${getMeasurementAndThroughputUnits(targetComposition?.throughputUnit, targetComposition?.measurementType)})`,
      decimalPipe.transform(baselineData?.performance, '1.0-7'),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  factory.addRow(
    'Baseline energy to carbon factor (kgC/kWh)',
    decimalPipe.transform(baselineData?.energyCarbonFactor, '1.0-7'),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );
}

function addTargetsSection(
  factory: SummaryFactory,
  targets: Targets,
  targetComposition: TargetComposition,
  isEditable: boolean,
): void {
  const decimalPipe = new DecimalPipe('en-GB');

  factory
    .addSection('Targets', `../${BaseLineAndTargetsStep.ADD_TARGETS}`)
    .addRow('Improvement (%)', decimalPipe.transform(targets?.improvement, '1.0-7'), {
      change: isEditable,
      appendChangeParam: true,
    });

  if (targetComposition?.agreementCompositionType !== 'NOVEM') {
    factory.addRow(
      `Target (${getBaselineUnits(targetComposition?.throughputUnit, targetComposition?.measurementType, targetComposition?.agreementCompositionType)})`,
      decimalPipe.transform(targets?.target, '1.0-7'),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }
}
