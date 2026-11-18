import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { ActiveSectorStore } from '../active-sector.store';

const CanEditGuard = () => inject(ActiveSectorStore).state.editable;

export const DETAILS_ROUTES: Routes = [
  {
    path: 'edit-details',
    canActivate: [CanEditGuard],
    data: {
      pageTitle: 'Edit sector details',
      backlink: '../',
      breadcrumb: false,
    },
    loadComponent: () =>
      import('./edit-sector-association-details/edit-sector-association-details.component').then(
        (c) => c.EditSectorAssociationDetailsComponent,
      ),
  },
  {
    path: 'edit-contact-details',
    canActivate: [CanEditGuard],
    data: {
      pageTitle: 'Edit sector contact details',
      backlink: '../',
      breadcrumb: false,
    },
    loadComponent: () =>
      import('./edit-sector-association-contact-details/edit-sector-association-contact-details.component').then(
        (c) => c.EditSectorAssociationContactDetailsComponent,
      ),
  },
];
