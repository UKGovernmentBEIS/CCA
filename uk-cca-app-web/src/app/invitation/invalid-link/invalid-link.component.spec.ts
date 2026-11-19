import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { InvalidLinkComponent } from './invalid-link.component';

describe('InvalidLinkComponent', () => {
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'invalid-link', component: InvalidLinkComponent }], withComponentInputBinding()),
      ],
    }).compileComponents();

    harness = await RouterTestingHarness.create();
  });

  it('should display expired link message if code is EMAIL1001', async () => {
    await harness.navigateByUrl('/invalid-link?code=EMAIL1001', InvalidLinkComponent);

    expect(harness.routeNativeElement.querySelector('h1').textContent).toEqual('This link has expired');
  });

  it('should display invalid link message on any non expired code', async () => {
    await harness.navigateByUrl('/invalid-link?code=TOKEN1001', InvalidLinkComponent);

    expect(harness.routeNativeElement.querySelector('h1').textContent).toEqual('This link is invalid');
  });
});
