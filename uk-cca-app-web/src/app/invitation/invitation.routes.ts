import { Routes } from '@angular/router';

import { OPERATOR_INVITATION_USER_ROUTES } from './operator-user-invitation/routes';
import { REGULATOR_INVITATION_ROUTES } from './regulator-invitation/routes';
import { SECTOR_USER_INVITATION_ROUTES } from './sector-user-invitation/routes';

export const INVITATION_ROUTES: Routes = [
  ...REGULATOR_INVITATION_ROUTES,
  ...SECTOR_USER_INVITATION_ROUTES,
  ...OPERATOR_INVITATION_USER_ROUTES,
];
