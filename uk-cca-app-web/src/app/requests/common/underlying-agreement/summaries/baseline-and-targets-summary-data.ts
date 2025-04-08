import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryFactory } from '@shared/components';
import { transformAttachmentsAndFileUUIDsToDownloadableFiles } from '@shared/utils';

import {
  BaselineData,
  SectorAssociationDetails,
  TargetComposition,
  TargetPeriod6Details,
  Targets,
  UnderlyingAgreementReviewDecision,
} from 'cca-api';

import { getBaselineUnits, getMeasurementAndThroughputUnits } from '../modules';
import { AgreementCompositionTypePipe, MeasurementTypeToUnitPipe } from '../pipes';
import { MeasurementTypeToOptionTextPipe } from '../pipes';
import { BaseLineAndTargetsStep } from '../underlying-agreement.types';
import { boolToString } from '../utils';
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
  sectorAssociation: SectorAssociationDetails,
  targetPeriodDetails: TargetPeriod6Details,
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl?: string,
) {
  const factory = new SummaryFactory();

  if (!isTp5Period) {
    return targetPeriodSummaryData(
      factory,
      sectorAssociation,
      targetPeriodDetails,
      attachments,
      isEditable,
      multiFileDownloadUrl,
    ).create();
  }

  factory.addSection('', `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`);
  factory.addRow(
    'Are you providing baseline and target information for TP5 (2021 to 2022)?',
    boolToString(baselineExists),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  if (baselineExists === false) {
    return factory.create();
  }

  targetPeriodSummaryData(
    factory,
    sectorAssociation,
    targetPeriodDetails,
    attachments,
    isEditable,
    multiFileDownloadUrl,
  );

  return factory.create();
}

export function toBaselineAndTargetsSummaryDataWithDecision(
  sectorAssociation: SectorAssociationDetails,
  targetPeriodDetails: TargetPeriod6Details,
  decision: UnderlyingAgreementReviewDecision,
  { isTp5Period, attachments, baselineExists, downloadUrl, isEditable }: BaselineAndTargetsSummaryMetadata,
) {
  let factory = new SummaryFactory();

  if (!isTp5Period) {
    factory = targetPeriodSummaryData(
      factory,
      sectorAssociation,
      targetPeriodDetails,
      attachments.submit,
      isEditable,
      downloadUrl,
    );

    return addDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
  } else {
    factory.addSection('', `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`);
    factory.addRow(
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
      sectorAssociation,
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
  sectorAssociation: SectorAssociationDetails,
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
    sectorAssociation,
    targetComposition,
    attachments,
    isEditable,
    multiFileDownloadUrl,
  );

  addBaselineDataSection(
    factory,
    baselineData,
    targetComposition,
    sectorAssociation?.throughputUnit,
    attachments,
    isEditable,
    multiFileDownloadUrl,
  );

  addTargetsSection(factory, targets, targetComposition, sectorAssociation?.throughputUnit, isEditable);

  return factory;
}

function addTargetCompositionSection(
  factory: SummaryFactory,
  sectorAssociationDetails: SectorAssociationDetails,
  targetComposition: TargetComposition,
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl: string,
): void {
  const measurementPipe = new MeasurementTypeToOptionTextPipe();
  const agreementTypePipe = new AgreementCompositionTypePipe();
  const decimalPipe = new DecimalPipe('en-GB');

  factory.addSection('Target composition', `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`);

  factory.addFileListRow(
    'Target calculator file',
    transformAttachmentsAndFileUUIDsToDownloadableFiles(
      [targetComposition?.calculatorFile],
      attachments,
      multiFileDownloadUrl,
    ),
    { change: isEditable },
  );

  factory.addRow(
    'Energy or carbon units used by the sector',
    measurementPipe.transform(sectorAssociationDetails?.measurementType),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  factory.addRow(
    'Energy or carbon units used by the target unit',
    measurementPipe.transform(targetComposition?.measurementType),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  factory.addRow(
    'Target type for agreement composition',
    agreementTypePipe.transform(targetComposition?.agreementCompositionType),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  if (targetComposition?.agreementCompositionType !== 'NOVEM') {
    if (sectorAssociationDetails?.throughputUnit) {
      factory.addRow('The umbrella agreement throughput has a unit of', sectorAssociationDetails.throughputUnit);
      factory.addRow(
        'Is the target unit’s throughput measured in units that differ from those of the umbrella agreement?',
        boolToString(targetComposition?.isTargetUnitThroughputMeasured),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );

      if (targetComposition?.isTargetUnitThroughputMeasured) {
        factory.addRow('Target unit throughput has a unit of', targetComposition?.throughputUnit, {
          change: isEditable,
          appendChangeParam: true,
        });

        factory.addRow('Conversion factor', decimalPipe.transform(targetComposition?.conversionFactor, '1.0-7'), {
          change: isEditable,
          appendChangeParam: true,
        });
      }
    } else {
      factory.addRow('Target unit throughput has a unit of', targetComposition?.throughputUnit);
    }

    factory.addFileListRow(
      'Upload evidence',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        targetComposition?.conversionEvidences,
        attachments,
        multiFileDownloadUrl,
      ),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }
}

function addBaselineDataSection(
  factory: SummaryFactory,
  baselineData: BaselineData,
  targetComposition: TargetComposition,
  sectorThroughputUnit: SectorAssociationDetails['throughputUnit'],
  attachments: Record<string, string>,
  isEditable: boolean,
  multiFileDownloadUrl?: string,
): void {
  const datePipe = new GovukDatePipe();
  const decimalPipe = new DecimalPipe('en-GB');
  const measurementTypeToUnit = new MeasurementTypeToUnitPipe();
  factory.addSection('Details of baseline data', `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`);

  factory.addRow(
    'Is at least 12 months of consecutive baseline data available?',
    boolToString(baselineData?.isTwelveMonths),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  factory.addRow(
    baselineData?.isTwelveMonths === true
      ? 'Start date of baseline'
      : 'Enter the date that 12 months of data will be available.',
    datePipe.transform(baselineData?.baselineDate),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  factory.addRow(
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
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        baselineData?.greenfieldEvidences,
        attachments,
        multiFileDownloadUrl,
      ),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  factory.addRow(
    `Baseline ${measurementTypeToUnit.transform(targetComposition.measurementType)} for the target facility`,
    decimalPipe.transform(baselineData?.energy, '1.0-7'),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  factory.addRow(
    'Have you used the special reporting mechanism to adjust the baseline throughput for any of the facilities in the target unit using combined heat and power (CHP)?',
    boolToString(baselineData?.usedReportingMechanism),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  factory.addRow(
    `Baseline throughput ${targetComposition?.throughputUnit != null ? '(' + targetComposition.throughputUnit + ')' : sectorThroughputUnit ? '(' + sectorThroughputUnit + ')' : ''}`,
    decimalPipe.transform(baselineData?.throughput, '1.0-7'),
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  if (targetComposition?.agreementCompositionType === 'RELATIVE') {
    factory.addRow(
      `Performance (${getMeasurementAndThroughputUnits(targetComposition?.throughputUnit, sectorThroughputUnit, targetComposition?.measurementType)})`,
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
  sectorThroughputUnit: SectorAssociationDetails['throughputUnit'],
  isEditable: boolean,
): void {
  const decimalPipe = new DecimalPipe('en-GB');

  factory.addSection('Targets', `../${BaseLineAndTargetsStep.ADD_TARGETS}`);

  factory.addRow('Improvement (%)', decimalPipe.transform(targets?.improvement, '1.0-7'), {
    change: isEditable,
    appendChangeParam: true,
  });

  if (targetComposition?.agreementCompositionType !== 'NOVEM') {
    factory.addRow(
      `Target (${getBaselineUnits(targetComposition?.throughputUnit, sectorThroughputUnit, targetComposition?.measurementType, targetComposition?.agreementCompositionType)})`,
      decimalPipe.transform(targets?.target, '1.0-7'),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }
}
