import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe } from '@shared/pipes';
import { getAddressAsArray } from '@shared/utils';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../types';

export function toReviewTargetUnitDetailsSummaryData(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', targetUnitDetails?.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(targetUnitDetails.operatorType), {
      change: isEditable,
    })
    .addRow('Company number', targetUnitDetails.companyRegistrationNumber ?? 'Not provided', {
      change: isEditable,
    })
    .addRow('Reason for not having a registration number', targetUnitDetails.registrationNumberMissingReason, {
      change: isEditable,
    });

  if (targetUnitDetails.subsectorAssociationName) {
    factory.addRow('Subsector', targetUnitDetails.subsectorAssociationName, {
      change: isEditable,
    });
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep?.OPERATOR_ADDRESS)
    .addRow('Address', getAddressAsArray(targetUnitDetails?.operatorAddress), {
      change: isEditable,
    })

    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep?.RESPONSIBLE_PERSON)
    .addRow('First name', targetUnitDetails?.responsiblePersonDetails?.firstName, {
      change: isEditable,
    })
    .addRow('Last name', targetUnitDetails?.responsiblePersonDetails?.lastName, {
      change: isEditable,
    })
    .addRow('Email address', targetUnitDetails?.responsiblePersonDetails?.email, {
      change: isEditable,
    })
    .addRow('Address', getAddressAsArray(targetUnitDetails?.responsiblePersonDetails?.address), {
      change: isEditable,
    });

  return factory.create();
}
