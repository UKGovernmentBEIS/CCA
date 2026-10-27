import { Routes } from '@angular/router';

import { WORKFLOW_DETAILS_ROUTES } from '@shared/components';
import { PendingRequestGuard } from '@shared/guards';

import { CONTACTS_ROUTES } from './contacts-tab/contacts.routes';
import { DETAILS_ROUTES } from './details-tab/details.routes';
import { SCHEME_ROUTES } from './scheme-tab/scheme.routes';
import { sectorTabBreadcrumb } from './sector-breadcrumb';
import { SUBSISTENCE_FEES_TAB_ROUTES } from './subsistence-fees-tab/subsistence-fees-tab.routes';
import { IsAllowedUserGuard } from './subsistence-fees-tab/subsitence-fees-tab.guard';
import { TARGET_UNIT_ROUTES } from './target-units-tab/target-unit.routes';

export const SECTOR_ROUTES: Routes = [
  {
    title: 'Sector details',
    path: '',
    loadComponent: () => import('./sector.component').then((c) => c.SectorComponent),
  },
  ...DETAILS_ROUTES,
  ...SCHEME_ROUTES,
  ...CONTACTS_ROUTES,
  {
    path: 'target-units',
    data: { breadcrumb: sectorTabBreadcrumb('target-units') },
    children: TARGET_UNIT_ROUTES,
  },
  {
    path: 'workflow-details',
    canActivate: [IsAllowedUserGuard],
    data: { breadcrumb: sectorTabBreadcrumb('workflow-history') },
    children: WORKFLOW_DETAILS_ROUTES,
  },
  {
    path: 'subsistence-fees',
    canActivate: [IsAllowedUserGuard],
    data: { breadcrumb: sectorTabBreadcrumb('subsistence-fees') },
    children: SUBSISTENCE_FEES_TAB_ROUTES,
  },
  {
    title: 'Start a new task',
    path: 'process-actions',
    loadComponent: () => import('@shared/components').then((c) => c.StartNewTaskComponent),
    canDeactivate: [PendingRequestGuard],
  },
];
