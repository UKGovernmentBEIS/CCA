import { DecimalPipe } from '@angular/common';

import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryData, SummaryFactory } from '@shared/components';
import { FacilityStatusEnum } from '@shared/pipes';
import { transformAttachmentsAndFileUUIDsToDownloadableFiles, transformPhoneNumber } from '@shared/utils';
import { getAddressAsArray } from '@shared/utils';

import {
  Facility,
  UnderlyingAgreementFacilityReviewDecision,
  UnderlyingAgreementVariationFacilityReviewDecision,
} from 'cca-api';

import { AgreementTypeEnum, ApplicationReasonTypeEnum, CaNameEnum } from '../pipes';
import { FacilityWizardStep } from '../underlying-agreement.types';
import { boolToString } from '../utils';
import { addFacilityDecisionSummaryData } from './decision-summary-data';

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
    .addRow('Site name', facility?.facilityDetails.name, { change: opts.changeName && isEditable })
    .addRow('Facility code', facility?.facilityId)
    .addRow('Is this facility covered by UK ETS?', boolToString(facilityDetails?.isCoveredByUkets), {
      change: isEditable,
    })
    .addRow('UK ETS Installation Identifier', facilityDetails?.uketsId, {
      change: isEditable,
    })
    .addRow('Application reason', ApplicationReasonTypeEnum[facilityDetails?.applicationReason], {
      change: isEditable && facility?.status === 'NEW',
    })
    .addRow('Previous facility ID', facilityDetails?.previousFacilityId, {
      change: isEditable && facility?.status === 'NEW',
    })
    .addRow('Facility address', getAddressAsArray(facilityDetails?.facilityAddress), {
      change: isEditable,
      prewrap: true,
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
    .addRow('Contact address', getAddressAsArray(facilityContact?.address), {
      change: isEditable,
      prewrap: true,
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
      transformAttachmentsAndFileUUIDsToDownloadableFiles([eligibility?.permitFile], attachments, downloadUrl),
      { change: isEditable },
    )

    .addSection('Extent of the facility', `../${FacilityWizardStep.EXTENT}`)
    .addFileListRow(
      'Manufacturing process description',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        [facilityExtent?.manufacturingProcessFile],
        attachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Process flow maps',
      transformAttachmentsAndFileUUIDsToDownloadableFiles([facilityExtent?.processFlowFile], attachments, downloadUrl),
      { change: isEditable },
    )
    .addFileListRow(
      'Annotated site plans',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        [facilityExtent?.annotatedSitePlansFile],
        attachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Eligible process description',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        [facilityExtent?.eligibleProcessFile],
        attachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addRow('Are any directly associated activities claimed?', boolToString(facilityExtent?.areActivitiesClaimed), {
      change: isEditable,
    })
    .addFileListRow(
      'Directly associated activities description',
      transformAttachmentsAndFileUUIDsToDownloadableFiles(
        [facilityExtent?.activitiesDescriptionFile],
        attachments,
        downloadUrl,
      ),
      { change: isEditable },
    )

    .addSection('Apply the 70% rule', `../${FacilityWizardStep.APPLY_RULE}`)
    .addRow(
      'Energy consumed in the installation',
      applyRule?.energyConsumed != null ? pipe.transform(applyRule?.energyConsumed, '1.0-7') + ' %' : '',
      {
        change: isEditable,
      },
    )
    .addRow(
      'Energy consumed in relation to 3/7ths provision',
      applyRule?.energyConsumedProvision ? pipe.transform(applyRule?.energyConsumedProvision, '1.0-7') + ' %' : '',
      {
        change: isEditable,
      },
    )
    .addRow(
      'Energy consumed in eligible facility',
      applyRule?.energyConsumedEligible != null
        ? pipe.transform(applyRule?.energyConsumedEligible, '1.0-7') + ' %'
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
      transformAttachmentsAndFileUUIDsToDownloadableFiles([applyRule?.evidenceFile], attachments, downloadUrl),
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
  const factory = new SummaryFactory()
    .addSection('')
    .addRow('Facility status', FacilityStatusEnum[facility?.status] ?? '');

  if (facility?.excludedDate) {
    const datePipe = new GovukDatePipe();
    factory.addRow('Exclusion date', datePipe.transform(facility?.excludedDate));
  }

  return facilitySummaryFactory(facility, attachments, isEditable, downloadUrl, { ...opts, factory: factory }).create();
}

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
  let factory = new SummaryFactory().addSection('').addRow('Facility status', FacilityStatusEnum[facility.status]);

  if (facility.excludedDate) {
    const datePipe = new GovukDatePipe();
    factory.addRow('Exclusion date', datePipe.transform(facility.excludedDate));
  }

  factory = facilitySummaryFactory(facility, attachments.submit, isEditable, downloadUrl, { factory: factory });
  return addFacilityDecisionSummaryData(factory, decision, attachments.review, isEditable, downloadUrl).create();
}
