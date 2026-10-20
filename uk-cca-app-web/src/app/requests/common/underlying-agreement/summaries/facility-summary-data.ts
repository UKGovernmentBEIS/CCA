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
import { MeasurementTypeToOptionTextPipe, MeasurementTypeToUnitPipe } from '@shared/pipes';
import { Country, Improvement, SchemeVersions } from '@shared/types';
import { fileUtils, transformPhoneNumber } from '@shared/utils';

import {
  Apply70Rule,
  EligibilityDetailsAndAuthorisation,
  Facility,
  FacilityBaselineData,
  FacilityBaselineEnergyConsumption,
  FacilityExtent,
  FacilityTargetComposition,
  FacilityTargets,
  SchemeData,
  TargetUnitAccountContactDTO,
  UnderlyingAgreementFacilityReviewDecision,
  UnderlyingAgreementVariationFacilityReviewDecision,
} from 'cca-api';

import { boolToString, isCCA2Scheme, isCCA3Scheme } from '../../utils';
import { AgreementCompositionTypePipe, AgreementTypeEnum, ApplicationReasonTypeEnum, CaNameEnum } from '../pipes';
import { FacilityWizardStep } from '../types';
import { addFacilityDecisionSummaryData } from './decision-summary-data';

const decimalPipe = new DecimalPipe('en-GB');
const datePipe = new GovukDatePipe();

type FacilitySummaryOptions = {
  changeName?: boolean;
  factory?: SummaryFactory;
  productsLink?: string;
};

type FacilitySummaryBuildContext = {
  facility: Facility;
  sectorSchemeData: SchemeData;
  schemeVersions: SchemeVersions;
  countries: Country[];
  attachments: Record<string, string>;
  isEditable: boolean;
  downloadUrl: string;
  opts: FacilitySummaryOptions;
};

type BaselineEnergySectionParams = {
  baselineEnergy: FacilityBaselineEnergyConsumption;
  carbonConversionFactorMeasurement: string;
  baselineYear: number | null;
  targetComposition: FacilityTargetComposition;
};

type ToFacilityWizardSummaryDataArgs = {
  facility: Facility;
  sectorSchemeData: SchemeData;
  schemeVersions: SchemeVersions;
  countries: Country[];
  attachments: Record<string, string>;
  isEditable: boolean;
  downloadUrl: string;
  opts?: { changeName?: boolean; productsLink?: string };
};

type ToFacilityWizardSummaryDataWithDecisionArgs = {
  facility: Facility;
  sectorSchemeData: SchemeData;
  schemeVersions: SchemeVersions;
  decision: UnderlyingAgreementFacilityReviewDecision | UnderlyingAgreementVariationFacilityReviewDecision;
  countries: Country[];
  attachments: { submit: Record<string, string>; review: Record<string, string> };
  isEditable: boolean;
  downloadUrl: string;
  opts?: { changeName?: boolean; productsLink?: string };
};

type ToFacilitySummaryDataWithStatusArgs = {
  facility: Facility;
  sectorSchemeData: SchemeData;
  schemeVersions: SchemeVersions;
  countries: Country[];
  attachments: Record<string, string>;
  isEditable: boolean;
  downloadUrl: string;
  opts?: { factory?: SummaryFactory; changeName?: boolean; productsLink?: string };
};

type ToFacilityWizardSummaryDataWithDecisionAndStatusArgs = {
  facility: Facility;
  sectorSchemeData: SchemeData;
  schemeVersions: SchemeVersions;
  decision: UnderlyingAgreementFacilityReviewDecision | UnderlyingAgreementVariationFacilityReviewDecision;
  countries: Country[];
  attachments: { submit: Record<string, string>; review: Record<string, string> };
  isEditable: boolean;
  downloadUrl: string;
  opts?: { factory?: SummaryFactory; changeName?: boolean; productsLink?: string };
};

function formatNumber(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') return '';

  const parsed = Number(value);
  return Number.isFinite(parsed) ? (decimalPipe.transform(parsed, '1.0-7') ?? '') : '';
}

