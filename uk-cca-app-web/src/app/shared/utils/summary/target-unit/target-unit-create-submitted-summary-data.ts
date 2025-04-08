import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SummaryData, SummaryFactory } from '@shared/components';
import { transformOperatorType } from '@shared/pipes';

import { AccountAddressDTO, SectorAssociationSchemeDTO, TargetUnitAccountPayload } from 'cca-api';

import { transformPhoneNumber } from '../../phone';

export function toTargetUnitCreateSubmittedSummaryData(payload: TargetUnitAccountPayload): SummaryData {
  const subSectors = (inject(ActivatedRoute).snapshot.data?.subSectorScheme as SectorAssociationSchemeDTO)
    ?.subsectorAssociationSchemes;

  const selectedSubsector = subSectors?.find((ss) => ss.id === payload?.subsectorAssociationId)?.subsectorAssociation
    ?.name;

  return new SummaryFactory()
    .addSection('Target unit details', '../', { testid: 'target-unit-details-list' })
    .addRow('Operator name', payload?.name)
    .addRow('Operator type', transformOperatorType(payload?.operatorType))
    .addRow(
      payload?.isCompanyRegistrationNumber
        ? 'Company Registration Number'
        : 'Reason why you do not have a registration number',
      payload?.isCompanyRegistrationNumber
        ? payload?.companyRegistrationNumber
        : payload?.registrationNumberMissingReason,
    )
    .addRow('Nature of business (SIC) code', payload?.sicCode)
    .addRow('Subsector', selectedSubsector)

    .addSection('Operator address', '../operator-address', { testid: 'operator-address-list' })
    .addRow('Address', getAddressAsArray(payload?.address), { prewrap: true })

    .addSection('Responsible person', '../responsible-person', { testid: 'responsible-person-list' })
    .addRow('First name', payload?.responsiblePerson?.firstName)
    .addRow('Last name', payload?.responsiblePerson?.lastName)
    .addRow('Job title', payload?.responsiblePerson?.jobTitle)
    .addRow('Address', getAddressAsArray(payload?.responsiblePerson?.address), { prewrap: true })
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
    .addRow('Address', getAddressAsArray(payload?.administrativeContactDetails?.address), {
      prewrap: true,
    })
    .create();
}

function getAddressAsArray(address: AccountAddressDTO): string[] {
  return [address?.line1, address?.line2, address?.city, address?.county, address?.postcode, address?.country].filter(
    Boolean,
  );
}
