import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { map, tap } from 'rxjs';

import { RequestActionsService } from 'cca-api';

import { PaymentState } from './store/payment.state';
import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentActionGuard implements CanActivate {
  constructor(
    private readonly store: PaymentStore,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return this.requestActionsService.getRequestActionById(actionId).pipe(
      tap((requestAction) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          requestId: requestAction.requestId,
          requestType: requestAction.requestType,
          competentAuthority: requestAction.competentAuthority,
          actionPayload: requestAction.payload,
          requestActionId: requestAction.id,
          isEditable: false,
          requestActionCreationDate: requestAction.creationDate,
        } as PaymentState);
      }),
      map(() => true),
    );
  }

  canDeactivate(): boolean {
    return true;
  }
}
