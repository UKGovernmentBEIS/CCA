import { FormBuilder } from '@angular/forms';

import { SectorAssociationSiteContactInfoDTO } from 'cca-api';

export function createForm(fb: FormBuilder, siteContacts: SectorAssociationSiteContactInfoDTO[]) {
  return fb.group({
    siteContacts: fb.array(
      siteContacts.map(({ sectorAssociationId, userId }) => {
        return fb.group({ sectorAssociationId, userId });
      }),
    ),
  });
}
