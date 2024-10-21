import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { boolToString } from '@requests/common';
import { SummaryData, SummaryFactory } from '@shared/components';
import { transformOperatorType } from '@shared/pipes';
import { transformPhoneNumber } from '@shared/utils/phone';

import { AccountAddressDTO, SectorAssociationSchemeDTO, TargetUnitAccountPayload } from 'cca-api';

export function toTargetUnitCreateSummaryData(payload: TargetUnitAccountPayload): SummaryData {
  const subSectors = (inject(ActivatedRoute).snapshot.data?.subSectorScheme as SectorAssociationSchemeDTO)
    ?.subsectorAssociationSchemes;

  const selectedSubsector = subSectors?.find((ss) => ss.id === payload?.subsectorAssociationId)?.subsectorAssociation
    ?.name;

  return new SummaryFactory()
    .addSection('Target unit details', '../', { testid: 'target-unit-details-list' })
    .addChangeRow('Operator name', payload?.name)
    .addChangeRow('Operator type', transformOperatorType(payload?.operatorType))
    .addChangeRow('Does your company have a registration number?', boolToString(payload.isCompanyRegistrationNumber))
    .addChangeRow(
      payload?.isCompanyRegistrationNumber
        ? 'Company registration number'
        : 'Reason why you do not have a registration number',
      payload?.isCompanyRegistrationNumber
        ? payload?.companyRegistrationNumber
        : payload?.registrationNumberMissingReason,
    )
    .addChangeRow('Nature of business (SIC) code', payload?.sicCode)
    .addRow('Subsector', selectedSubsector, selectedSubsector ? { change: true } : null)

    .addSection('Operator address', '../operator-address', { testid: 'operator-address-list' })
    .addChangeRow('Address', getAddressAsArray(payload?.address), { prewrap: true })

    .addSection('Responsible person', '../responsible-person', { testid: 'responsible-person-list' })
    .addChangeRow('First name', payload?.responsiblePerson?.firstName)
    .addChangeRow('Last name', payload?.responsiblePerson?.lastName)
    .addChangeRow('Job title', payload?.responsiblePerson?.jobTitle)
    .addChangeRow('Email address', payload?.responsiblePerson?.email)
    .addChangeRow('Phone number', transformPhoneNumber(payload?.responsiblePerson?.phoneNumber))
    .addChangeRow('Address', getAddressAsArray(payload?.responsiblePerson?.address), { prewrap: true })

    .addSection('Administrative contact details', '../administrative-contact', {
      testid: 'administrative-contact-list',
    })
    .addChangeRow('First name', payload?.administrativeContactDetails?.firstName)
    .addChangeRow('Last name', payload?.administrativeContactDetails?.lastName)
    .addChangeRow('Job title', payload?.administrativeContactDetails?.jobTitle)
    .addChangeRow('Email address', payload?.administrativeContactDetails?.email)
    .addChangeRow('Phone number', transformPhoneNumber(payload?.administrativeContactDetails?.phoneNumber))
    .addChangeRow('Address', getAddressAsArray(payload?.administrativeContactDetails?.address), {
      prewrap: true,
    })
    .create();
}

function getAddressAsArray(address: AccountAddressDTO): string[] {
  return [address?.line1, address?.line2, address?.city, address?.county, address?.postcode, address?.country].filter(
    Boolean,
  );
}
