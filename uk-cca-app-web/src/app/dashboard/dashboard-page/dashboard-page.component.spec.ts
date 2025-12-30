import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { DashboardPageComponent } from './dashboard-page.component';

class Page extends BasePage<DashboardPageComponent> {
  get assignedToOthersTabLink() {
    return this.query<HTMLAnchorElement>('#tab_assigned-to-others');
  }

  get unassignedTabLink() {
    return this.query<HTMLAnchorElement>('#tab_unassigned');
  }

  get assignedToMeTabLink() {
    return this.query<HTMLAnchorElement>('#tab_assigned-to-me');
  }

  get assignedToMeTab() {
    return this.query<HTMLDivElement>('#assigned-to-me');
  }

  get assignedToOthersTab() {
    return this.query<HTMLDivElement>('#assigned-to-others');
  }

  get unassignedTab() {
    return this.query<HTMLDivElement>('#unassigned');
  }
}

describe('DashboardPageComponent', () => {
  let authStore: AuthStore;
  let component: DashboardPageComponent;
  let fixture: ComponentFixture<DashboardPageComponent>;
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardPageComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      roleType: 'OPERATOR',
      userId: 'opTestId',
    });

    fixture = TestBed.createComponent(DashboardPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('for operators', () => {
    beforeEach(() => {
      authStore.setUserState({ roleType: 'OPERATOR', userId: '331' });
      fixture.detectChanges();
    });

    it('should display the unassigned items', () => {
      expect(page.unassignedTabLink).toBeTruthy();
    });
  });

  it('should NOT render assigned to me table rows', () => {
    expect(page.assignedToMeTabLink).toBeFalsy();
  });
});