function toSchemeParticipationString(schemeVersions: SchemeVersions): string {
  if (!schemeVersions || schemeVersions.length === 0) return '';
  return schemeVersions.length > 1 ? 'Both' : schemeVersions[0].replace('_', '');
}

function addStatusSection(factory: SummaryFactory, facility: Facility): SummaryFactory {
  const statusPipe = new StatusPipe();

  factory.addSection('').addRow('Facility status', statusPipe.transform(facility?.status) ?? '');

  if (facility?.excludedDate) {
    const datePipe = new GovukDatePipe();
    factory.addRow('Exclusion date', datePipe.transform(facility?.excludedDate));
  }

  return factory;
}

function addDetailsSection(factory: SummaryFactory, ctx: FacilitySummaryBuildContext): SummaryFactory {
  const { facility, schemeVersions, countries, isEditable, opts } = ctx;
  const facilityDetails = facility?.facilityDetails;

  factory
    .addSection('Facility Details', `../${FacilityWizardStep.DETAILS}`)
    .addRow('Facility ID', facility?.facilityId)
    .addRow('Site name', facilityDetails.name, { change: opts.changeName && isEditable })
    .addRow(
      'Are the activities carried out in this facility included in the UK ETS?',
      boolToString(facilityDetails?.isCoveredByUkets),
      { change: isEditable },
    )
    .addRow('UK ETS Installation Identifier', facilityDetails?.uketsId, { change: isEditable })
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
      { change: isEditable },
    );
  }

  factory
    .addRow('Scheme participation', toSchemeParticipationString(facilityDetails?.participatingSchemeVersions), {
      change: isEditable,
    })
    .addTextAreaRow('Facility address', transformAddress(facilityDetails?.facilityAddress, countries), {
      change: isEditable,
    });

  return factory;
}

function addContactDetailsSection(factory: SummaryFactory, ctx: FacilitySummaryBuildContext): SummaryFactory {
  const { facility, countries, isEditable } = ctx;
  const facilityContact: TargetUnitAccountContactDTO = facility?.facilityContact;

  return factory
    .addSection('Facility contact details', `../${FacilityWizardStep.CONTACT_DETAILS}`)
    .addRow('First name', facilityContact?.firstName, { change: isEditable })
    .addRow('Last name', facilityContact?.lastName, { change: isEditable })
    .addRow('Email address', facilityContact?.email, { change: isEditable })
    .addTextAreaRow('Contact address', transformAddress(facilityContact?.address, countries), { change: isEditable })
    .addRow('Phone number', transformPhoneNumber(facilityContact?.phoneNumber), { change: isEditable });
}

function addEligibilityDetailsSection(factory: SummaryFactory, ctx: FacilitySummaryBuildContext): SummaryFactory {
  const { facility, attachments, downloadUrl, isEditable } = ctx;
  const eligibility: EligibilityDetailsAndAuthorisation = facility?.eligibilityDetailsAndAuthorisation;

  return factory
    .addSection('CCA eligibility details and authorisation', `../${FacilityWizardStep.ELIGIBILITY_DETAILS}`)
    .addRow(
      'Is the facility adjacent to or connected to an existing CCA facility?',
      boolToString(eligibility?.isConnectedToExistingFacility),
      { change: isEditable },
    )
    .addRow('Facility ID of adjacent facility', eligibility?.adjacentFacilityId, { change: isEditable })
    .addRow('Agreement type - eligible under', AgreementTypeEnum[eligibility?.agreementType], { change: isEditable })
    .addRow(
      'Do you hold a current Environmental Permitting Regulations (EPR) authorisation for any activity being carried out in the facility?',
      boolToString(eligibility?.erpAuthorisationExists),
      { change: isEditable },
    )
    .addRow('Authorisation number', eligibility?.authorisationNumber, { change: isEditable })
    .addRow('Regulator name', CaNameEnum[eligibility?.regulatorName], { change: isEditable })
    .addFileListRow(
      'Attach a copy of the permit',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments([eligibility?.permitFile], attachments), downloadUrl),
      { change: isEditable },
    );
}

