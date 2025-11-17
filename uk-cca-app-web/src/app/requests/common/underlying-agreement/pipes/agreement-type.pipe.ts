import { Pipe, PipeTransform } from '@angular/core';

import { EligibilityDetailsAndAuthorisation } from 'cca-api';

export enum AgreementTypeEnum {
  ENVIRONMENTAL_PERMITTING_REGULATIONS = 'Environmental Permitting Regulations (EPR)',
  ENERGY_INTENSIVE = 'Energy Intensive',
}

@Pipe({ name: 'agreementType' })
export class AgreementTypePipe implements PipeTransform {
  transform(value: EligibilityDetailsAndAuthorisation['agreementType']): string {
    const text = AgreementTypeEnum[value];
    if (!text) throw new Error('invalid agreement type');
    return text;
  }
}
