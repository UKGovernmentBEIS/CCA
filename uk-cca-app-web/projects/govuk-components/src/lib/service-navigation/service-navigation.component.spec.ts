import { Component, PLATFORM_ID, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, Router } from '@angular/router';

import { vi } from 'vitest';

import type { GovukLinkTarget, GovukServiceNavigationItem } from '../types';
import { GovukServiceNavigationComponent } from './service-navigation.component';

describe('GovukServiceNavigationComponent', () => {
  let component: GovukServiceNavigationComponent;
  let fixture: ComponentFixture<TestComponent>;
  let host: TestComponent;

  @Component({
    selector: 'govuk-service-navigation-test',
    template: `
      <govuk-service-navigation
        [ariaLabel]="ariaLabel()"
        [collapseOnMobile]="collapseOnMobile()"
        [menuButtonLabel]="menuButtonLabel()"
        [menuButtonText]="menuButtonText()"
        [navigation]="navigation()"
        [navigationId]="navigationId()"
        [navigationLabel]="navigationLabel()"
        [serviceHomepage]="serviceHomepage()"
        [serviceName]="serviceName()"
      />
    `,
    imports: [GovukServiceNavigationComponent],
  })
  class TestComponent {
    readonly serviceName = signal<string | undefined>('Manage your Climate Change Agreements');
    readonly serviceHomepage = signal<GovukLinkTarget | undefined>({ routerLink: '/' });
    readonly navigation = signal<GovukServiceNavigationItem[]>([
      { text: 'Tasks', routerLink: '/dashboard' },
      { text: 'Target unit accounts', routerLink: '/target-unit-accounts' },
      { text: 'Sign out', routerLink: '/sign-out' },
    ]);
    readonly ariaLabel = signal('Service information');
    readonly navigationLabel = signal<string | undefined>(undefined);
    readonly menuButtonText = signal('Menu');
    readonly menuButtonLabel = signal<string | undefined>(undefined);
    readonly navigationId = signal('navigation');
    readonly collapseOnMobile = signal<boolean | undefined>(undefined);
  }

  // The component reads `matchMedia` while constructing its `isDesktop` signal,
  // so the viewport has to be decided before the fixture exists.
  const realMatchMedia = window.matchMedia;

  const setViewport = (isDesktop: boolean) => {
    window.matchMedia = ((query: string) => ({ matches: isDesktop, media: query })) as typeof window.matchMedia;
  };

  const render = async (isDesktop = true) => {
    setViewport(isDesktop);
    fixture = TestBed.createComponent(TestComponent);
    host = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(GovukServiceNavigationComponent)).componentInstance;
    await fixture.whenStable();
  };

  const navigateTo = async (url: string) => {
    await TestBed.inject(Router).navigateByUrl(url);
    await fixture.whenStable();
  };

  const root = (): HTMLElement => fixture.nativeElement.querySelector('govuk-service-navigation');
  const nav = (): HTMLElement | null => fixture.nativeElement.querySelector('nav');
  const toggle = (): HTMLButtonElement | null =>
    fixture.nativeElement.querySelector('.govuk-service-navigation__toggle');
  const list = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-service-navigation__list');
  const items = (): HTMLElement[] =>
    Array.from(fixture.nativeElement.querySelectorAll('.govuk-service-navigation__item'));
  const serviceNameEl = (): HTMLElement | null =>
    fixture.nativeElement.querySelector('.govuk-service-navigation__service-name');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [
        provideRouter([
          { path: 'dashboard', children: [] },
          { path: 'target-unit-accounts', children: [] },
          { path: 'sign-out', children: [] },
          { path: 'buyout-surplus', children: [{ path: 'new-batch', children: [] }] },
          { path: '', children: [] },
        ]),
      ],
    }).compileComponents();
  });

  afterEach(() => {
    window.matchMedia = realMatchMedia;
  });

  it('should create', async () => {
    await render();
    expect(component).toBeTruthy();
  });

  describe('service name', () => {
    it('should wrap the service name and links in a named region', async () => {
      await render();

      expect(root().getAttribute('role')).toBe('region');
      expect(root().getAttribute('aria-label')).toBe('Service information');
    });

    it('should accept a custom region label', async () => {
      await render();
      host.ariaLabel.set('Climate Change Agreements service');
      await fixture.whenStable();

      expect(root().getAttribute('aria-label')).toBe('Climate Change Agreements service');
    });

    // GDS only wraps in a landmark when there is a service name — otherwise the
    // <nav> is landmark enough on its own.
    it('should not expose a region when there is no service name', async () => {
      await render();
      host.serviceName.set(undefined);
      await fixture.whenStable();

      expect(root().hasAttribute('role')).toBe(false);
      expect(root().hasAttribute('aria-label')).toBe(false);
      expect(serviceNameEl()).toBeNull();
    });

    it('should render the service name as a router link', async () => {
      await render();

      const link = serviceNameEl()?.querySelector('a') as HTMLAnchorElement;
      expect(link.getAttribute('href')).toBe('/');
      expect(link.classList.contains('govuk-service-navigation__link')).toBe(true);
      expect(link.textContent?.trim()).toBe('Manage your Climate Change Agreements');
    });

    it('should give every link the service navigation link class', async () => {
      await render();

      const anchors: HTMLElement[] = Array.from(fixture.nativeElement.querySelectorAll('a'));
      expect(anchors).toHaveLength(4);
      for (const anchor of anchors) {
        expect(anchor.classList.contains('govuk-service-navigation__link')).toBe(true);
      }
    });

    it('should render the service name as a plain href when it leaves the app', async () => {
      await render();
      host.serviceHomepage.set({ href: 'https://www.gov.uk' });
      await fixture.whenStable();

      const link = serviceNameEl()?.querySelector('a') as HTMLAnchorElement;
      expect(link.getAttribute('href')).toBe('https://www.gov.uk');
      expect(link.classList.contains('govuk-service-navigation__link')).toBe(true);
    });

    it('should carry query params and a fragment through to the service homepage link', async () => {
      await render();
      host.serviceHomepage.set({ routerLink: '/', queryParams: { lang: 'cy' }, fragment: 'content' });
      await fixture.whenStable();

      expect(serviceNameEl()?.querySelector('a')?.getAttribute('href')).toBe('/?lang=cy#content');
    });

    it('should prefer the route when both a route and an href are given', async () => {
      await render();
      host.serviceHomepage.set({ routerLink: '/', href: 'https://www.gov.uk' });
      await fixture.whenStable();

      expect(serviceNameEl()?.querySelector('a')?.getAttribute('href')).toBe('/');
    });

    it('should render the service name as text when there is no homepage', async () => {
      await render();
      host.serviceHomepage.set(undefined);
      await fixture.whenStable();

      expect(serviceNameEl()?.querySelector('a')).toBeNull();
      const text = serviceNameEl()?.querySelector('.govuk-service-navigation__text');
      expect(text?.textContent?.trim()).toBe('Manage your Climate Change Agreements');
    });
  });

  describe('navigation', () => {
    it('should render the navigation as a labelled landmark', async () => {
      await render();

      expect(nav()?.getAttribute('aria-label')).toBe('Menu');
    });

    it('should label the landmark independently of the menu button', async () => {
      await render();
      host.navigationLabel.set('Primary navigation');
      host.menuButtonText.set('Show menu');
      await fixture.whenStable();

      expect(nav()?.getAttribute('aria-label')).toBe('Primary navigation');
      expect(toggle()?.textContent?.trim()).toBe('Show menu');
    });

    it('should render one link per item', async () => {
      await render();

      expect(items()).toHaveLength(3);
      expect(items().map((item) => item.querySelector('a')?.getAttribute('href'))).toEqual([
        '/dashboard',
        '/target-unit-accounts',
        '/sign-out',
      ]);
      expect(items().map((item) => item.querySelector('a')?.textContent?.trim())).toEqual([
        'Tasks',
        'Target unit accounts',
        'Sign out',
      ]);
    });

    it('should render no landmark when there are no items', async () => {
      await render();
      host.navigation.set([]);
      await fixture.whenStable();

      expect(nav()).toBeNull();
    });

    it('should carry query params and fragments through to the router link', async () => {
      await render();
      host.navigation.set([
        {
          text: 'Buy-out and surplus',
          routerLink: '/buyout-surplus',
          queryParams: { status: 'paid' },
          fragment: 'transactions',
        },
      ]);
      await fixture.whenStable();

      expect(items()[0].querySelector('a')?.getAttribute('href')).toBe('/buyout-surplus?status=paid#transactions');
    });

    it('should use the navigation id to tie the button to the list', async () => {
      await render(false);
      host.navigationId.set('primary-navigation');
      await fixture.whenStable();

      expect(list()?.id).toBe('primary-navigation');
      expect(toggle()?.getAttribute('aria-controls')).toBe('primary-navigation');
    });
  });

  describe('current page', () => {
    it('should mark the item for the current route', async () => {
      await render();
      await navigateTo('/target-unit-accounts');

      expect(items()[1].classList.contains('govuk-service-navigation__item--active')).toBe(true);

      const link = items()[1].querySelector('a') as HTMLAnchorElement;
      expect(link.getAttribute('aria-current')).toBe('page');
      expect(link.querySelector('strong.govuk-service-navigation__active-fallback')?.textContent).toBe(
        'Target unit accounts',
      );
    });

    it.each(['/buyout-surplus#cost', '/buyout-surplus?paymentStatus=PAID#transactions', '/buyout-surplus/new-batch'])(
      'should mark an item with query params as current on %s',
      async (url) => {
        await render();
        host.navigation.set([
          {
            text: 'Buy-out and surplus',
            routerLink: '/buyout-surplus',
            queryParams: { paymentStatus: 'AWAITING_PAYMENT' },
          },
        ]);
        await navigateTo(url);

        expect(items()[0].classList.contains('govuk-service-navigation__item--active')).toBe(true);
        expect(items()[0].querySelector('a')?.getAttribute('aria-current')).toBe('page');
      },
    );

    it('should leave every other item unmarked', async () => {
      await render();
      await navigateTo('/target-unit-accounts');

      for (const index of [0, 2]) {
        expect(items()[index].classList.contains('govuk-service-navigation__item--active')).toBe(false);
        expect(items()[index].querySelector('a')?.hasAttribute('aria-current')).toBe(false);
        expect(items()[index].querySelector('strong')).toBeNull();
      }
    });

    it('should follow the router as the user navigates', async () => {
      await render();
      await navigateTo('/target-unit-accounts');
      await navigateTo('/dashboard');

      expect(items()[0].querySelector('a')?.getAttribute('aria-current')).toBe('page');
      expect(items()[1].querySelector('a')?.hasAttribute('aria-current')).toBe(false);
    });
  });

  describe('mobile menu', () => {
    it('should hide the menu button and show the links on desktop', async () => {
      await render();

      expect(toggle()?.hasAttribute('hidden')).toBe(true);
      expect(toggle()?.getAttribute('aria-hidden')).toBe('true');
      expect(list()?.hasAttribute('hidden')).toBe(false);
    });

    it('should collapse the links behind the menu button on small screens', async () => {
      await render(false);

      expect(toggle()?.hasAttribute('hidden')).toBe(false);
      expect(toggle()?.hasAttribute('aria-hidden')).toBe(false);
      expect(toggle()?.getAttribute('aria-expanded')).toBe('false');
      expect(list()?.hasAttribute('hidden')).toBe(true);
    });

    it('should open and close the menu from the button', async () => {
      await render(false);

      toggle()?.click();
      await fixture.whenStable();
      expect(toggle()?.getAttribute('aria-expanded')).toBe('true');
      expect(list()?.hasAttribute('hidden')).toBe(false);

      toggle()?.click();
      await fixture.whenStable();
      expect(toggle()?.getAttribute('aria-expanded')).toBe('false');
      expect(list()?.hasAttribute('hidden')).toBe(true);
    });

    // GDS defaults collapseNavigationOnMobile to `navigation.length > 1`.
    it('should not collapse a single navigation item', async () => {
      await render(false);
      host.navigation.set([{ text: 'Sign out', routerLink: '/sign-out' }]);
      await fixture.whenStable();

      expect(toggle()).toBeNull();
      expect(list()?.hasAttribute('hidden')).toBe(false);
    });

    it('should collapse a single item when asked to explicitly', async () => {
      await render(false);
      host.navigation.set([{ text: 'Sign out', routerLink: '/sign-out' }]);
      host.collapseOnMobile.set(true);
      await fixture.whenStable();

      expect(toggle()).not.toBeNull();
      expect(list()?.hasAttribute('hidden')).toBe(true);
    });

    it('should keep the links visible when collapsing is turned off', async () => {
      await render(false);
      host.collapseOnMobile.set(false);
      await fixture.whenStable();

      expect(toggle()).toBeNull();
      expect(list()?.hasAttribute('hidden')).toBe(false);
    });

    it('should give the button a longer label when one is supplied', async () => {
      await render(false);
      host.menuButtonLabel.set('Show or hide menu');
      await fixture.whenStable();

      expect(toggle()?.getAttribute('aria-label')).toBe('Show or hide menu');
    });

    // A label identical to the visible text would be announced twice.
    it('should drop a label that repeats the button text', async () => {
      await render(false);
      host.menuButtonLabel.set('Menu');
      await fixture.whenStable();

      expect(toggle()?.hasAttribute('aria-label')).toBe(false);
    });
  });

  describe('server-side rendering', () => {
    // `matchMedia` and `resize` do not exist on the server, so the component
    // must not reach for either. Removing them makes any attempt throw.
    const renderOnServer = async () => {
      TestBed.overrideProvider(PLATFORM_ID, { useValue: 'server' });
      Reflect.deleteProperty(window, 'matchMedia');

      fixture = TestBed.createComponent(TestComponent);
      host = fixture.componentInstance;
      await fixture.whenStable();
    };

    it('should render without measuring the viewport', async () => {
      await expect(renderOnServer()).resolves.toBeUndefined();
      expect(items()).toHaveLength(3);
    });

    // The same markup GDS ships without JavaScript: links visible, button hidden.
    it('should leave the links visible and the menu button hidden', async () => {
      await renderOnServer();

      expect(list()?.hasAttribute('hidden')).toBe(false);
      expect(toggle()?.hasAttribute('hidden')).toBe(true);
      expect(toggle()?.getAttribute('aria-hidden')).toBe('true');
    });
  });

  describe('security', () => {
    // Angular logs a console warning when it sanitizes an unsafe URL — expected here
    beforeEach(() => {
      vi.spyOn(console, 'warn').mockImplementation(() => undefined);
    });

    afterEach(() => {
      vi.restoreAllMocks();
    });

    it('should block javascript: URLs in the service homepage link', async () => {
      await render();
      host.serviceHomepage.set({ href: 'javascript:alert(1)' });
      await fixture.whenStable();

      expect(serviceNameEl()?.querySelector('a')?.getAttribute('href')).toContain('unsafe:');
    });

    it('should not render the service name as raw HTML', async () => {
      await render();
      host.serviceName.set('<img src=x onerror="alert(1)">');
      await fixture.whenStable();

      expect(serviceNameEl()?.querySelector('img')).toBeNull();
      expect(serviceNameEl()?.textContent).toContain('<img');
    });

    it('should not render item text as raw HTML', async () => {
      await render();
      host.navigation.set([{ text: '<script>alert(1)</script>', routerLink: '/dashboard' }]);
      await fixture.whenStable();

      expect(items()[0].querySelector('script')).toBeNull();
      expect(items()[0].textContent).toContain('<script>');
    });
  });
});
