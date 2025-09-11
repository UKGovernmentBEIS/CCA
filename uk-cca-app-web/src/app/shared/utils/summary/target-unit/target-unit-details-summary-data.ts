import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { operatorTypeMap, StatusPipe } from '@shared/pipes';

import { TargetUnitAccountDetailsResponseDTO } from 'cca-api';

import { getAddressAsArray } from '../../address';
import { fileUtils } from '../../files';
import { transformPhoneNumber } from '../../phone';

export function toTargetUnitDetailsSummaryData(
  details: TargetUnitAccountDetailsResponseDTO,
  isEditable: boolean,
  isFinancialIndependenceEditable: boolean,
  downloadURL: string,
): SummaryData {
  if (!details) return;

  const responsiblePerson = details.targetUnitAccountDetails.responsiblePerson;
  const administrativePerson = details.targetUnitAccountDetails.administrativeContactDetails;

  const factory = new SummaryFactory();

  if (['LIVE', 'TERMINATED'].includes(details.targetUnitAccountDetails.status)) {
    factory
      .addSection('Active Underlying Agreement')
      .addFileListRow(
        'Downloadable version',
        fileUtils.toDownloadableDocument([details.underlyingAgreementDetails.fileDocument], downloadURL),
      )
      .addRow(
        'Activation date',
        new DatePipe('en-GB').transform(details.underlyingAgreementDetails.activationDate, 'dd MMM y'),
      );
  }

  factory
    .addSection('Target unit details', 'edit/details')
    .addRow('Operator name', details.targetUnitAccountDetails.name)
    .addRow('Operator type', operatorTypeMap[details.targetUnitAccountDetails.operatorType])
    .addRow('Company number', details.targetUnitAccountDetails.companyRegistrationNumber ?? 'Not provided');

  if (details.targetUnitAccountDetails.registrationNumberMissingReason) {
    factory.addRow(
      'Reason for not having a registration number',
      details.targetUnitAccountDetails.registrationNumberMissingReason,
    );
  }

  factory
    .addRow('Standard industrial classification (SIC) codes', details.targetUnitAccountDetails.sicCodes, {
      change: isEditable,
      appendChangeParam: false,
    })
    .addRow('Subsector', details.subsectorAssociation?.name)

    .addSection('Financial independence', 'edit/financial-independence')
    .addRow('Status', new StatusPipe().transform(details.targetUnitAccountDetails.financialIndependenceStatus), {
      change: isFinancialIndependenceEditable,
      appendChangeParam: false,
    })

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
    });

  return factory.create();
}
