import { Routes } from '@angular/router';

export const SUBSISTENCE_FEES_TAB_ROUTES: Routes = [
  {
    path: ':sectorId/sector-moas',
    loadChildren: () =>
      import('../../../subsistence-fees/sent-subsistence-fees/sector-moas/sector-moas.routes').then(
        (r) => r.SECTOR_MOAS_ROUTES,
      ),
  },
];
