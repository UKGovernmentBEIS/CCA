import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { ActiveSectorStore } from '../active-sector.store';

const CanEditGuard = () => inject(ActiveSectorStore).state.editable;

export const DETAILS_ROUTES: Routes = [
  {
    title: 'Edit sector details',
    path: 'edit-details',
    loadComponent: () =>
      import('./edit-sector-association-details/edit-sector-association-details.component').then(
        (c) => c.EditSectorAssociationDetailsComponent,
      ),
    canActivate: [CanEditGuard],
    data: {
      backlink: { link: '../', fragment: 'details' },
      breadcrumb: false,
    },
  },
  {
    title: 'Edit sector contact details',
    path: 'edit-contact-details',
    loadComponent: () =>
      import('./edit-sector-association-contact-details/edit-sector-association-contact-details.component').then(
        (c) => c.EditSectorAssociationContactDetailsComponent,
      ),
    canActivate: [CanEditGuard],
    data: {
      backlink: { link: '../', fragment: 'details' },
      breadcrumb: false,
    },
  },
];
