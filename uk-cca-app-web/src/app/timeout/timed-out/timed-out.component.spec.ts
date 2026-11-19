import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { mockClass } from '@netz/common/testing';
import { AuthService } from '@shared/services';

import { TimedOutComponent } from './timed-out.component';

describe('TimedOutComponent', () => {
  let harness: RouterTestingHarness;

  const authService = mockClass(AuthService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'timed-out', component: TimedOutComponent }], withComponentInputBinding()),
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    harness = await RouterTestingHarness.create();
  });

  it('should sign in again on button click', async () => {
    await harness.navigateByUrl('/timed-out?idle=30', TimedOutComponent);
    const loginSpy = vi.spyOn(authService, 'login');

    harness.routeNativeElement.querySelector('button').click();

    expect(loginSpy).toHaveBeenCalledTimes(1);
  });

  it('should show the idle time from the query param', async () => {
    await harness.navigateByUrl('/timed-out?idle=30', TimedOutComponent);

    expect(harness.routeNativeElement.querySelector('p').textContent).toContain(
      'We have reset your session because you did not do anything for less than a minute .',
    );
  });

  it('should show the expired message without an idle query param', async () => {
    await harness.navigateByUrl('/timed-out', TimedOutComponent);

    expect(harness.routeNativeElement.querySelector('p').textContent).toContain(
      'We have reset your session because it expired.',
    );
  });
});
