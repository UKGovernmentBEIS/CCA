import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { boolToString } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformOperatorType } from '@shared/pipes';

import { AccountAddressDTO, SectorAssociationSchemesDTO, TargetUnitAccountPayload } from 'cca-api';

import { transformPhoneNumber } from '../../phone';

export function toTargetUnitCreateSummaryData(payload: TargetUnitAccountPayload): SummaryData {
  const subSectors = (inject(ActivatedRoute).snapshot.data?.subSectorScheme as SectorAssociationSchemesDTO)
    ?.subsectorAssociations;

  const selectedSubsector = subSectors?.find((ss) => ss.id === payload?.subsectorAssociationId)?.name;

  const factory = new SummaryFactory()
    .addSection('Target unit details', '../', { testid: 'target-unit-details-list' })
    .addChangeRow('Operator name', payload?.name)
    .addChangeRow('Operator type', transformOperatorType(payload?.operatorType))
    .addChangeRow('Does your company have a registration number?', boolToString(payload.isCompanyRegistrationNumber))
    .addChangeRow('Company number', payload?.companyRegistrationNumber ?? 'Not provided');

  if (payload?.registrationNumberMissingReason) {
    factory.addChangeRow('Reason for not having a registration number', payload?.registrationNumberMissingReason);
  }

  factory
    .addChangeRow('Standard Industrial Classification (SIC) codes', payload?.sicCodes)
    .addRow('Subsector', selectedSubsector, selectedSubsector ? { change: true } : null)

    .addSection('Operator address', '../operator-address', { testid: 'operator-address-list' })
    .addTextAreaRow('Address', getAddressAsArray(payload?.address), { change: true })

    .addSection('Responsible person', '../responsible-person', { testid: 'responsible-person-list' })
    .addChangeRow('First name', payload?.responsiblePerson?.firstName)
    .addChangeRow('Last name', payload?.responsiblePerson?.lastName)
    .addChangeRow('Job title', payload?.responsiblePerson?.jobTitle)
    .addChangeRow('Email address', payload?.responsiblePerson?.email)
    .addChangeRow('Phone number', transformPhoneNumber(payload?.responsiblePerson?.phoneNumber))
    .addTextAreaRow('Address', getAddressAsArray(payload?.responsiblePerson?.address), { change: true })

    .addSection('Administrative contact details', '../administrative-contact', {
      testid: 'administrative-contact-list',
    })
    .addChangeRow('First name', payload?.administrativeContactDetails?.firstName)
    .addChangeRow('Last name', payload?.administrativeContactDetails?.lastName)
    .addChangeRow('Job title', payload?.administrativeContactDetails?.jobTitle)
    .addChangeRow('Email address', payload?.administrativeContactDetails?.email)
    .addChangeRow('Phone number', transformPhoneNumber(payload?.administrativeContactDetails?.phoneNumber))
    .addTextAreaRow('Address', getAddressAsArray(payload?.administrativeContactDetails?.address), {
      change: true,
    });

  return factory.create();
}

function getAddressAsArray(address: AccountAddressDTO): string[] {
  return [address?.line1, address?.line2, address?.city, address?.county, address?.postcode, address?.country].filter(
    Boolean,
  );
}