function addExtentSection(factory: SummaryFactory, ctx: FacilitySummaryBuildContext): SummaryFactory {
  const { facility, attachments, downloadUrl, isEditable } = ctx;
  const facilityExtent: FacilityExtent = facility?.facilityExtent;

  return factory
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
    );
}

function add70RuleSection(factory: SummaryFactory, ctx: FacilitySummaryBuildContext): SummaryFactory {
  const { facility, attachments, downloadUrl, isEditable } = ctx;
  const applyRule: Apply70Rule = facility?.apply70Rule;

  return factory
    .addSection('70% rule', `../${FacilityWizardStep.APPLY_RULE}`)
    .addRow(
      'Energy consumed in the installation',
      applyRule?.energyConsumed != null ? `${decimalPipe.transform(applyRule?.energyConsumed, '1.0-7')} %` : '',
      { change: isEditable },
    )
    .addRow(
      'Energy consumed in relation to 3/7ths provision',
      applyRule?.energyConsumedProvision
        ? `${decimalPipe.transform(applyRule?.energyConsumedProvision, '1.0-7')} %`
        : '',
      { change: isEditable },
    )
    .addRow(
      'Energy consumed in eligible facility',
      applyRule?.energyConsumedEligible != null
        ? `${decimalPipe.transform(applyRule?.energyConsumedEligible, '1.0-7')} %`
        : '',
      { change: isEditable },
    )
    .addRow('Sub-metered start date', applyRule?.startDate ? datePipe.transform(applyRule.startDate) : '', {
      change: isEditable,
    })
    .addFileListRow(
      'Evidence',
      fileUtils.toDownloadableFiles(fileUtils.extractAttachments([applyRule?.evidenceFile], attachments), downloadUrl),
      { change: isEditable },
    );
}

function addTargetCompositionSection(
  factory: SummaryFactory,
  targetComposition: FacilityTargetComposition,
  ctx: FacilitySummaryBuildContext,
): SummaryFactory {
  const { sectorSchemeData, attachments, downloadUrl, isEditable } = ctx;
  const measurementTypeToOptionTextPipe = new MeasurementTypeToOptionTextPipe();
  const agreementTypePipe = new AgreementCompositionTypePipe();

  return factory
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
      { change: isEditable, appendChangeParam: true },
    )
    .addRow(
      'Energy or carbon units used by the facility',
      measurementTypeToOptionTextPipe.transform(targetComposition?.measurementType),
      { change: isEditable, appendChangeParam: true },
    )
    .addRow(
      'Target type for agreement composition',
      agreementTypePipe.transform(targetComposition?.agreementCompositionType),
    );
}

function addBaselineDataSection(
  factory: SummaryFactory,
  baselineData: FacilityBaselineData,
  ctx: FacilitySummaryBuildContext,
): SummaryFactory {
  const { attachments, downloadUrl, isEditable } = ctx;

  factory
    .addSection('Details of baseline data', `../${FacilityWizardStep.BASELINE_DATA}`)
    .addRow(
      'Is at least 12 months of consecutive baseline data available?',
      boolToString(baselineData?.isTwelveMonths),
      { change: isEditable, appendChangeParam: true },
    )
    .addRow(
      baselineData?.isTwelveMonths === true
        ? 'Start date of baseline'
        : 'Enter the date that 12 months of data will be available.',
      datePipe.transform(baselineData?.baselineDate),
      { change: isEditable, appendChangeParam: true },
    )
    .addTextAreaRow(
      baselineData?.isTwelveMonths === true
        ? 'Explain why you are using a different baseline year'
        : 'Explain how the target unit fits the greenfield criteria',
      baselineData?.explanation,
      { change: isEditable, appendChangeParam: true },
    );

  if (baselineData?.isTwelveMonths === false) {
    factory.addFileListRow(
      'Evidence',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(baselineData?.greenfieldEvidences, attachments),
        downloadUrl,
      ),
      { change: isEditable, appendChangeParam: true },
    );
  }

  factory
    .addRow(
      'Must the Special Reporting Methodology be applied for this facility?',
      boolToString(baselineData?.usedReportingMechanism),
      { change: isEditable, appendChangeParam: true },
    )
    .addRow(
      `Baseline energy to ${
        baselineData?.carbonConversionFactorMeasurement === 'kgCe/kWh' ? 'carbon' : 'carbon dioxide'
      } conversion factor (${baselineData?.carbonConversionFactorMeasurement})`,
      decimalPipe.transform(baselineData?.energyCarbonFactor, '1.0-7'),
      { change: isEditable, appendChangeParam: true },
    );

  return factory;
}

