import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { StatusPipe } from '@shared/pipes';
import { Improvement, SchemeVersions } from '@shared/types';
import { fileUtils, transformPhoneNumber } from '@shared/utils';
import { getAddressAsArray } from '@shared/utils';

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

export function toFacilityWizardSummaryDataWithDecision(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  decision: UnderlyingAgreementFacilityReviewDecision | UnderlyingAgreementVariationFacilityReviewDecision,
  attachments: { submit: Record<string, string>; review: Record<string, string> },
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const factory = toFacilityWizardSummary(
    facility,
    sectorSchemeData,
    schemeVersions,
    attachments.submit,
    isEditable,
    downloadUrl,
    { changeName: true },
  );

  return addFacilityDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
}

export function toFacilityWizardSummaryData(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean } = {},
): SummaryData {
  return toFacilityWizardSummary(
    facility,
    sectorSchemeData,
    schemeVersions,
    attachments,
    isEditable,
    downloadUrl,
    opts,
  ).create();
}

function toFacilityWizardSummary(
  facility: Facility,
  sectorSchemeData: SchemeData,
  schemeVersions: SchemeVersions,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean; factory?: SummaryFactory } = {},
): SummaryFactory {
  const facilityDetails = facility?.facilityDetails;
  const facilityContact = facility?.facilityContact;
  const eligibility = facility?.eligibilityDetailsAndAuthorisation;
  const facilityExtent = facility?.facilityExtent;
  const applyRule = facility?.apply70Rule;

  const decimalPipe = new DecimalPipe('en-GB');
  const datePipe = new GovukDatePipe();
  const measurementTypeToOptionTextPipe = new MeasurementTypeToOptionTextPipe();
  const agreementTypePipe = new AgreementCompositionTypePipe();
  const measurementTypeToUnit = new MeasurementTypeToUnitPipe();

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
    .addTextAreaRow('Facility address', getAddressAsArray(facilityDetails?.facilityAddress), {
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
    .addTextAreaRow('Contact address', getAddressAsArray(facilityContact?.address), {
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

  if (isCCA3Scheme(schemeVersions)) {
    // TARGET COMPOSITION SECTION
    factory
      .addSection('Target composition', `../${FacilityWizardStep.TARGET_COMPOSITION}`)
      .addFileListRow(
        'Target calculator file',
        fileUtils.toDownloadableFiles(
          fileUtils.extractAttachments(
            [facility?.cca3BaselineAndTargets?.targetComposition?.calculatorFile],
            attachments,
          ),
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
        measurementTypeToOptionTextPipe.transform(facility?.cca3BaselineAndTargets?.targetComposition?.measurementType),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'Target type for agreement composition',
        agreementTypePipe.transform(facility?.cca3BaselineAndTargets?.targetComposition?.agreementCompositionType),
      )

      // BASELINE DATA SECTION
      .addSection('Details of baseline data', `../${FacilityWizardStep.BASELINE_DATA}`)
      .addRow(
        'Is at least 12 months of consecutive baseline data available?',
        boolToString(facility?.cca3BaselineAndTargets?.baselineData?.isTwelveMonths),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        facility?.cca3BaselineAndTargets?.baselineData?.isTwelveMonths === true
          ? 'Start date of baseline'
          : 'Enter the date that 12 months of data will be available.',
        datePipe.transform(facility?.cca3BaselineAndTargets?.baselineData?.baselineDate),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addTextAreaRow(
        facility?.cca3BaselineAndTargets?.baselineData?.isTwelveMonths === true
          ? 'Explain why you are using a different baseline year'
          : 'Explain how the target unit fits the greenfield criteria',
        facility?.cca3BaselineAndTargets?.baselineData?.explanation,
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );

    if (facility?.cca3BaselineAndTargets?.baselineData?.isTwelveMonths === false) {
      factory.addFileListRow(
        'Evidence',
        fileUtils.toDownloadableFiles(
          fileUtils.extractAttachments(
            facility?.cca3BaselineAndTargets?.baselineData?.greenfieldEvidences,
            attachments,
          ),
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
        `Baseline ${measurementTypeToUnit.transform(facility?.cca3BaselineAndTargets?.targetComposition.measurementType)} for the target facility`,
        decimalPipe.transform(facility?.cca3BaselineAndTargets?.baselineData?.energy, '1.0-7'),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'Have you used the special reporting mechanism to adjust the baseline throughput for any of the facilities in the target unit using combined heat and power (CHP)?',
        boolToString(facility?.cca3BaselineAndTargets?.baselineData?.usedReportingMechanism),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )

      // TARGETS SECTION
      .addSection('Targets', `../${FacilityWizardStep.TARGETS}`)
      .addRow(
        'TP7 (2026) improvement (%)',
        decimalPipe.transform(
          facility?.cca3BaselineAndTargets?.facilityTargets?.improvements[Improvement.TP7],
          '1.0-7',
        ),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'TP8 (2027 to 2028) improvement (%)',
        decimalPipe.transform(
          facility?.cca3BaselineAndTargets?.facilityTargets?.improvements[Improvement.TP8],
          '1.0-7',
        ),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      )
      .addRow(
        'TP9 (2029 to 2030) improvement (%)',
        decimalPipe.transform(
          facility?.cca3BaselineAndTargets?.facilityTargets?.improvements[Improvement.TP9],
          '1.0-7',
        ),
        {
          change: isEditable,
          appendChangeParam: true,
        },
      );
  }

  return factory;
}

// TODO: this will be refactored in the future, once the facility wizard is fully migrated to the new design
function facilitySummaryFactory(
  facility: Facility,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean; factory?: SummaryFactory } = {},
): SummaryFactory {
  const facilityDetails = facility?.facilityDetails;
  const facilityContact = facility?.facilityContact;
  const eligibility = facility?.eligibilityDetailsAndAuthorisation;
  const facilityExtent = facility?.facilityExtent;
  const applyRule = facility?.apply70Rule;

  const pipe = new DecimalPipe('en-GB');
  const datePipe = new GovukDatePipe();

  const factory = opts?.factory ?? new SummaryFactory();

  return factory
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
    })
    .addTextAreaRow('Facility address', getAddressAsArray(facilityDetails?.facilityAddress), {
      change: isEditable,
    })

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
    .addTextAreaRow('Contact address', getAddressAsArray(facilityContact?.address), {
      change: isEditable,
    })
    .addRow('Phone number', transformPhoneNumber(facilityContact?.phoneNumber), {
      change: isEditable,
    })

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

    .addSection('Apply the 70% rule', `../${FacilityWizardStep.APPLY_RULE}`)
    .addRow(
      'Energy consumed in the installation',
      applyRule?.energyConsumed != null ? `${pipe.transform(applyRule?.energyConsumed, '1.0-7')} %` : '',
      {
        change: isEditable,
      },
    )
    .addRow(
      'Energy consumed in relation to 3/7ths provision',
      applyRule?.energyConsumedProvision ? `${pipe.transform(applyRule?.energyConsumedProvision, '1.0-7')} %` : '',
      {
        change: isEditable,
      },
    )
    .addRow(
      'Energy consumed in eligible facility',
      applyRule?.energyConsumedEligible != null
        ? `${pipe.transform(applyRule?.energyConsumedEligible, '1.0-7')} %`
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
}

export function toFacilitySummaryData(
  facility: Facility,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  opts: { changeName?: boolean } = {},
): SummaryData {
  return facilitySummaryFactory(facility, attachments, isEditable, downloadUrl, opts).create();
}

export function toFacilitySummaryDataWithStatus(
  facility: Facility,
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

  return facilitySummaryFactory(facility, attachments, isEditable, downloadUrl, { ...opts, factory: factory }).create();
}

// TODO: this will be refactored in the future, once the facility wizard is fully migrated to the new design
export function toFacilitySummaryDataWithDecision(
  facility: Facility,
  decision: UnderlyingAgreementFacilityReviewDecision | UnderlyingAgreementVariationFacilityReviewDecision,
  attachments: { submit: Record<string, string>; review: Record<string, string> },
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const factory = facilitySummaryFactory(facility, attachments.submit, isEditable, downloadUrl, { changeName: true });
  return addFacilityDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
}

export function toFacilitySummaryDataWithStatusAndDecision(
  facility: Facility,
  decision: UnderlyingAgreementFacilityReviewDecision | UnderlyingAgreementVariationFacilityReviewDecision,
  attachments: { submit: Record<string, string>; review: Record<string, string> },
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const statusPipe = new StatusPipe();

  let factory = new SummaryFactory().addSection('').addRow('Facility status', statusPipe.transform(facility?.status));

  if (facility.excludedDate) {
    const datePipe = new GovukDatePipe();
    factory.addRow('Exclusion date', datePipe.transform(facility.excludedDate));
  }

  factory = facilitySummaryFactory(facility, attachments.submit, isEditable, downloadUrl, { factory: factory });
  return addFacilityDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
}

function toSchemeParticipationString(schemeVersions: SchemeVersions): string {
  if (!schemeVersions || schemeVersions.length === 0) return '';
  return schemeVersions.length > 1 ? 'Both' : schemeVersions[0].replace('_', '');
}
