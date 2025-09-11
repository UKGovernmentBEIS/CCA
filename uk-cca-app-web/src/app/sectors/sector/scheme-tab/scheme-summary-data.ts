import { PercentPipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { fileUtils } from '@shared/utils';

import { SectorAssociationSchemesDTO, SubsectorAssociationSchemesDTO, TargetCommitmentDTO } from 'cca-api';

function addTargetCommitmentsSection(
  factory: SummaryFactory,
  targetCommitments: TargetCommitmentDTO[],
  percentPipe: PercentPipe,
): void {
  factory.addPlainTextSection('Sector commitment');

  targetCommitments
    .sort((a, b) => {
      const numA = Number(a.targetPeriod.trim().substring(0, 4).replace(/\D/g, ''));
      const numB = Number(b.targetPeriod.trim().substring(0, 4).replace(/\D/g, ''));
      return numA - numB;
    })
    .forEach((commitment) => {
      const value = percentPipe.transform(Number(commitment.targetImprovement || 0), '1.0-3', 'en-GB');
      factory.addRow(commitment.targetPeriod, value);
    });
}

export function toSectorSchemeSummaryData(
  sectorScheme: SectorAssociationSchemesDTO,
  subSectorsLength: number,
): SummaryData {
  if (!sectorScheme) return [];

  const factory = new SummaryFactory();
  const percentPipe = new PercentPipe('en-GB');
  const hasSubSectors = subSectorsLength > 0;

  const cca2Scheme = sectorScheme.sectorAssociationSchemeMap?.[SchemeVersion.CCA_2];
  if (cca2Scheme) {
    factory.addSection('CCA2 (2013-2024)');

    if (!hasSubSectors) {
      factory.addPlainTextSection('Details');
    }

    if (cca2Scheme.umbrellaAgreement) {
      const file = {
        ...cca2Scheme.umbrellaAgreement,
        name: `${cca2Scheme.umbrellaAgreement.fileName} (${cca2Scheme.umbrellaAgreement.fileType}, ${cca2Scheme.umbrellaAgreement.fileSize}KB)`,
      };
      factory.addFileListRow(
        'Umbrella agreement CCA2',
        fileUtils.toDownloadableFileFromInfoDTO([file], 'sector-documents'),
      );
    }

    if (!hasSubSectors && cca2Scheme.targetSet) {
      factory.addRow('Target type', cca2Scheme.targetSet.targetCurrencyType);

      if (['Absolute', 'Relative'].includes(cca2Scheme.targetSet.targetCurrencyType)) {
        factory.addRow('Throughput unit', cca2Scheme.targetSet.throughputUnit);
      }

      factory.addRow('Energy or Carbon unit', cca2Scheme.targetSet.energyOrCarbonUnit);
    }

    if (!hasSubSectors && cca2Scheme.targetSet?.targetCommitments) {
      addTargetCommitmentsSection(factory, cca2Scheme.targetSet.targetCommitments, percentPipe);
    }
  }

  const cca3Scheme = sectorScheme.sectorAssociationSchemeMap?.[SchemeVersion.CCA_3];
  if (cca3Scheme) {
    factory.addSection('CCA3 (2026-2030)');

    if (!hasSubSectors) {
      factory.addPlainTextSection('Details');
    }

    if (cca3Scheme.umbrellaAgreement) {
      const file = {
        ...cca3Scheme.umbrellaAgreement,
        name: `${cca3Scheme.umbrellaAgreement.fileName} (${cca3Scheme.umbrellaAgreement.fileType}, ${cca3Scheme.umbrellaAgreement.fileSize}KB)`,
      };
      factory.addFileListRow(
        'Umbrella agreement CCA3',
        fileUtils.toDownloadableFileFromInfoDTO([file], 'sector-documents'),
      );
    }

    if (!hasSubSectors && cca3Scheme.targetSet) {
      factory.addRow('Target type', cca3Scheme.targetSet.targetCurrencyType);

      if (['Absolute', 'Relative'].includes(cca3Scheme.targetSet.targetCurrencyType)) {
        factory.addRow('Throughput unit', cca3Scheme.targetSet.throughputUnit);
      }

      factory.addRow('Energy or Carbon unit', cca3Scheme.targetSet.energyOrCarbonUnit);
    }

    if (!hasSubSectors && cca3Scheme.targetSet?.targetCommitments) {
      addTargetCommitmentsSection(factory, cca3Scheme.targetSet.targetCommitments, percentPipe);
    }
  }

  return factory.create();
}

export function toSubsectorSchemeSummaryData(subsectorScheme: SubsectorAssociationSchemesDTO): SummaryData {
  if (!subsectorScheme) return [];

  const factory = new SummaryFactory();
  const percentPipe = new PercentPipe('en-GB');

  const cca2Scheme = subsectorScheme.subsectorAssociationSchemeMap?.[SchemeVersion.CCA_2];
  if (cca2Scheme) {
    factory.addSection('CCA2 (2013-2024)').addPlainTextSection('Details');

    if (cca2Scheme.targetSet) {
      factory.addRow('Target type', cca2Scheme.targetSet.targetCurrencyType);

      if (['Absolute', 'Relative'].includes(cca2Scheme.targetSet.targetCurrencyType)) {
        factory.addRow('Throughput unit', cca2Scheme.targetSet.throughputUnit);
      }

      factory.addRow('Energy or Carbon unit', cca2Scheme.targetSet.energyOrCarbonUnit);
    }

    if (cca2Scheme.targetSet?.targetCommitments) {
      addTargetCommitmentsSection(factory, cca2Scheme.targetSet.targetCommitments, percentPipe);
    }
  }

  const cca3Scheme = subsectorScheme.subsectorAssociationSchemeMap?.[SchemeVersion.CCA_3];
  if (cca3Scheme) {
    factory.addSection('CCA3 (2026-2030)').addPlainTextSection('Details');

    if (cca3Scheme.targetSet) {
      factory.addRow('Target type', cca3Scheme.targetSet.targetCurrencyType);

      if (['Absolute', 'Relative'].includes(cca3Scheme.targetSet.targetCurrencyType)) {
        factory.addRow('Throughput unit', cca3Scheme.targetSet.throughputUnit);
      }

      factory.addRow('Energy or Carbon unit', cca3Scheme.targetSet.energyOrCarbonUnit);
    }

    if (cca3Scheme.targetSet?.targetCommitments) {
      addTargetCommitmentsSection(factory, cca3Scheme.targetSet.targetCommitments, percentPipe);
    }
  }

  return factory.create();
}
