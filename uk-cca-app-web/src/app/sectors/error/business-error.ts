import { buildSaveNotFoundError, BusinessError } from '@error/business-error/business-error';

const sectorBusinessLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/sectors'],
  linkText: 'Return to Manage Sectors page',
  fragment: 'sector-association',
};

export const sectorAssociationDetailsUpdateError = new BusinessError(
  'Sector association details cannot be updated',
).withLink(sectorBusinessLink);

export const sectorAssociationContactDetailsUpdateError = new BusinessError(
  'Sector association contact cannot be updated',
).withLink(sectorBusinessLink);

export const sectorUserDetailsUpdateError = new BusinessError('Sector user details cannot be updated').withLink(
  sectorBusinessLink,
);

export const saveNotFoundUserError = buildSaveNotFoundError().withLink(sectorBusinessLink);
