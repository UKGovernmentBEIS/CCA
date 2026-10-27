import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { vi } from 'vitest';

import type { GovukLinkTarget } from '../types';
import { GovukHeaderComponent } from './header.component';

describe('GovukHeaderComponent', () => {
  let component: GovukHeaderComponent;
  let fixture: ComponentFixture<TestComponent>;
  let host: TestComponent;

  @Component({
    selector: 'govuk-header-test',
    template: `
      <govuk-header [containerClasses]="containerClasses()" [homepage]="homepage()" [productName]="productName()">
        @if (showActions()) {
          <a class="govuk-link govuk-link--inverse" href="https://example.com/services">Change service</a>
          <a class="govuk-link govuk-link--inverse" href="/sign-out">Sign out</a>
        }
      </govuk-header>
    `,
    imports: [GovukHeaderComponent],
  })
  class TestComponent {
    readonly homepage = signal<GovukLinkTarget | undefined>(undefined);
    readonly productName = signal<string | undefined>(undefined);
    readonly containerClasses = signal('govuk-width-container');
    readonly showActions = signal(false);
  }

  const root = (): HTMLElement => fixture.nativeElement.querySelector('govuk-header');
  const container = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-header__container');
  const homepageLink = (): HTMLAnchorElement | null =>
    fixture.nativeElement.querySelector('.govuk-header__homepage-link');
  const productNameEl = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-header__product-name');
  const actions = (): HTMLElement | null => fixture.nativeElement.querySelector('.govuk-header__actions');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [
        provideRouter([
          { path: 'home', children: [] },
          { path: '**', children: [] },
        ]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    host = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(GovukHeaderComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should carry the govuk-header class on the host element', () => {
    expect(root().classList.contains('govuk-header')).toBe(true);
  });

  describe('logo', () => {
    it('should render the GOV.UK logo SVG', () => {
      const svg = homepageLink()?.querySelector('svg.govuk-header__logotype');
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute('aria-label')).toBe('GOV.UK');
      expect(svg?.getAttribute('role')).toBe('img');
      expect(svg?.getAttribute('focusable')).toBe('false');
      expect(svg?.querySelector('title')?.textContent).toBe('GOV.UK');
    });

    it('should give the homepage link the header link class', () => {
      expect(homepageLink()?.classList.contains('govuk-header__homepage-link')).toBe(true);
    });

    it('should link to gov.uk by default', () => {
      expect(homepageLink()?.getAttribute('href')).toBe('//gov.uk');
    });

    it('should link to an external homepage as a plain href', () => {
      host.homepage.set({ href: 'https://www.gov.uk' });
      fixture.detectChanges();

      expect(homepageLink()?.getAttribute('href')).toBe('https://www.gov.uk');
    });

    it('should route to an internal homepage', () => {
      host.homepage.set({ routerLink: '/home' });
      fixture.detectChanges();

      expect(homepageLink()?.getAttribute('href')).toBe('/home');
      expect(homepageLink()?.querySelector('svg.govuk-header__logotype')).toBeTruthy();
    });

    it('should carry query params and fragments through to the router link', () => {
      host.homepage.set({ routerLink: '/home', queryParams: { lang: 'cy' }, fragment: 'top' });
      fixture.detectChanges();

      expect(homepageLink()?.getAttribute('href')).toBe('/home?lang=cy#top');
    });

    it('should prefer the route when both a route and an href are given', () => {
      host.homepage.set({ routerLink: '/home', href: 'https://www.gov.uk' });
      fixture.detectChanges();

      expect(homepageLink()?.getAttribute('href')).toBe('/home');
    });
  });

  describe('product name', () => {
    it('should not display a product name by default', () => {
      expect(productNameEl()).toBeNull();
    });

    it('should display the product name next to the logo', () => {
      host.productName.set('GOV.UK Pay');
      fixture.detectChanges();

      expect(productNameEl()?.textContent?.trim()).toBe('GOV.UK Pay');
      expect(productNameEl()?.closest('.govuk-header__homepage-link')).toBe(homepageLink());
    });
  });

  describe('container', () => {
    it('should use the standard width container out of the box', () => {
      const bare = TestBed.createComponent(GovukHeaderComponent);
      bare.detectChanges();

      const el: HTMLElement | null = bare.nativeElement.querySelector('.govuk-header__container');
      expect(el?.className).toBe('govuk-header__container govuk-width-container');
    });

    it('should apply the container classes alongside the BEM element class', () => {
      host.containerClasses.set('govuk-width-container govuk-width-container--wide');
      fixture.detectChanges();

      expect(container()?.classList.contains('govuk-header__container')).toBe(true);
      expect(container()?.classList.contains('govuk-width-container')).toBe(true);
      expect(container()?.classList.contains('govuk-width-container--wide')).toBe(true);
    });

    // GDS renders a full-width header by swapping the width container for a
    // modifier class, so no container class must be a supported value.
    it('should span the full width when the width container is dropped', () => {
      host.containerClasses.set('govuk-header__container--full-width');
      fixture.detectChanges();

      expect(container()?.classList.contains('govuk-width-container')).toBe(false);
      expect(container()?.classList.contains('govuk-header__container--full-width')).toBe(true);
    });

    it('should keep the BEM element class when no container classes are given', () => {
      host.containerClasses.set('');
      fixture.detectChanges();

      expect(container()?.className).toBe('govuk-header__container');
    });
  });

  describe('actions', () => {
    it('should render an empty actions area when nothing is projected', () => {
      expect(actions()).not.toBeNull();
      expect(actions()?.children).toHaveLength(0);
    });

    it('should project content into the actions area', () => {
      host.showActions.set(true);
      fixture.detectChanges();

      const links = actions()?.querySelectorAll('a');
      expect(links?.length).toBe(2);
      expect(links?.[0].textContent?.trim()).toBe('Change service');
      expect(links?.[0].classList.contains('govuk-link--inverse')).toBe(true);
      expect(links?.[1].textContent?.trim()).toBe('Sign out');
    });

    it('should keep projected content out of the logo', () => {
      host.showActions.set(true);
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('.govuk-header__logo a:not(.govuk-header__homepage-link)')).toBeNull();
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

    it('should block javascript: URLs in the homepage link', () => {
      host.homepage.set({ href: 'javascript:alert(1)' });
      fixture.detectChanges();

      expect(homepageLink()?.getAttribute('href')).toContain('unsafe:');
    });

    it('should not render the product name as raw HTML', () => {
      host.productName.set('<img src=x onerror="alert(1)">');
      fixture.detectChanges();

      expect(productNameEl()?.querySelector('img')).toBeNull();
      expect(productNameEl()?.textContent).toContain('<img');
    });
  });
});
