import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe, transformAddress } from '@shared/pipes';
import { equalAddressFields, equalFields } from '@shared/utils';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

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
