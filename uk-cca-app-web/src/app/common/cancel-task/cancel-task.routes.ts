import { Routes } from '@angular/router';

import { CancelComponent } from '@common/cancel-task/components/cancel';
import { ConfirmationComponent } from '@common/cancel-task/components/confirmation';

export const ROUTES: Routes = [
  {
    path: '',
    component: CancelComponent,
    data: { pageTitle: 'Task cancellation' },
  },
  {
    path: 'confirmation',
    component: ConfirmationComponent,
    data: { pageTitle: 'Task cancelled' },
  },
];
