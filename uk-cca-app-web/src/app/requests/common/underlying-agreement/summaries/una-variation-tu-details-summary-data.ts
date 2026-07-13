import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe, transformAddress } from '@shared/pipes';
import { Country } from '@shared/types';

import { AccountAddressDTO, AccountReferenceData, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../types';

type ToVariationTargetUnitDetailsSummaryDataArgs = {
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails;
  countries: Country[];
  isEditable: boolean;
  prefix?: string;
};

type ToVariationTargetUnitDetailsOriginalSummaryDataArgs = {
  accountReferenceData: AccountReferenceData;
  countries: Country[];
  isEditable: boolean;
  prefix?: string;
};

type TargetUnitSummaryData = {
  operatorName: string;
  operatorType: 'LIMITED_COMPANY' | 'PARTNERSHIP' | 'SOLE_TRADER' | 'NONE';
  companyRegistrationNumber?: string;
  registrationNumberMissingReason?: string;
  subsectorAssociationName?: string;
  operatorAddress: AccountAddressDTO;
  responsiblePerson: {
    firstName: string;
    lastName: string;
    email: string;
    address: AccountAddressDTO;
  };
};

function toSummaryData(
  data: TargetUnitSummaryData,
  countries: Country[],
  isEditable: boolean,
  prefix = '../',
): SummaryFactory {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS)
    .addRow('Operator name', data.operatorName, { change: isEditable })
    .addRow('Operator type', operatorTypePipe.transform(data.operatorType))
    .addRow('Company number', data.companyRegistrationNumber ?? 'Not provided')
    .addRow('Reason for not having a registration number', data.registrationNumberMissingReason);

  if (data.subsectorAssociationName) {
    factory.addRow('Subsector', data.subsectorAssociationName);
  }

  return factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS)
    .addRow('Address', transformAddress(data.operatorAddress, countries), { change: isEditable })
    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON)
    .addRow('First name', data.responsiblePerson.firstName, { change: isEditable })
    .addRow('Last name', data.responsiblePerson.lastName, { change: isEditable })
    .addRow('Email address', data.responsiblePerson.email, { change: isEditable })
    .addRow('Address', transformAddress(data.responsiblePerson.address, countries), { change: isEditable });
}

export function toVariationTargetUnitDetailsSummaryData(
  args: ToVariationTargetUnitDetailsSummaryDataArgs,
): SummaryData {
  return toSummaryData(
    {
      operatorName: args.targetUnitDetails?.operatorName,
      operatorType: args.targetUnitDetails?.operatorType,
      companyRegistrationNumber: args.targetUnitDetails?.companyRegistrationNumber,
      registrationNumberMissingReason: args.targetUnitDetails?.registrationNumberMissingReason,
      subsectorAssociationName: args.targetUnitDetails?.subsectorAssociationName,
      operatorAddress: args.targetUnitDetails?.operatorAddress,
      responsiblePerson: {
        firstName: args.targetUnitDetails?.responsiblePersonDetails?.firstName,
        lastName: args.targetUnitDetails?.responsiblePersonDetails?.lastName,
        email: args.targetUnitDetails?.responsiblePersonDetails?.email,
        address: args.targetUnitDetails?.responsiblePersonDetails?.address,
      },
    },
    args.countries,
    args.isEditable,
    args.prefix ?? '../',
  ).create();
}

export function toVariationTargetUnitDetailsOriginalSummaryData(
  args: ToVariationTargetUnitDetailsOriginalSummaryDataArgs,
): SummaryData {
  const { accountReferenceData } = args;

  return toSummaryData(
    {
      operatorName: accountReferenceData.targetUnitAccountDetails.operatorName,
      operatorType: accountReferenceData.targetUnitAccountDetails.operatorType,
      companyRegistrationNumber: accountReferenceData.targetUnitAccountDetails.companyRegistrationNumber,
      registrationNumberMissingReason: accountReferenceData.targetUnitAccountDetails.registrationNumberMissingReason,
      subsectorAssociationName: accountReferenceData.sectorAssociationDetails.subsectorAssociationName,
      operatorAddress: accountReferenceData.targetUnitAccountDetails.address,
      responsiblePerson: {
        firstName: accountReferenceData.targetUnitAccountDetails.responsiblePerson.firstName,
        lastName: accountReferenceData.targetUnitAccountDetails.responsiblePerson.lastName,
        email: accountReferenceData.targetUnitAccountDetails.responsiblePerson.email,
        address: accountReferenceData.targetUnitAccountDetails.responsiblePerson.address,
      },
    },
    args.countries,
    args.isEditable,
    args.prefix ?? '../',
  ).create();
}