function addBaselineEnergySection(
  factory: SummaryFactory,
  params: BaselineEnergySectionParams,
  ctx: FacilitySummaryBuildContext,
): SummaryFactory {
  const { isEditable, opts } = ctx;
  const { baselineEnergy, carbonConversionFactorMeasurement, baselineYear, targetComposition } = params;
  const measurementTypeToUnitPipe = new MeasurementTypeToUnitPipe();

  const baselineEnergyProducts = baselineEnergy?.variableEnergyConsumptionDataByProduct ?? [];
  const includedBaselineEnergyProducts = baselineEnergyProducts.filter(
    (product) => (product.productStatus ?? '').toUpperCase() !== 'EXCLUDED',
  );

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

  factory
    .addSection('Details of baseline energy or carbon', `../${FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION}`)
    .addRow(
      `Fixed baseline energy for the facility (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
      formatNumber(fixedEnergyValue),
      { change: isEditable, appendChangeParam: true },
    );

  factory.addRow('Is there a variable energy amount?', boolToString(baselineEnergy?.hasVariableEnergy), {
    change: isEditable,
    appendChangeParam: true,
  });

  if (baselineEnergy?.hasVariableEnergy) {
    factory.addRow(
      `Indicate how you want to account for the portion of variable energy used (or ${
        carbonConversionFactorMeasurement === 'kgCe/kWh' ? 'carbon' : 'carbon dioxide'
      } emitted) for your facility`,
      baselineEnergy.variableEnergyType === 'TOTALS'
        ? 'Totals only'
        : baselineEnergy.variableEnergyType === 'BY_PRODUCT'
          ? 'Split by product'
          : '',
      { change: isEditable, appendChangeParam: true },
    );

    if (baselineEnergy.variableEnergyType === 'TOTALS') {
      factory.addRow(
        `Total baseline variable energy (${measurementTypeToUnitPipe.transform(targetComposition?.measurementType)})`,
        formatNumber(baselineEnergy.baselineVariableEnergy),
        { change: isEditable, appendChangeParam: true },
      );
    }

    if (baselineEnergy.variableEnergyType === 'BY_PRODUCT') {
      const productCount = includedBaselineEnergyProducts.length;
      const productLabel =
        productCount === 0 ? 'No products added' : `${productCount} ${productCount === 1 ? 'Product' : 'Products'}`;

      factory.addRow('Products submitted', productLabel, {
        change: isEditable,
        appendChangeParam: true,
        link: `${opts.productsLink ?? '../products'}`,
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
      .addRow('Throughput unit', baselineEnergy.throughputUnit ?? '', { change: isEditable, appendChangeParam: true });
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

  return factory;
}

function addTargetsSection(
  factory: SummaryFactory,
  facilityTargets: FacilityTargets,
  ctx: FacilitySummaryBuildContext,
): SummaryFactory {
  const { isEditable } = ctx;

  return factory
    .addSection('Targets', `../${FacilityWizardStep.TARGETS}`)
    .addRow(
      'TP7 (2026) improvement (%)',
      decimalPipe.transform(facilityTargets?.improvements[Improvement.TP7], '1.0-7'),
      { change: isEditable, appendChangeParam: true },
    )
    .addRow(
      'TP8 (2027 to 2028) improvement (%)',
      decimalPipe.transform(facilityTargets?.improvements[Improvement.TP8], '1.0-7'),
      { change: isEditable, appendChangeParam: true },
    )
    .addRow(
      'TP9 (2029 to 2030) improvement (%)',
      decimalPipe.transform(facilityTargets?.improvements[Improvement.TP9], '1.0-7'),
      { change: isEditable, appendChangeParam: true },
    );
}

function toFacilityWizardSummaryFactory(ctx: FacilitySummaryBuildContext): SummaryFactory {
  const { facility, schemeVersions, opts } = ctx;

  const targetComposition = facility?.cca3BaselineAndTargets?.targetComposition;
  const baselineData = facility?.cca3BaselineAndTargets?.baselineData;
  const carbonConversionFactorMeasurement =
    facility?.cca3BaselineAndTargets?.baselineData?.carbonConversionFactorMeasurement;

  const baselineYear = baselineData?.baselineDate ? new Date(baselineData.baselineDate).getFullYear() : null;

  let factory = opts?.factory ?? new SummaryFactory();

  factory = addDetailsSection(factory, ctx);
  factory = addContactDetailsSection(factory, ctx);
  factory = addEligibilityDetailsSection(factory, ctx);
  factory = addExtentSection(factory, ctx);
  factory = add70RuleSection(factory, ctx);

  if (isCCA3Scheme(schemeVersions) && facility?.cca3BaselineAndTargets) {
    factory = addTargetCompositionSection(factory, targetComposition, ctx);
    factory = addBaselineDataSection(factory, baselineData, ctx);

    factory = addBaselineEnergySection(
      factory,
      {
        baselineEnergy: facility?.cca3BaselineAndTargets?.facilityBaselineEnergyConsumption,
        carbonConversionFactorMeasurement,
        baselineYear,
        targetComposition,
      },
      ctx,
    );

    factory = addTargetsSection(factory, facility?.cca3BaselineAndTargets?.facilityTargets, ctx);
  }

  return factory;
}

export function toFacilityWizardSummaryData(args: ToFacilityWizardSummaryDataArgs): SummaryData {
  return toFacilityWizardSummaryFactory({
    facility: args.facility,
    sectorSchemeData: args.sectorSchemeData,
    schemeVersions: args.schemeVersions,
    countries: args.countries,
    attachments: args.attachments,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
    opts: args.opts ?? {},
  }).create();
}

export function toFacilityWizardSummaryDataWithDecision(
  args: ToFacilityWizardSummaryDataWithDecisionArgs,
): SummaryData {
  const factory = toFacilityWizardSummaryFactory({
    facility: args.facility,
    sectorSchemeData: args.sectorSchemeData,
    schemeVersions: args.schemeVersions,
    countries: args.countries,
    attachments: args.attachments.submit,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
    opts: { changeName: args.opts?.changeName ?? true, productsLink: args.opts?.productsLink },
  });

  if (!args.decision?.type) return factory.create();

  return addFacilityDecisionSummaryData({
    factory,
    decision: args.decision,
    attachments: args.attachments.review,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
  }).create();
}

export function toFacilitySummaryDataWithStatus(args: ToFacilitySummaryDataWithStatusArgs): SummaryData {
  const opts = args.opts ?? {};
  let factory = new SummaryFactory();

  factory = addStatusSection(factory, args.facility);

  factory = toFacilityWizardSummaryFactory({
    facility: args.facility,
    sectorSchemeData: args.sectorSchemeData,
    schemeVersions: args.schemeVersions,
    countries: args.countries,
    attachments: args.attachments,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
    opts: { ...opts, factory, productsLink: opts.productsLink },
  });

  return factory.create();
}

export function toFacilityWizardSummaryDataWithDecisionAndStatus(
  args: ToFacilityWizardSummaryDataWithDecisionAndStatusArgs,
): SummaryData {
  const opts = args.opts ?? {};
  let factory = new SummaryFactory();

  factory = addStatusSection(factory, args.facility);

  factory = toFacilityWizardSummaryFactory({
    facility: args.facility,
    sectorSchemeData: args.sectorSchemeData,
    schemeVersions: args.schemeVersions,
    countries: args.countries,
    attachments: args.attachments.submit,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
    opts: { factory, changeName: opts.changeName ?? true, productsLink: opts.productsLink },
  });

  if (!args.decision?.type) return factory.create();

  return addFacilityDecisionSummaryData({
    factory,
    decision: args.decision,
    attachments: args.attachments.review,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
  }).create();
}
