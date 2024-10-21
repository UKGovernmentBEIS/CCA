import { BusinessError } from '@error/business-error/business-error';

const targetUnitLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/sectors'],
  linkText: 'Return to Manage Sectors page',
  fragment: 'sector-association',
};

export const targetUnitCreationError = new BusinessError('Target unit account cannot be created').withLink(
  targetUnitLink,
);

export const operatorUserUpdateError = new BusinessError('Operator user details cannot be updated').withLink(
  targetUnitLink,
);
