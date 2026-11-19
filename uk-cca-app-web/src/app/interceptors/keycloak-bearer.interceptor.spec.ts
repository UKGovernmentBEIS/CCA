import { HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { firstValueFrom, of } from 'rxjs';

import { AuthService, KeycloakService } from '@shared/services';

import { environment } from 'src/environments/environment';

import { KeycloakBearerInterceptor } from './keycloak-bearer.interceptor';

describe('KeycloakBearerInterceptor', () => {
  const keycloakService = {
    isAuthenticated: true,
    token: 'test-token',
    updateToken: vi.fn(),
  };

  const authService = {
    login: vi.fn(),
    reauthenticate: vi.fn(),
  };

  const sameOriginUrl = '/v1.0/users/validate-password';
  const apiOriginUrl = new URL(sameOriginUrl, new URL(environment.apiOptions.baseUrl, location.origin)).toString();
  const externalUrl = 'https://api.pwnedpasswords.com/range/ABC';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: AuthService, useValue: authService },
      ],
    });

    keycloakService.isAuthenticated = true;
    keycloakService.token = 'test-token';
    keycloakService.updateToken.mockReset();
    keycloakService.updateToken.mockResolvedValue(true);
    authService.login.mockReset();
    authService.login.mockResolvedValue(undefined);
    authService.reauthenticate.mockReset();
    authService.reauthenticate.mockResolvedValue(undefined);
  });

  function intercept(request: HttpRequest<unknown>, next: HttpHandlerFn) {
    return TestBed.runInInjectionContext(() => KeycloakBearerInterceptor(request, next));
  }

  function nextHandler() {
    return vi.fn().mockReturnValue(of(new HttpResponse<unknown>()));
  }

  it('should add a bearer token for same-origin requests', async () => {
    const next = nextHandler();

    await firstValueFrom(intercept(new HttpRequest<unknown>('GET', sameOriginUrl), next));

    expect(keycloakService.updateToken).toHaveBeenCalledWith(environment.timeoutBanner.timeOffsetSeconds);
    expect(next).toHaveBeenCalledTimes(1);
    const forwarded = next.mock.calls[0][0] as unknown as HttpRequest<unknown>;
    expect(forwarded.headers.get('Authorization')).toBe('Bearer test-token');
  });

  it('should add a bearer token for requests to the configured API origin', async () => {
    const next = nextHandler();

    await firstValueFrom(intercept(new HttpRequest<unknown>('GET', apiOriginUrl), next));

    expect(keycloakService.updateToken).toHaveBeenCalledWith(environment.timeoutBanner.timeOffsetSeconds);
    expect(next).toHaveBeenCalledTimes(1);
    const forwarded = next.mock.calls[0][0] as unknown as HttpRequest<unknown>;
    expect(forwarded.headers.get('Authorization')).toBe('Bearer test-token');
  });

  it('should not add a bearer token for external requests', async () => {
    const next = nextHandler();
    const request = new HttpRequest<unknown>('GET', externalUrl);

    await firstValueFrom(intercept(request, next));

    expect(keycloakService.updateToken).not.toHaveBeenCalled();
    expect(next).toHaveBeenCalledWith(request);
    expect(request.headers.has('Authorization')).toBe(false);
  });

  it('should not add a bearer token when unauthenticated', async () => {
    keycloakService.isAuthenticated = false;
    const next = nextHandler();
    const request = new HttpRequest<unknown>('GET', sameOriginUrl);

    await firstValueFrom(intercept(request, next));

    expect(keycloakService.updateToken).not.toHaveBeenCalled();
    expect(next).toHaveBeenCalledWith(request);
    expect(request.headers.has('Authorization')).toBe(false);
  });

  it('should still propagate the refresh error when re-authentication rejects', async () => {
    keycloakService.updateToken.mockRejectedValueOnce(new Error('refresh failed'));
    authService.reauthenticate.mockRejectedValueOnce(new Error('login failed'));
    const next = nextHandler();

    await expect(firstValueFrom(intercept(new HttpRequest<unknown>('GET', sameOriginUrl), next))).rejects.toThrow(
      'refresh failed',
    );

    expect(authService.reauthenticate).toHaveBeenCalledTimes(1);
    expect(authService.login).not.toHaveBeenCalled();
    expect(next).not.toHaveBeenCalled();
  });

  it('should not add a bearer token for lookalike hosts', async () => {
    const apiUrl = new URL(environment.apiOptions.baseUrl, location.origin);
    const lookalike = `${apiUrl.protocol}//${apiUrl.host}.attacker.test/v1.0/users/validate-password`;
    const next = nextHandler();
    const request = new HttpRequest<unknown>('GET', lookalike);

    await firstValueFrom(intercept(request, next));

    expect(keycloakService.updateToken).not.toHaveBeenCalled();
    expect(request.headers.has('Authorization')).toBe(false);
  });
});
