import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { vi } from 'vitest';

import type { GovukFooterNavigation, GovukLinkItem } from '../types';
import { GovukFooterComponent } from './footer.component';

describe('GovukFooterComponent', () => {
  let component: GovukFooterComponent;
  let fixture: ComponentFixture<TestComponent>;
  let host: TestComponent;

  @Component({
    selector: 'govuk-footer-test',
    template: `
      <govuk-footer
        [containerClasses]="containerClasses()"
        [copyright]="copyright()"
        [meta]="meta()"
        [metaVisuallyHiddenTitle]="metaVisuallyHiddenTitle()"
        [navigation]="navigation()"
        [showContentLicence]="showContentLicence()"
      >
        <p class="test-meta-content">{{ metaContent() }}</p>
      </govuk-footer>
    `,
    imports: [GovukFooterComponent],
  })
  class TestComponent {
    readonly navigation = signal<GovukFooterNavigation[]>([]);
    readonly meta = signal<GovukLinkItem[]>([]);
    readonly metaVisuallyHiddenTitle = signal('Support links');
    readonly showContentLicence = signal(true);
    readonly copyright = signal('© Crown copyright');
    readonly containerClasses = signal('');
    readonly metaContent = signal('');
  }

  const root = (): HTMLElement => fixture.nativeElement.querySelector('govuk-footer');
  const container = (): HTMLElement | null => fixture.nativeElement.querySelector('govuk-footer > div');
  const navigation = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-footer__navigation');
  const sections = (): HTMLElement[] => Array.from(fixture.nativeElement.querySelectorAll('.govuk-footer__section'));
  const navLinks = (): HTMLAnchorElement[] =>
    Array.from(fixture.nativeElement.querySelectorAll('.govuk-footer__list-item a'));
  const metaLinks = (): HTMLAnchorElement[] =>
    Array.from(fixture.nativeElement.querySelectorAll('.govuk-footer__inline-list-item a'));
  const metaHeading = (): HTMLElement | null =>
    fixture.nativeElement.querySelector('.govuk-footer__meta h2.govuk-visually-hidden');
  const metaCustom = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-footer__meta-custom');
  const licence = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-footer__licence-description');
  const copyright = (): HTMLAnchorElement | null =>
    fixture.nativeElement.querySelector('.govuk-footer__copyright-logo');

  const setNavItems = (items: GovukLinkItem[]) => {
    host.navigation.set([{ title: 'Services', items }]);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [
        provideRouter([
          { path: 'cookies', children: [] },
          { path: '**', children: [] },
        ]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    host = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(GovukFooterComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should carry the govuk-footer class on the host element', () => {
    expect(root().classList.contains('govuk-footer')).toBe(true);
  });

  it('should render the crown logo as a decorative SVG', () => {
    const crown = fixture.nativeElement.querySelector('svg.govuk-footer__crown');
    expect(crown).toBeTruthy();
    expect(crown?.getAttribute('role')).toBe('presentation');
    expect(crown?.getAttribute('focusable')).toBe('false');
  });

  describe('container', () => {
    it('should use the standard width container out of the box', () => {
      const bare = TestBed.createComponent(GovukFooterComponent);
      bare.detectChanges();

      const el: HTMLElement | null = bare.nativeElement.querySelector('div');
      expect(el?.className).toBe('govuk-width-container');
    });

    it('should add container classes alongside the width container', () => {
      host.containerClasses.set('govuk-width-container--wide');
      fixture.detectChanges();

      expect(container()?.classList.contains('govuk-width-container')).toBe(true);
      expect(container()?.classList.contains('govuk-width-container--wide')).toBe(true);
    });
  });

  describe('navigation', () => {
    it('should render no navigation by default', () => {
      expect(navigation()).toBeNull();
      expect(fixture.nativeElement.querySelector('.govuk-footer__section-break')).toBeNull();
    });

    it('should render a section with a heading and its links, followed by a break', () => {
      setNavItems([
        { text: 'Service A', href: '/a' },
        { text: 'Service B', href: '/b' },
      ]);

      expect(navigation()).not.toBeNull();
      expect(sections()[0].querySelector('h2.govuk-footer__heading')?.textContent?.trim()).toBe('Services');
      expect(navLinks().map((link) => link.textContent?.trim())).toEqual(['Service A', 'Service B']);
      expect(fixture.nativeElement.querySelector('.govuk-footer__section-break')).not.toBeNull();
    });

    it('should default the section width to full', () => {
      setNavItems([{ text: 'Service A', href: '/a' }]);

      expect(sections()[0].classList.contains('govuk-grid-column-full')).toBe(true);
    });

    it('should apply the specified section widths', () => {
      host.navigation.set([
        { title: 'Services and information', width: 'two-thirds', items: [{ text: 'Service A', href: '/a' }] },
        { title: 'Departments and policy', width: 'one-third', items: [{ text: 'Departments', href: '/departments' }] },
      ]);
      fixture.detectChanges();

      expect(sections()[0].classList.contains('govuk-grid-column-two-thirds')).toBe(true);
      expect(sections()[1].classList.contains('govuk-grid-column-one-third')).toBe(true);
    });

    it('should render a single-column list by default', () => {
      setNavItems([{ text: 'Link', href: '/link' }]);

      const list = fixture.nativeElement.querySelector('.govuk-footer__list');
      expect(list?.className).toBe('govuk-footer__list');
    });

    it('should split the list over the requested number of columns', () => {
      host.navigation.set([{ title: 'Section', columns: 3, items: [{ text: 'Link', href: '/link' }] }]);
      fixture.detectChanges();

      const list = fixture.nativeElement.querySelector('.govuk-footer__list');
      expect(list?.classList.contains('govuk-footer__list--columns-3')).toBe(true);
    });

    it('should render the heading without a list when a section has no items', () => {
      host.navigation.set([{ title: 'Empty', items: [] }]);
      fixture.detectChanges();

      expect(sections()[0].querySelector('h2')?.textContent?.trim()).toBe('Empty');
      expect(sections()[0].querySelector('ul')).toBeNull();
    });
  });

  describe('meta', () => {
    it('should render no support links or hidden heading by default', () => {
      expect(metaHeading()).toBeNull();
      expect(fixture.nativeElement.querySelector('.govuk-footer__inline-list')).toBeNull();
    });

    it('should render the support links', () => {
      host.meta.set([
        { text: 'Cookies', href: '/cookies' },
        { text: 'Privacy', href: '/privacy' },
      ]);
      fixture.detectChanges();

      expect(metaLinks().map((link) => link.textContent?.trim())).toEqual(['Cookies', 'Privacy']);
    });

    it('should title the support links for assistive technology', () => {
      host.meta.set([{ text: 'Cookies', href: '/cookies' }]);
      fixture.detectChanges();

      expect(metaHeading()?.textContent?.trim()).toBe('Support links');
    });

    it('should accept a custom hidden title', () => {
      host.meta.set([{ text: 'Cookies', href: '/cookies' }]);
      host.metaVisuallyHiddenTitle.set('Footer links');
      fixture.detectChanges();

      expect(metaHeading()?.textContent?.trim()).toBe('Footer links');
    });

    it('should project custom content below the support links', () => {
      host.metaContent.set('Built by the Environment Agency');
      fixture.detectChanges();

      const projected = metaCustom()?.querySelector('.test-meta-content');
      expect(projected?.textContent).toBe('Built by the Environment Agency');
      expect(metaCustom()?.closest('.govuk-footer__meta-item--grow')).not.toBeNull();
    });
  });

  describe('licence', () => {
    it('should show the Open Government Licence by default', () => {
      expect(fixture.nativeElement.querySelector('.govuk-footer__licence-logo')).not.toBeNull();
      expect(licence()?.textContent).toContain('Open Government Licence');

      const link = licence()?.querySelector('a');
      expect(link?.classList.contains('govuk-footer__link')).toBe(true);
      expect(link?.getAttribute('rel')).toBe('license');
      expect(link?.getAttribute('href')).toBe(
        'https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/',
      );
    });

    it('should hide the licence when asked to', () => {
      host.showContentLicence.set(false);
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('.govuk-footer__licence-logo')).toBeNull();
      expect(licence()).toBeNull();
    });
  });

  describe('copyright', () => {
    it('should link Crown copyright to the National Archives by default', () => {
      expect(copyright()?.textContent?.trim()).toBe('© Crown copyright');
      expect(copyright()?.classList.contains('govuk-footer__link')).toBe(true);
      expect(copyright()?.getAttribute('href')).toContain('nationalarchives.gov.uk');
    });

    it('should display custom copyright text', () => {
      host.copyright.set('© Custom copyright');
      fixture.detectChanges();

      expect(copyright()?.textContent?.trim()).toBe('© Custom copyright');
    });
  });

  describe('link targets', () => {
    it('should route a navigation item that has a routerLink', () => {
      setNavItems([{ text: 'Cookies', routerLink: '/cookies' }]);

      expect(navLinks()[0].getAttribute('href')).toBe('/cookies');
    });

    it('should route a meta item that has a routerLink', () => {
      host.meta.set([{ text: 'Cookies', routerLink: '/cookies' }]);
      fixture.detectChanges();

      expect(metaLinks()[0].getAttribute('href')).toBe('/cookies');
    });

    it('should carry query params and fragments into the route', () => {
      setNavItems([{ text: 'Cookies', routerLink: '/cookies', queryParams: { from: 'footer' }, fragment: 'settings' }]);

      expect(navLinks()[0].getAttribute('href')).toBe('/cookies?from=footer#settings');
    });

    it('should render a plain href for a link that leaves the app', () => {
      setNavItems([{ text: 'GOV.UK', href: 'https://www.gov.uk' }]);

      expect(navLinks()[0].getAttribute('href')).toBe('https://www.gov.uk');
    });

    it('should prefer the route when both a route and an href are given', () => {
      setNavItems([{ text: 'Cookies', routerLink: '/cookies', href: 'https://www.gov.uk' }]);

      expect(navLinks()[0].getAttribute('href')).toBe('/cookies');
    });

    it('should give every link the footer link class', () => {
      setNavItems([
        { text: 'Cookies', routerLink: '/cookies' },
        { text: 'GOV.UK', href: 'https://www.gov.uk' },
      ]);
      host.meta.set([
        { text: 'Privacy', routerLink: '/privacy' },
        { text: 'Contact', href: 'https://www.gov.uk/contact' },
      ]);
      fixture.detectChanges();

      for (const link of [...navLinks(), ...metaLinks()]) {
        expect(link.classList.contains('govuk-footer__link')).toBe(true);
      }
    });

    describe('new tabs', () => {
      it('should default target="_blank" links to a safe rel', () => {
        setNavItems([{ text: 'GOV.UK', href: 'https://www.gov.uk', target: '_blank' }]);

        expect(navLinks()[0].getAttribute('target')).toBe('_blank');
        expect(navLinks()[0].getAttribute('rel')).toBe('noreferrer noopener');
      });

      it('should keep an explicit rel', () => {
        setNavItems([{ text: 'Licence', href: 'https://www.gov.uk', target: '_blank', rel: 'license' }]);

        expect(navLinks()[0].getAttribute('rel')).toBe('license');
      });

      it('should not add a rel or target to a link that opens in the same tab', () => {
        setNavItems([{ text: 'GOV.UK', href: 'https://www.gov.uk' }]);

        expect(navLinks()[0].hasAttribute('rel')).toBe(false);
        expect(navLinks()[0].hasAttribute('target')).toBe(false);
      });

      it('should apply the same rules to meta links', () => {
        host.meta.set([{ text: 'Contact', href: 'https://www.gov.uk/contact', target: '_blank' }]);
        fixture.detectChanges();

        expect(metaLinks()[0].getAttribute('target')).toBe('_blank');
        expect(metaLinks()[0].getAttribute('rel')).toBe('noreferrer noopener');
      });
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

    it('should block javascript: URLs in navigation links', () => {
      setNavItems([{ text: 'Click', href: 'javascript:alert(1)' }]);

      expect(navLinks()[0].getAttribute('href')).toContain('unsafe:');
    });
  });
});
