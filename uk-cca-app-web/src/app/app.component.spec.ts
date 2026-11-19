import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { BreadcrumbService } from '@netz/common/navigation';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';
import { AnalyticsService, KeycloakService, SignInTrackingService } from '@shared/services';

import { UserStateDTO } from 'cca-api';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let page: Page;
  let authStore: AuthStore;
  let breadcrumbService: BreadcrumbService;

  // The service navigation reads `matchMedia` while constructing its `isDesktop`
  // signal, so the viewport has to be decided before the fixture exists.
  const realMatchMedia = window.matchMedia;

  const analyticsService = { push: vi.fn(), enableGoogleTagManager: vi.fn() };

  const serviceContextEvents = () =>
    analyticsService.push.mock.calls
      .map(([event]) => event as Record<string, unknown>)
      .filter((event) => event['event'] === 'service_context');
  const lastServiceContextEvent = () => serviceContextEvents().at(-1);

  const createApp = () => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  const setUser = (roleType: UserStateDTO['roleType'], loginStatus?: UserStateDTO['status']) => {
    authStore.setUserState({ roleType, status: loginStatus });
    fixture.detectChanges();
  };

  class Page extends BasePage<AppComponent> {
    get footer() {
      return this.query<HTMLElement>('.govuk-footer');
    }

    get headerLandmark() {
      return this.query<HTMLElement>('header.govuk-template__header');
    }

    get footerLandmark() {
      return this.query<HTMLElement>('footer.govuk-template__footer');
    }

    get dashboardLink() {
      return this.query<HTMLAnchorElement>('a[href="/dashboard"]');
    }

    get targetUnitAccountsLink() {
      return this.query<HTMLAnchorElement>('a[href="/target-unit-accounts"]');
    }

    get sectorsLink() {
      return this.query<HTMLAnchorElement>('a[href="/sectors"]');
    }

    get regulatorsLink() {
      return this.query<HTMLAnchorElement>('a[href="/user/regulators"]');
    }

    get buyOutSurplusLink() {
      return this.query<HTMLAnchorElement>('a[href="/buyout-surplus?paymentStatus=AWAITING_PAYMENT"]');
    }

    get templatesLink() {
      return this.query<HTMLAnchorElement>('a[href="/templates"]');
    }

    get navigationList() {
      return this.query<HTMLUListElement>('.govuk-service-navigation__list');
    }

    get serviceNameLink() {
      return this.query<HTMLAnchorElement>('.govuk-service-navigation__service-name a');
    }

    get breadcrumbs() {
      return this.queryAll<HTMLLIElement>('.govuk-breadcrumbs__list-item');
    }

    get taskInformationRegion() {
      return this.query<HTMLElement>('section[aria-label="Task information"]');
    }

    get phaseBar() {
      return this.query<HTMLElement>('cca-phase-bar');
    }

    get workflowTaskHeader() {
      return this.query<HTMLElement>('cca-workflow-task-header');
    }

    get mainContent() {
      return this.query<HTMLElement>('main');
    }

    get breadcrumbsElement() {
      return this.query<HTMLElement>('netz-breadcrumbs');
    }

    get backLink() {
      return this.query<HTMLElement>('netz-back-link');
    }
  }

  beforeEach(async () => {
    window.matchMedia = ((query: string) => ({ matches: true, media: query })) as typeof window.matchMedia;

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter([]),
        KeycloakService,
        { provide: AnalyticsService, useValue: analyticsService },
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(),
        },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ roleType: 'OPERATOR', status: 'NO_AUTHORITY' });
    breadcrumbService = TestBed.inject(BreadcrumbService);
    sessionStorage.clear();
    createApp();
    page = new Page(fixture);
  });

  afterEach(() => {
    sessionStorage.clear();
    window.matchMedia = realMatchMedia;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should report the signed in user type to analytics on page load', () => {
    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'operator' });
  });

  it('should not report a page load when the user is not signed in', () => {
    authStore.reset();
    analyticsService.push.mockClear();

    createApp();

    expect(analyticsService.push).not.toHaveBeenCalled();

    authStore.setIsLoggedIn(false);
    analyticsService.push.mockClear();

    createApp();

    expect(analyticsService.push).not.toHaveBeenCalled();
  });

  it('should report an unknown user type when the role type is missing or empty', () => {
    authStore.setUserState({ status: 'ENABLED' });

    createApp();

    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'unknown' });

    authStore.setUserState({ roleType: '', status: 'ENABLED' });

    createApp();

    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'unknown' });
  });

  it('should report an unfamiliar role type in lowercase', () => {
    authStore.setUserState({ roleType: 'NEW_SECTOR_ROLE', status: 'ENABLED' });

    createApp();

    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'new_sector_role' });
  });

  it('should report a completed sign-in and clear the sign-in flag', () => {
    sessionStorage.setItem(SignInTrackingService.SIGN_IN_STARTED_KEY, String(Date.now()));

    createApp();

    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'operator', login: true });
    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should not report a completed sign-in for an abandoned sign-in', () => {
    sessionStorage.setItem(SignInTrackingService.SIGN_IN_STARTED_KEY, String(Date.now() - 31 * 60 * 1000));

    createApp();

    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'operator' });
    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should not report a completed sign-in for an existing session', () => {
    createApp();

    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'operator' });
    expect(lastServiceContextEvent()).not.toHaveProperty('login');
  });

  it('should clear the sign-in flag when the user is not signed in', () => {
    sessionStorage.setItem(SignInTrackingService.SIGN_IN_STARTED_KEY, String(Date.now()));
    authStore.reset();
    analyticsService.push.mockClear();

    createApp();

    expect(analyticsService.push).not.toHaveBeenCalled();
    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should not enable Tag Manager before analytics cookies have been accepted', () => {
    expect(analyticsService.enableGoogleTagManager).not.toHaveBeenCalled();
    expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'operator' });
  });

  it('should still report the page load when session storage is unavailable', () => {
    const getItemSpy = vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('session storage is unavailable');
    });

    try {
      createApp();

      expect(lastServiceContextEvent()).toEqual({ event: 'service_context', user_type: 'operator' });
    } finally {
      getItemSpy.mockRestore();
    }
  });

  it('should render the footer', () => {
    expect(page.footer).toBeTruthy();
  });

  // The header and footer components render plain <div> elements, matching the
  // GOV.UK Frontend v6 macros, so the page template wrappers carry the landmarks.
  it('should wrap the header and footer in the page template landmarks', () => {
    expect(page.headerLandmark).toBeTruthy();
    expect(page.headerLandmark.contains(page.serviceNameLink)).toBe(true);
    expect(page.footerLandmark).toBeTruthy();
    expect(page.footerLandmark.contains(page.footer)).toBe(true);
  });

  it('should not render the dashboard link for disabled users or a sector user with no authority', () => {
    setUser('SECTOR_USER', 'NO_AUTHORITY');
    expect(page.dashboardLink).toBeFalsy();

    setUser('SECTOR_USER', 'ENABLED');
    expect(page.dashboardLink).toBeTruthy();
    expect(page.targetUnitAccountsLink).toBeTruthy();
    expect(page.sectorsLink).toBeTruthy();

    setUser('REGULATOR', 'ENABLED');
    expect(page.dashboardLink).toBeTruthy();
    expect(page.targetUnitAccountsLink).toBeTruthy();
    expect(page.sectorsLink).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');
    expect(page.dashboardLink).toBeFalsy();
    expect(page.targetUnitAccountsLink).toBeFalsy();
    expect(page.sectorsLink).toBeFalsy();
  });

  it('should link the service name to the dashboard for authorized users and to the landing page otherwise', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');
    expect(page.serviceNameLink?.getAttribute('href')).toBe('/landing');

    setUser('REGULATOR', 'ENABLED');
    expect(page.serviceNameLink?.getAttribute('href')).toBe('/dashboard');
  });

  it('should render the regulators link only if the user is regulator', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');
    expect(page.regulatorsLink).toBeFalsy();

    setUser('REGULATOR', 'ENABLED');
    expect(page.regulatorsLink).toBeTruthy();
  });

  it('should link to buy-out and surplus with the default payment status for regulators', () => {
    setUser('SECTOR_USER', 'ENABLED');
    expect(page.buyOutSurplusLink).toBeFalsy();

    setUser('REGULATOR', 'ENABLED');
    expect(page.buyOutSurplusLink?.getAttribute('href')).toBe('/buyout-surplus?paymentStatus=AWAITING_PAYMENT');
  });

  it('should not render the navigation list if user is disabled', () => {
    setUser('REGULATOR', 'ENABLED');
    expect(page.navigationList).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');
    fixture.detectChanges();

    expect(page.navigationList).toBeFalsy();
  });

  it('should not render the navigation list if user is not logged in', () => {
    authStore.setIsLoggedIn(true);
    setUser('SECTOR_USER', 'ENABLED');
    expect(page.navigationList).toBeTruthy();

    authStore.setIsLoggedIn(false);
    setUser('SECTOR_USER', 'NO_AUTHORITY');
    expect(page.navigationList).toBeFalsy();
  });

  it('should render the templates link only for sector users and regulators', () => {
    setUser('SECTOR_USER', 'ENABLED');
    expect(page.query<HTMLAnchorElement>('a[href="/sector-templates"]')).toBeTruthy();

    setUser('OPERATOR', 'ENABLED');
    expect(page.templatesLink).toBeFalsy();
    expect(page.query<HTMLAnchorElement>('a[href="/sector-templates"]')).toBeFalsy();

    setUser('REGULATOR', 'ENABLED');
    expect(page.templatesLink).toBeTruthy();
  });

  it('should place the phase and task header inside a named region, with breadcrumbs and back link outside main', () => {
    expect(page.taskInformationRegion).toBeTruthy();
    expect(page.taskInformationRegion.contains(page.workflowTaskHeader)).toBe(true);

    expect(page.phaseBar).toBeTruthy();
    expect(page.taskInformationRegion.contains(page.phaseBar)).toBe(false);

    expect(page.mainContent).toBeTruthy();
    expect(page.mainContent.contains(page.breadcrumbsElement)).toBe(false);
    expect(page.mainContent.contains(page.backLink)).toBe(false);
  });

  it('should display breadcrumbs', () => {
    expect(page.breadcrumbs).toEqual([]);

    breadcrumbService.links.set([{ text: 'Dashboard', link: ['/dashboard'] }]);
    fixture.detectChanges();
    expect(Array.from(page.breadcrumbs).map((breacrumb) => breacrumb.textContent)).toEqual(['Dashboard']);

    expect(page.breadcrumbs[0].querySelector<HTMLAnchorElement>('a').href).toContain('/dashboard');
    expect(page.breadcrumbs.length).toBe(1);

    breadcrumbService.show([]);
    fixture.detectChanges();

    expect(page.breadcrumbs).toEqual([]);
  });
});
