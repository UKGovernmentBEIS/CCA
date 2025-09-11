import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { CONTACTS_ROUTES } from './contacts-tab/contacts.routes';
import { DETAILS_ROUTES } from './details-tab/details.routes';
import { SCHEME_ROUTES } from './scheme-tab/scheme.routes';
import { SUBSISTENCE_FEES_TAB_ROUTES } from './subsistence-fees-tab/subsistence-fees-tab.routes';
import { IsAllowedUserGuard } from './subsistence-fees-tab/subsitence-fees-tab.guard';
import { TARGET_UNIT_ROUTES } from './target-units-tab/target-unit.routes';
import { WORKFLOW_DETAILS_ROUTES } from './workflow-history-tab/workflow-details/workflow-details.routes';

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
  {
    path: 'workflow-details',
    canActivate: [IsAllowedUserGuard],
    children: WORKFLOW_DETAILS_ROUTES,
  },
  {
    path: 'subsistence-fees',
    canActivate: [IsAllowedUserGuard],
    children: SUBSISTENCE_FEES_TAB_ROUTES,
  },
  {
    path: 'process-actions',
    title: 'Start a new task',
    canDeactivate: [PendingRequestGuard],
    loadComponent: () => import('@shared/components').then((c) => c.StartNewTaskComponent),
  },
];
