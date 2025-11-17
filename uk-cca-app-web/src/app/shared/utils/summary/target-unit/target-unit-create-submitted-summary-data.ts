import { boolToString } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformAddress, transformOperatorType } from '@shared/pipes';

import { TargetUnitAccountPayload } from 'cca-api';

import { transformPhoneNumber } from '../../phone';

export function toTargetUnitCreateSubmittedSummaryData(payload: TargetUnitAccountPayload): SummaryData {
  const factory = new SummaryFactory()
    .addSection('Target unit details', '../', { testid: 'target-unit-details-list' })
    .addRow('Operator name', payload?.name)
    .addRow('Operator type', transformOperatorType(payload?.operatorType))
    .addRow('Does your company have a registration number?', boolToString(payload.isCompanyRegistrationNumber))
    .addRow('Company number', payload?.companyRegistrationNumber ?? 'Not provided');

  if (payload?.registrationNumberMissingReason) {
    factory.addRow('Reason for not having a registration number', payload?.registrationNumberMissingReason);
  }

  factory
    .addRow('Standard Industrial Classification (SIC) codes', payload?.sicCodes)
    .addRow('Subsector', payload?.subsectorAssociationName)

    .addSection('Operator address', '../operator-address', { testid: 'operator-address-list' })
    .addTextAreaRow('Address', transformAddress(payload?.address))

    .addSection('Responsible person', '../responsible-person', { testid: 'responsible-person-list' })
    .addRow('First name', payload?.responsiblePerson?.firstName)
    .addRow('Last name', payload?.responsiblePerson?.lastName)
    .addRow('Job title', payload?.responsiblePerson?.jobTitle)
    .addTextAreaRow('Address', transformAddress(payload?.responsiblePerson?.address))
    .addRow('Phone number', transformPhoneNumber(payload?.responsiblePerson?.phoneNumber))
    .addRow('Email address', payload?.responsiblePerson?.email)

    .addSection('Administrative contact details', '../administrative-contact', {
      testid: 'administrative-contact-list',
    })
    .addRow('First name', payload?.administrativeContactDetails?.firstName)
    .addRow('Last name', payload?.administrativeContactDetails?.lastName)
    .addRow('Job title', payload?.administrativeContactDetails?.jobTitle)
    .addRow('Email address', payload?.administrativeContactDetails?.email)
    .addRow('Phone number', transformPhoneNumber(payload?.administrativeContactDetails?.phoneNumber))
    .addTextAreaRow('Address', transformAddress(payload?.administrativeContactDetails?.address));

  return factory.create();
}
