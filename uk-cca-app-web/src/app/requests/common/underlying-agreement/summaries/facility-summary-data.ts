import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import {
  calculateFixedEnergy,
  calculateOtherYearsVariableEnergy,
  calculateTotalEnergy,
  calculateVariableEnergy,
} from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe, transformAddress } from '@shared/pipes';
import { Improvement, SchemeVersions } from '@shared/types';
import { fileUtils, transformPhoneNumber } from '@shared/utils';

import {
  Facility,
  SchemeData,
  UnderlyingAgreementFacilityReviewDecision,
  UnderlyingAgreementVariationFacilityReviewDecision,
} from 'cca-api';

import { boolToString, isCCA2Scheme, isCCA3Scheme } from '../../utils';
import {
  AgreementCompositionTypePipe,
  AgreementTypeEnum,
  ApplicationReasonTypeEnum,
  CaNameEnum,
  MeasurementTypeToOptionTextPipe,
  MeasurementTypeToUnitPipe,
} from '../pipes';
import { FacilityWizardStep } from '../types';
import { addFacilityDecisionSummaryData } from './decision-summary-data';

function toFacilityWizardSummaryFactory(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean; factory?: SummaryFactory; productsLink?: string } = {},
): SummaryFactory {
  const facilityDetails = facility?.facilityDetails;
  const facilityContact = facility?.facilityContact;
  const eligibility = facility?.eligibilityDetailsAndAuthorisation;
  const facilityExtent = facility?.facilityExtent;
  const applyRule = facility?.apply70Rule;
  const targetComposition = facility?.cca3BaselineAndTargets?.targetComposition;
  const baselineData = facility?.cca3BaselineAndTargets?.baselineData;
  const facilityTargets = facility?.cca3BaselineAndTargets?.facilityTargets;

  const decimalPipe = new DecimalPipe('en-GB');
  const datePipe = new GovukDatePipe();
  const measurementTypeToOptionTextPipe = new MeasurementTypeToOptionTextPipe();
  const agreementTypePipe = new AgreementCompositionTypePipe();
  const measurementTypeToUnitPipe = new MeasurementTypeToUnitPipe();

  const formatNumber = (value: string | number | null | undefined) => {
    if (value === null || value === undefined || value === '') {
      return '';
    }
    const parsed = Number(value);
    return Number.isFinite(parsed) ? (decimalPipe.transform(parsed, '1.0-7') ?? '') : '';
  };

  const baselineEnergy = facility?.cca3BaselineAndTargets?.facilityBaselineEnergyConsumption;
  const baselineEnergyProducts = baselineEnergy?.variableEnergyConsumptionDataByProduct ?? [];
  const includedBaselineEnergyProducts = baselineEnergyProducts.filter(
    (product) => (product.productStatus ?? '').toUpperCase() !== 'EXCLUDED',
  );
  const baselineYear = baselineData?.baselineDate ? new Date(baselineData.baselineDate).getFullYear() : null;
  const fixedEnergyValue = calculateFixedEnergy(baselineEnergy?.totalFixedEnergy);
  const variableEnergyValue = calculateVariableEnergy(
    baselineEnergy?.hasVariableEnergy,
    baselineEnergy?.variableEnergyType,
    baselineEnergy?.baselineVariableEnergy,
    includedBaselineEnergyProducts,
    baselineYear,
  );
  const totalEnergyValue = calculateTotalEnergy(fixedEnergyValue, variableEnergyValue);
  const otherYearsVariableEnergy = calculateOtherYearsVariableEnergy(
    includedBaselineEnergyProducts,
    baselineYear,
    baselineEnergy?.variableEnergyType,
  );

  const factory = opts?.factory ?? new SummaryFactory();

  factory
    // FACILITY DETAILS SECTION
    .addSection('Facility Details', `../${FacilityWizardStep.DETAILS}`)
    .addRow('Facility ID', facility?.facilityId)
    .addRow('Site name', facility?.facilityDetails.name, { change: opts.changeName && isEditable })
    .addRow(
      'Are the activities carried out in this facility included in the UK ETS?',
      boolToString(facilityDetails?.isCoveredByUkets),
      {
        change: isEditable,
      },
    )
    .addRow('UK ETS Installation Identifier', facilityDetails?.uketsId, {
      change: isEditable,
    })
    .addRow('Application reason', ApplicationReasonTypeEnum[facilityDetails?.applicationReason], {
      change: isEditable && facility?.status === 'NEW',
    })
    .addRow('Previous facility ID', facilityDetails?.previousFacilityId, {
      change: isEditable && facility?.status === 'NEW',
    });

  if (isCCA2Scheme(schemeVersions)) {
    factory.addRow(
      'Will this facility participate in the CCA3 (2026-2030) scheme?',
      boolToString(isCCA3Scheme(facilityDetails?.participatingSchemeVersions)),
      {
        change: isEditable,
      },
    );
  }

  factory
    .addRow('Scheme participation', toSchemeParticipationString(facilityDetails?.participatingSchemeVersions), {
      change: isEditable,
    })
    .addTextAreaRow('Facility address', transformAddress(facilityDetails?.facilityAddress), {
      change: isEditable,
    })

    // FACILITY CONTACT DETAILS SECTION
    .addSection('Facility contact details', `../${FacilityWizardStep.CONTACT_DETAILS}`)
    .addRow('First name', facilityContact?.firstName, {
      change: isEditable,
    })
    .addRow('Last name', facilityContact?.lastName, {
      change: isEditable,
    })
    .addRow('Email address', facilityContact?.email, {
      change: isEditable,
    })
    .addTextAreaRow('Contact address', transformAddress(facilityContact?.address), {
      change: isEditable,
    })
    .addRow('Phone number', transformPhoneNumber(facilityContact?.phoneNumber), {
      change: isEditable,
    })

    // ELIGIBILITY DETAILS AND AUTHORISATION SECTION
    .addSection('CCA eligibility details and authorisation', `../${FacilityWizardStep.ELIGIBILITY_DETAILS}`)
    .addRow(
      'Is the facility adjacent to or connected to an existing CCA facility?',
      boolToString(eligibility?.isConnectedToExistingFacility),
      {
        change: isEditable,
      },
    )
    .addRow('Facility ID of adjacent facility', eligibility?.adjacentFacilityId, {
      change: isEditable,
    })
    .addRow('Agreement type - eligible under', AgreementTypeEnum[eligibility?.agreementType], {
      change: isEditable,
    })
    .addRow(
      'Do you hold a current Environmental Permitting Regulations (EPR) authorisation for any activity being carried out in the facility?',
      boolToString(eligibility?.erpAuthorisationExists),
      {
        change: isEditable,
      },
    )
    .addRow('Authorisation number', eligibility?.authorisationNumber, {
      change: isEditable,
    })
    .addRow('Regulator name', CaNameEnum[eligibility?.regulatorName], {
      change: isEditable,
    })
    .addFileListRow(
      'Attach a copy of the permit',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments([eligibility?.permitFile], attachments), downloadUrl),
      { change: isEditable },
    )

    // EXTENT OF THE FACILITY SECTION
    .addSection('Extent of the facility', `../${FacilityWizardStep.EXTENT}`)
    .addFileListRow(
      'Manufacturing process description',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([facilityExtent?.manufacturingProcessFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Process flow maps',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([facilityExtent?.processFlowFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Annotated site plans',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([facilityExtent?.annotatedSitePlansFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Eligible process description',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([facilityExtent?.eligibleProcessFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addRow('Are any directly associated activities claimed?', boolToString(facilityExtent?.areActivitiesClaimed), {
      change: isEditable,
    })
    .addFileListRow(
      'Directly associated activities description',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([facilityExtent?.activitiesDescriptionFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )

    // 70% RULE SECTION
    .addSection('70% rule', `../${FacilityWizardStep.APPLY_RULE}`)
    .addRow(
      'Energy consumed in the installation',
      applyRule?.energyConsumed != null ? `${decimalPipe.transform(applyRule?.energyConsumed, '1.0-7')} %` : '',
      {
        change: isEditable,
      },
    )
    .addRow(
      'Energy consumed in relation to 3/7ths provision',
      applyRule?.energyConsumedProvision
        ? `${decimalPipe.transform(applyRule?.energyConsumedProvision, '1.0-7')} %`
        : '',
      {
        change: isEditable,
      },
    )
    .addRow(
      'Energy consumed in eligible facility',
      applyRule?.energyConsumedEligible != null
        ? `${decimalPipe.transform(applyRule?.energyConsumedEligible, '1.0-7')} %`
        : '',
      {
        change: isEditable,
      },
    )
    .addRow('Sub-metered start date', applyRule?.startDate ? datePipe.transform(applyRule.startDate) : '', {
      change: isEditable,
    })
    .addFileListRow(
      'Evidence',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments([applyRule?.evidenceFile], attachments), downloadUrl),
      { change: isEditable },
    );

  if (isCCA3Scheme(schemeVersions) && facility?.cca3BaselineAndTargets) {
    // TARGET COMPOSITION SECTION
    factory
      .addSection('Target composition', `../${FacilityWizardStep.TARGET_COMPOSITION}`)
      .addFileListRow(
        'Target calculator file',
        fileUtils.toDownloadableFiles(
          fileUtils.extractAttachments([targetComposition?.calculatorFile], attachments),
          downloadUrl,
        ),
        { change: isEditable },
      )
      .addRow(
        'Energy or carbon units used by the sector',
        measurementTypeToOptionTextPipe.transform(sectorSchemeData?.sectorMeasurementType),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'Energy or carbon units used by the target unit',
        measurementTypeToOptionTextPipe.transform(targetComposition?.measurementType),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'Target type for agreement composition',
        agreementTypePipe.transform(targetComposition?.agreementCompositionType),
      )

      // BASELINE DATA SECTION
      .addSection('Details of baseline data', `../${FacilityWizardStep.BASELINE_DATA}`)
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
          downloadUrl,
        ),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );
    }

    factory
      .addRow(
        'Must the Special Reporting Methodology be applied for this facility?',
        boolToString(baselineData?.usedReportingMechanism),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        `Baseline energy to carbon factor (kgC/${measurementTypeToUnitPipe.transform(targetComposition.measurementType)})`,
        decimalPipe.transform(baselineData?.energyCarbonFactor, '1.0-7'),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )

      //BASELINE ENERGY CONSUMPTION SECTION
      .addSection(
        'Details of baseline energy or carbon consumption',
        `../${FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION}`,
      )
      .addRow(
        `Fixed baseline energy for the facility (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
        formatNumber(fixedEnergyValue),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );

    factory.addRow('Is there a variable energy amount?', boolToString(baselineEnergy?.hasVariableEnergy), {
      change: isEditable,
      appendChangeParam: true,
    });

    if (baselineEnergy?.hasVariableEnergy) {
      factory.addRow(
        'Indicate how you want to account for the portion of variable energy used (or carbon emitted) for your facility',
        baselineEnergy.variableEnergyType === 'TOTALS'
          ? 'Totals only'
          : baselineEnergy.variableEnergyType === 'BY_PRODUCT'
            ? 'Split by product'
            : '',
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );

      if (baselineEnergy.variableEnergyType === 'TOTALS') {
        factory.addRow(
          `Total baseline variable energy (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
          formatNumber(baselineEnergy.baselineVariableEnergy),
          {
            change: isEditable,
            appendChangeParam: true,
          },
        );
      }

      if (baselineEnergy.variableEnergyType === 'BY_PRODUCT') {
        const productCount = includedBaselineEnergyProducts.length;
        const productLabel =
          productCount === 0 ? 'No products added' : `${productCount} ${productCount === 1 ? 'Product' : 'Products'}`;

        factory.addRow('Products submitted', productLabel, {
          change: isEditable,
          appendChangeParam: true,
          link: `${opts?.productsLink ?? '../products'}`,
        });
      }
    }

    // Show throughput fields when hasVariableEnergy is false or when variableEnergyType is TOTALS
    if (
      baselineEnergy?.hasVariableEnergy === false ||
      (baselineEnergy?.hasVariableEnergy && baselineEnergy.variableEnergyType === 'TOTALS')
    ) {
      factory
        .addRow('Total baseline throughput', formatNumber(baselineEnergy.totalThroughput), {
          change: isEditable,
          appendChangeParam: true,
        })
        .addRow('Throughput unit', baselineEnergy.throughputUnit ?? '', {
          change: isEditable,
          appendChangeParam: true,
        });
    }

    factory
      .addRow(
        `Variable baseline energy for the facility (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
        formatNumber(variableEnergyValue),
      )
      .addRow(
        `Total baseline energy for the facility (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
        formatNumber(totalEnergyValue),
      );

    if (otherYearsVariableEnergy) {
      factory.addRow(
        `Other years - variable baseline energy (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
        formatNumber(otherYearsVariableEnergy),
      );
    }

    factory
      // TARGETS SECTION
      .addSection('Targets', `../${FacilityWizardStep.TARGETS}`)
      .addRow(
        'TP7 (2026) improvement (%)',
        decimalPipe.transform(facilityTargets?.improvements[Improvement.TP7], '1.0-7'),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'TP8 (2027 to 2028) improvement (%)',
        decimalPipe.transform(facilityTargets?.improvements[Improvement.TP8], '1.0-7'),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'TP9 (2029 to 2030) improvement (%)',
        decimalPipe.transform(facilityTargets?.improvements[Improvement.TP9], '1.0-7'),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );
  }

  return factory;
}

export function toFacilityWizardSummaryData(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean; productsLink?: string } = {},
): SummaryData {
  return toFacilityWizardSummaryFactory(
    facility,
    sectorSchemeData,
    schemeVersions,
    attachments,
    isEditable,
    downloadUrl,
    opts,
  ).create();
}

export function toFacilityWizardSummaryDataWithDecision(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  decision: UnderlyingAgreementFacilityReviewDecision | UnderlyingAgreementVariationFacilityReviewDecision,
  attachments: { submit: Record<string, string>; review: Record<string, string> },
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean; productsLink?: string } = {},
): SummaryData {
  const factory = toFacilityWizardSummaryFactory(
    facility,
    sectorSchemeData,
    schemeVersions,
    attachments.submit,
    isEditable,
    downloadUrl,
    {
      changeName: opts.changeName ?? true,
      productsLink: opts.productsLink,
    },
  );

  if (!decision?.type) return factory.create();

  return addFacilityDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
}

export function toFacilitySummaryDataWithStatus(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean } = {},
): SummaryData {
  const statusPipe = new StatusPipe();

  const factory = new SummaryFactory()
    .addSection('')
    .addRow('Facility status', statusPipe.transform(facility?.status) ?? '');

  if (facility?.excludedDate) {
    const datePipe = new GovukDatePipe();
    factory.addRow('Exclusion date', datePipe.transform(facility?.excludedDate));
  }

  return toFacilityWizardSummaryFactory(
    facility,
    sectorSchemeData,
    schemeVersions,
    attachments,
    isEditable,
    downloadUrl,
    {
      ...opts,
      factory: factory,
    },
  ).create();
}

function toSchemeParticipationString(schemeVersions: SchemeVersions): string {
  if (!schemeVersions || schemeVersions.length === 0) return '';
  return schemeVersions.length > 1 ? 'Both' : schemeVersions[0].replace('_', '');
}
