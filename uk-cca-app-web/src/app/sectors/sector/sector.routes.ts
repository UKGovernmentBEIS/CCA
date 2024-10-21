import { Routes } from '@angular/router';

import { CONTACTS_ROUTES } from './contacts-tab/contacts.routes';
import { DETAILS_ROUTES } from './details-tab/details.routes';
import { SCHEME_ROUTES } from './scheme-tab/scheme.routes';
import { TARGET_UNIT_ROUTES } from './target-units-tab/target-unit.routes';

export const SECTOR_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./sector.component').then((c) => c.SectorComponent),
  },
  ...DETAILS_ROUTES,
  ...SCHEME_ROUTES,
  ...CONTACTS_ROUTES,
  {
    path: 'target-units',
    children: TARGET_UNIT_ROUTES,
  },
];
