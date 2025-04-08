import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { financialIndependenceStatusTypeMap, operatorTypeMap } from '@shared/pipes';

import { TargetUnitAccountDetailsResponseDTO } from 'cca-api';

import { getAddressAsArray } from '../../address';
import { transformFileInfoToDownloadableFile } from '../../file-attachments-transformations';
import { transformPhoneNumber } from '../../phone';

export function toTargetUnitDetailsSummaryData(
  details: TargetUnitAccountDetailsResponseDTO,
  isEditable: boolean,
  isFinancialIndependenceEditable: boolean,
  downloadURL: string,
): SummaryData {
  if (!details) {
    return;
  }

  const responsiblePerson = details.targetUnitAccountDetails.responsiblePerson;
  const administrativePerson = details.targetUnitAccountDetails.administrativeContactDetails;

  const summaryDetails = new SummaryFactory()
    .addSection('Target unit details', 'edit/details')
    .addRow('Operator name', details.targetUnitAccountDetails.name)
    .addRow('Operator type', operatorTypeMap[details.targetUnitAccountDetails.operatorType])
    .addRow(
      'Company registration number',
      details.targetUnitAccountDetails.companyRegistrationNumber ??
        details.targetUnitAccountDetails.registrationNumberMissingReason,
    )
    .addRow('Standard industrial classification (SIC) code', details.targetUnitAccountDetails.sicCode, {
      change: isEditable,
      appendChangeParam: false,
    })
    .addRow('Subsector', details.subsectorAssociation?.name)

    .addSection('Financial independence', 'edit/financial-independence')
    .addRow(
      'Status',
      financialIndependenceStatusTypeMap[details.targetUnitAccountDetails.financialIndependenceStatus],
      { change: isFinancialIndependenceEditable, appendChangeParam: false },
    )

    .addSection('Operator address')
    .addRow('Address', getAddressAsArray(details.targetUnitAccountDetails.address))

    .addSection('Responsible Person', 'edit/responsible-person')
    .addRow('First name', responsiblePerson.firstName)
    .addRow('Last name', responsiblePerson.lastName)
    .addRow('Job title', responsiblePerson.jobTitle, { change: isEditable, appendChangeParam: false })
    .addRow('Email address', responsiblePerson.email)
    .addRow('Phone number', transformPhoneNumber(responsiblePerson.phoneNumber), {
      change: isEditable,
      appendChangeParam: false,
    })
    .addRow('Address', getAddressAsArray(responsiblePerson.address))

    .addSection('Administrative contact details', 'edit/administrative-contact')
    .addRow('First name', administrativePerson.firstName, { change: isEditable, appendChangeParam: false })
    .addRow('Last name', administrativePerson.lastName, { change: isEditable, appendChangeParam: false })
    .addRow('Job title', administrativePerson.jobTitle, { change: isEditable, appendChangeParam: false })
    .addRow('Email address', administrativePerson.email, { change: isEditable, appendChangeParam: false })
    .addRow('Phone number', transformPhoneNumber(administrativePerson.phoneNumber), {
      change: isEditable,
      appendChangeParam: false,
    })
    .addRow('Address', getAddressAsArray(administrativePerson.address), {
      change: isEditable,
      appendChangeParam: false,
    })
    .create();

  if (['LIVE', 'TERMINATED'].includes(details.targetUnitAccountDetails.status)) {
    const activeUnASection = new SummaryFactory()
      .addSection('Active Underlying Agreement')
      .addFileListRow(
        'Downloadable version',
        transformFileInfoToDownloadableFile(details.underlyingAgreementDetails.fileDocument, downloadURL),
      )
      .addRow(
        'Activation date',
        new DatePipe('en-GB').transform(details.underlyingAgreementDetails.activationDate, 'dd MMM y'),
      )
      .create();

    return activeUnASection.concat(summaryDetails);
  }

  return summaryDetails;
}
