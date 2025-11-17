import { Pipe, PipeTransform } from '@angular/core';

export enum AgreementCompositionTypeEnum {
  ABSOLUTE = 'Absolute',
  RELATIVE = 'Relative',
  NOVEM = 'Novem',
}

export function transformAgreementCompositionType(value: keyof typeof AgreementCompositionTypeEnum): string {
  const text = AgreementCompositionTypeEnum[value];
  if (!text) throw new Error('Invalid agreement composition type');
  return text;
}

@Pipe({ name: 'agreementCompositionType' })
export class AgreementCompositionTypePipe implements PipeTransform {
  transform = transformAgreementCompositionType;
}
