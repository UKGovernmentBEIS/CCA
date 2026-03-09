import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe, transformAddress } from '@shared/pipes';
import { equalAddressFields, equalFields } from '@shared/utils';

import { AccountReferenceData, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { CompaniesHouseState } from '../companies-house-details';
import { ReviewTargetUnitDetailsWizardStep } from '../types';

export function toReviewTargetUnitDetailsSummaryData(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  companiesHouseState?: CompaniesHouseState,
  toggleCompaniesHouseDetails?: boolean,
  prefix = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS)
    .addRow('Operator name', targetUnitDetails?.operatorName, {
      change: isEditable,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalFields(targetUnitDetails?.operatorName, companiesHouseState?.details?.name) === false,
    })
    .addRow('Operator type', operatorTypePipe.transform(targetUnitDetails.operatorType), {
      change: isEditable,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalFields(targetUnitDetails?.operatorType, companiesHouseState?.details?.operatorType) === false,
    })
    .addRow('Company number', targetUnitDetails.companyRegistrationNumber ?? 'Not provided', {
      change: isEditable,
      changeLink: prefix + ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalFields(targetUnitDetails?.companyRegistrationNumber, companiesHouseState?.details?.registrationNumber) ===
          false,
    })
    .addRow('Reason for not having a registration number', targetUnitDetails?.registrationNumberMissingReason, {
      change: isEditable,
      changeLink: prefix + ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
    });

  if (targetUnitDetails?.subsectorAssociationName) {
    factory.addRow('Subsector', targetUnitDetails?.subsectorAssociationName, {
      change: isEditable,
    });
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS)
    .addRow('Address', transformAddress(targetUnitDetails?.operatorAddress), {
      change: isEditable,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalAddressFields(
          transformAddress(targetUnitDetails?.operatorAddress),
          transformAddress(companiesHouseState?.details?.address),
        ) === false,
    })

    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON)
    .addRow('First name', targetUnitDetails?.responsiblePersonDetails?.firstName, {
      change: isEditable,
    })
    .addRow('Last name', targetUnitDetails?.responsiblePersonDetails?.lastName, {
      change: isEditable,
    })
    .addRow('Email address', targetUnitDetails?.responsiblePersonDetails?.email, {
      change: isEditable,
    })
    .addRow('Address', transformAddress(targetUnitDetails?.responsiblePersonDetails?.address), {
      change: isEditable,
    });

  return factory.create();
}

export function toReviewTargetUnitDetailsSummaryDataOriginal(
  accountReferenceData: AccountReferenceData,
  isEditable: boolean,
  companiesHouseState?: CompaniesHouseState,
  toggleCompaniesHouseDetails?: boolean,
  prefix = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS)
    .addRow('Operator name', accountReferenceData?.targetUnitAccountDetails?.operatorName, {
      change: isEditable,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalFields(
          accountReferenceData?.targetUnitAccountDetails?.operatorName,
          companiesHouseState?.details?.name,
        ) === false,
    })
    .addRow('Operator type', operatorTypePipe.transform(accountReferenceData?.targetUnitAccountDetails.operatorType), {
      change: isEditable,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalFields(
          accountReferenceData?.targetUnitAccountDetails?.operatorType,
          companiesHouseState?.details?.operatorType,
        ) === false,
    })
    .addRow(
      'Company number',
      accountReferenceData?.targetUnitAccountDetails?.companyRegistrationNumber ?? 'Not provided',
      {
        change: isEditable,
        changeLink: prefix + ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
        fieldDiff:
          toggleCompaniesHouseDetails &&
          equalFields(
            accountReferenceData?.targetUnitAccountDetails?.companyRegistrationNumber,
            companiesHouseState?.details?.registrationNumber,
          ) === false,
      },
    )
    .addRow(
      'Reason for not having a registration number',
      accountReferenceData?.targetUnitAccountDetails?.registrationNumberMissingReason,
      {
        change: isEditable,
        changeLink: prefix + ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
      },
    );

  if (accountReferenceData?.sectorAssociationDetails?.subsectorAssociationName) {
    factory.addRow('Subsector', accountReferenceData?.sectorAssociationDetails?.subsectorAssociationName, {
      change: isEditable,
    });
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS)
    .addRow('Address', transformAddress(accountReferenceData?.targetUnitAccountDetails?.address), {
      change: isEditable,
      fieldDiff:
        toggleCompaniesHouseDetails &&
        equalAddressFields(
          transformAddress(accountReferenceData?.targetUnitAccountDetails?.address),
          transformAddress(companiesHouseState?.details?.address),
        ) === false,
    })

    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON)
    .addRow('First name', accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.firstName, {
      change: isEditable,
    })
    .addRow('Last name', accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.lastName, {
      change: isEditable,
    })
    .addRow('Email address', accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.email, {
      change: isEditable,
    })
    .addRow('Address', transformAddress(accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.address), {
      change: isEditable,
    });

  return factory.create();
}
