import { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';

import { Observable } from 'rxjs';

import { logGoogleEvent } from '@core/analytics';
import { HttpMethods } from 'keycloak-angular/lib/core/interfaces/keycloak-options';

type AllowedRoute = { method: HttpMethods; endpoint: string };

const taskActionRoute = {
  method: 'POST',
  endpoint: '/tasks/actions',
};

const userRegisterRoutes: AllowedRoute[] = [
  {
    method: 'POST',
    endpoint: '/operator-users/registration/register',
  },
  {
    method: 'PUT',
    endpoint: '/regulator-users/registration/enable-from-invitation',
  },
];

const accountCreateRoutes: AllowedRoute[] = [
  {
    endpoint: 'aviation/accounts',
    method: 'POST',
  },
  {
    endpoint: 'requests',
    method: 'POST',
  },
];

const allowedRoutes = [taskActionRoute, ...userRegisterRoutes, ...accountCreateRoutes];

export function AnalyticsInterceptor(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
  const allowedRoute = allowedRoutes.find((ar) => request.url.includes(ar.endpoint));
  if (!allowedRoute) return next(request);
  if (request.url.includes(taskActionRoute.endpoint) && request.method === taskActionRoute.method) {
    handleTaskAction(request);
  } else if (userRegisterRoutes.some((r) => r.method === request.method && request.url.includes(r.endpoint))) {
    logGoogleEvent('USER_REGISTERED');
  } else if (accountCreateRoutes.some((r) => r.method === request.method && request.url.includes(r.endpoint))) {
    handleAccountCreate(request);
  }
  return next(request);
}
function handleTaskAction(request: HttpRequest<unknown>): void {
  let requestId: string;

  if (!requestId) {
    console.warn('no request id. will not log ga event');
    return;
  }

  const body = request.body;
  const eventName = body['requestTaskActionType'];
  const requestTaskId = body['requestTaskId'];
  const scheme = 'CCA';
  const role = this.authStore.getState().userState.roleType;

  logGoogleEvent(eventName, {
    requestTaskId,
    scheme,
    role,
    requestId,
  });
}
function handleAccountCreate(request: HttpRequest<unknown>): void {
  if (request.body['requestCreateActionType'] === 'ORGANISATION_ACCOUNT_OPENING_SUBMIT_APPLICATION')
    logGoogleEvent('ORGANISATION_ACCOUNT_OPENING_SUBMIT_APPLICATION', {
      scheme: 'CCA',
    });
}
