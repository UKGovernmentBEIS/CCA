import { Component, NgZone } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, Router, RouterOutlet } from '@angular/router';

import { SkipLinkComponent } from './skip-link.component';

describe('SkipLinkComponent', () => {
  let component: SkipLinkComponent;
  let fixture: ComponentFixture<HostComponent>;
  let router: Router;

  @Component({ template: '' })
  class RoutedComponent {}

  @Component({
    imports: [SkipLinkComponent, RouterOutlet],
    template: `
      <govuk-skip-link />
      <main id="main-content" tabindex="-1"></main>
      <router-outlet />
    `,
  })
  class HostComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([{ path: 'test', component: RoutedComponent }])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HostComponent);
    component = fixture.debugElement.query(By.directive(SkipLinkComponent)).componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should correctly set routerLink', async () => {
    const originalNavigateByUrl = router.navigateByUrl;

    vi.spyOn(router, 'navigateByUrl').mockImplementation((...options) =>
      TestBed.inject(NgZone).run(() => originalNavigateByUrl.apply(router, options)),
    );

    await router.navigateByUrl('/test');
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    expect(hostElement.querySelector<HTMLAnchorElement>('a').getAttribute('href').split('#')[0]).toEqual('/test');
  });

  it('should focus the main content element when its fragment is activated', async () => {
    const originalNavigateByUrl = router.navigateByUrl;

    vi.spyOn(router, 'navigateByUrl').mockImplementation((...options) =>
      TestBed.inject(NgZone).run(() => originalNavigateByUrl.apply(router, options)),
    );

    await router.navigateByUrl('/test#main-content');
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const main = hostElement.querySelector<HTMLElement>('#main-content');
    expect(document.activeElement).toBe(main);
  });
});
