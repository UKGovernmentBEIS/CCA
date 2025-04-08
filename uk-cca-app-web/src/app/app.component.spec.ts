import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { BreadcrumbService } from '@netz/common/navigation';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';
import { KeycloakService } from 'keycloak-angular';

import { UserStateDTO } from 'cca-api';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let page: Page;
  let authStore: AuthStore;
  let breadcrumbService: BreadcrumbService;

  const setUser = (roleType: UserStateDTO['roleType'], loginStatus?: UserStateDTO['status']) => {
    authStore.setUserState({ roleType, status: loginStatus });
    fixture.detectChanges();
  };

  class Page extends BasePage<AppComponent> {
    get footer() {
      return this.query<HTMLElement>('.govuk-footer');
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

    get accountsLink() {
      return this.query<HTMLAnchorElement>('a[href="/accounts"]');
    }

    get templatesLink() {
      return this.query<HTMLAnchorElement>('a[href="/templates"]');
    }

    get navList() {
      return this.query<HTMLDivElement>('govuk-header-nav-list');
    }

    get breadcrumbs() {
      return this.queryAll<HTMLLIElement>('.govuk-breadcrumbs__list-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        KeycloakService,
        provideHttpClient(),
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
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should render the footer', () => {
    expect(page.footer).toBeTruthy();
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

  it('should render the regulators link only if the user is regulator', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');
    expect(page.regulatorsLink).toBeFalsy();

    setUser('REGULATOR', 'ENABLED');
    expect(page.regulatorsLink).toBeTruthy();
  });

  it('should not render the nav list if user is disabled', () => {
    setUser('REGULATOR', 'ENABLED');
    expect(page.navList).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();
  });

  it('should not render the nav list if user is not logged in', () => {
    authStore.setIsLoggedIn(true);
    setUser('SECTOR_USER', 'ENABLED');
    expect(page.navList).toBeTruthy();

    authStore.setIsLoggedIn(false);
    setUser('SECTOR_USER', 'NO_AUTHORITY');
    expect(page.navList).toBeFalsy();
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
