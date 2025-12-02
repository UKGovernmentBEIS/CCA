import { isAddressCompleted } from '@shared/utils';

import { TargetUnitAccountPayload } from 'cca-api';

export const isWizardCompleted = (accountPayload: TargetUnitAccountPayload, subSectorsExist: boolean): boolean => {
  const isOperatorAddressCompleted = isAddressCompleted(accountPayload.address);

  const isResponsiblePersonCompleted =
    accountPayload.responsiblePerson?.email &&
    accountPayload.responsiblePerson?.firstName &&
    accountPayload.responsiblePerson?.lastName &&
    isAddressCompleted(accountPayload.responsiblePerson?.address);

  const isAdministrativeContactCompleted =
    accountPayload.administrativeContactDetails?.email &&
    accountPayload.administrativeContactDetails?.firstName &&
    accountPayload.administrativeContactDetails?.lastName &&
    isAddressCompleted(accountPayload.administrativeContactDetails?.address);

  return (
    isTargetUnitDetailsCompleted(accountPayload, subSectorsExist) &&
    isOperatorAddressCompleted &&
    isResponsiblePersonCompleted &&
    isAdministrativeContactCompleted
  );
};

export const isTargetUnitDetailsCompleted = (
  accountPayload: TargetUnitAccountPayload,
  subSectorsExist: boolean,
): boolean => {
  return subSectorsExist
    ? !!accountPayload.name &&
        !!accountPayload.operatorType &&
        (!!accountPayload.companyRegistrationNumber || !!accountPayload.registrationNumberMissingReason) &&
        !!accountPayload.subsectorAssociationId
    : !!accountPayload.name &&
        !!accountPayload.operatorType &&
        (!!accountPayload.companyRegistrationNumber || !!accountPayload.registrationNumberMissingReason);
};
