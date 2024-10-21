import { SummaryData, SummaryFactory } from '@shared/components';
import { ContactTypeEnum } from '@shared/pipes';
import { transformPhoneNumber } from '@shared/utils/phone';

import { CcaOperatorUserDetailsDTO } from 'cca-api';

export function toSummaryData(operator: CcaOperatorUserDetailsDTO, changeContactType: boolean): SummaryData {
  return new SummaryFactory()
    .addSection('Name', 'edit', { testid: 'name-list' })
    .addChangeRow('First name', operator.firstName)
    .addChangeRow('Last name', operator.lastName)
    .addChangeRow('Job title', operator.jobTitle)
    .addRow('Email', operator.email)

    .addSection('Organisation details', 'edit', { testid: 'organisation-details-list' })
    .addRow('Contact type', ContactTypeEnum[operator.contactType], changeContactType ? { change: true } : null)
    .addChangeRow('Organisation name', operator.organisationName)
    .addChangeRow('Phone number 1', transformPhoneNumber(operator.phoneNumber))
    .addChangeRow('Phone number 2', transformPhoneNumber(operator.mobileNumber))
    .create();
}
