import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of, throwError } from 'rxjs';

import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub, BasePage, expectToHaveNavigatedTo, RouterStubComponent } from '@netz/common/testing';
import { AuthService } from '@shared/services';

import { RegulatorAuthoritiesService, RegulatorUserDTO } from 'cca-api';

import { saveNotFoundRegulatorError } from '../../errors/business-error';
import { DetailsStore } from '../details/details.store';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let page: Page;
  let authStore: AuthStore;
  let detailsStore: DetailsStore;
  class Page extends BasePage<DeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h2');
    }

    get cancelLink() {
      return this.queryAll<HTMLAnchorElement>('a').find((element) => element.textContent.trim() === 'Cancel');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }

    get panelTitle() {
      return this.query<HTMLDivElement>('.govuk-panel__title');
    }

    get returnLink() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const user: RegulatorUserDTO = {
    email: 'alfyn-octo@cca.uk',
    firstName: 'Alfyn',
    lastName: 'Octo',
    jobTitle: '',
    phoneNumber: '',
  };

  let regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>>;

  let authService: Partial<jest.Mocked<AuthService>>;

  beforeEach(async () => {
    const activatedRoute = new ActivatedRouteStub({ userId: '1reg' }, null, { user });
    authService = {
      logout: jest.fn(),
    };
    regulatorAuthoritiesService = {
      deleteRegulatorUserByCompetentAuthority: jest.fn().mockReturnValue(of(null)),
      deleteCurrentRegulatorUserByCompetentAuthority: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      imports: [DeleteComponent, BusinessTestingModule, RouterStubComponent],
      providers: [
        DetailsStore,
        provideRouter([{ path: 'user/regulators', component: RouterStubComponent }]),
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    detailsStore = TestBed.inject(DetailsStore);
    detailsStore.update({ user });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain a heading with regulator name', () => {
    expect(page.header.textContent).toContain(`${user.firstName} ${user.lastName}`);
  });

  it('should contain cancel and delete buttons', () => {
    expect(page.cancelLink.textContent.trim()).toEqual('Cancel');
    expect(page.submitButton.textContent.trim()).toEqual('Confirm deletion');
  });

  it('should return without reload on cancel click', () => {
    page.cancelLink.click();

    expectToHaveNavigatedTo('/user/regulators#regulator-users');
  });

  it('should delete regulator and logout if current user', () => {
    authStore.setUserState({ userId: '1reg' });

    page.submitButton.click();

    expect(regulatorAuthoritiesService.deleteCurrentRegulatorUserByCompetentAuthority).toHaveBeenCalled();
    expect(regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthority).not.toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalled();
  });

  it('should delete regulator on confirm delete click', () => {
    authStore.setUserState({ userId: '1' });

    page.submitButton.click();

    expect(regulatorAuthoritiesService.deleteCurrentRegulatorUserByCompetentAuthority).not.toHaveBeenCalled();
    expect(regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthority).toHaveBeenCalledWith('1reg');
  });

  it('should show confirmation screen on delete', () => {
    authStore.setUserState({ userId: '1' });

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.panelTitle.textContent).toContain(user.firstName);
    expect(page.panelTitle.textContent).toContain(user.lastName);

    page.returnLink.click();

    expectToHaveNavigatedTo('/user/regulators#regulator-users');
  });

  it('should dismiss with a message if error', async () => {
    authStore.setUserState({ userId: '1' });
    regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthority.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1003' }, status: 400 })),
    );

    page.submitButton.click();

    await expectBusinessErrorToBe(saveNotFoundRegulatorError);
  });
});
