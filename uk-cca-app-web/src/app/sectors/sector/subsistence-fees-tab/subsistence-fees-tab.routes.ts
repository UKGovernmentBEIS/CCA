import { Routes } from '@angular/router';

export const SUBSISTENCE_FEES_TAB_ROUTES: Routes = [
  {
    path: ':sectorId/sector-moas',
    loadChildren: () => import('./sector-moas/sector-moas.routes').then((r) => r.SECTOR_MOAS_ROUTES),
  },
];
