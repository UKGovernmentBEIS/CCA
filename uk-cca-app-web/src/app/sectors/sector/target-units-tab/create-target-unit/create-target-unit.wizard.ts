import { TargetUnitAccountPayload } from 'cca-api';

export const isWizardCompleted = (accountPayload: TargetUnitAccountPayload, subSectorsExist: boolean): boolean => {
  const isOperatorAddressCompleted = !!accountPayload.address;
  const isResponsiblePersonCompleted = !!accountPayload.responsiblePerson && !!accountPayload.responsiblePerson.address;
  const isAdministrativeContactCompleted =
    !!accountPayload.administrativeContactDetails && !!accountPayload.administrativeContactDetails.address;

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
