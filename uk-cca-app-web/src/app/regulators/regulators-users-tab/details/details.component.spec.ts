import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { clear, click, type } from '@testing';

import { AuthoritiesService, RegulatorUsersService } from 'cca-api';

import { mockDetailsRouteDataAdd, mockRegulatorBasePermissions, mockRegulatorUserState } from '../../testing/mock-data';
import { addUserState, editorUserState, viewerUserState } from '../../testing/mock-details-store';
import { DetailsComponent } from './details.component';
import { DetailsStore } from './details.store';

describe('RegulatorDetailsComponent', () => {
  const routeEdit = new ActivatedRouteStub({ userId: mockRegulatorUserState.userId }, null);
  const routeEdit2 = new ActivatedRouteStub({ userId: '123' }, null);
  const routeView = new ActivatedRouteStub({ userId: mockRegulatorUserState.userId }, null);
  const routeAdd = new ActivatedRouteStub(null, null, mockDetailsRouteDataAdd);

  let fixture: ComponentFixture<DetailsComponent>;
  let authoritiesService: Partial<jest.Mocked<AuthoritiesService>>;
  let regulatorUsersService: Partial<jest.Mocked<RegulatorUsersService>>;
  let authStore: AuthStore;
  let detailsStore: DetailsStore;

  async function bootstrap(
    route: ActivatedRouteStub,
    opts: { add: boolean; edit: boolean } = { add: false, edit: true },
  ) {
    authoritiesService = {
      getRegulatorRoles: jest.fn().mockReturnValue(of(mockRegulatorBasePermissions)),
    };

    regulatorUsersService = {
      inviteRegulatorUserToCA: jest.fn().mockReturnValue(of(null)),
      updateCurrentRegulatorUser: jest.fn().mockReturnValue(of(null)),
      updateRegulatorUserByCaAndId: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      imports: [DetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        DetailsStore,
        {
          provide: ActivatedRoute,
          useValue: route,
        },
        {
          provide: AuthoritiesService,
          useValue: authoritiesService,
        },
        {
          provide: RegulatorUsersService,
          useValue: regulatorUsersService,
        },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState(mockRegulatorUserState);
    detailsStore = TestBed.inject(DetailsStore);

    if (opts.add) detailsStore.setState(addUserState);
    else opts.edit ? detailsStore.setState(editorUserState) : detailsStore.setState(viewerUserState);

    fixture = TestBed.createComponent(DetailsComponent);
    fixture.detectChanges();
  }

  it('should render header', async () => {
    await bootstrap(routeEdit);
    expect(fixture.nativeElement.textContent).toContain('User details');
  });

  it('should render details properly', async () => {
    await bootstrap(routeEdit);
    expect(
      (fixture.nativeElement.querySelector('#user\\.firstName') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe(editorUserState.user.firstName);
    expect(
      (fixture.nativeElement.querySelector('#user\\.lastName') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe(editorUserState.user.lastName);
    expect(
      (fixture.nativeElement.querySelector('#user\\.jobTitle') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe(editorUserState.user.jobTitle);
    expect(
      (fixture.nativeElement.querySelector('#user\\.email') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe(editorUserState.user.email);
    expect(
      (fixture.nativeElement.querySelector('#user\\.email') as { disabled?: boolean } | null)?.disabled ?? false,
    ).toBe(true);
    expect(
      (fixture.nativeElement.querySelector('#user\\.phoneNumber') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe(editorUserState.user.phoneNumber);
    expect(
      (fixture.nativeElement.querySelector('#user\\.mobileNumber') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe(editorUserState.user.mobileNumber);
    expect(
      fixture.nativeElement.querySelector('cca-file-input')?.textContent.includes(editorUserState.user.signature.name),
    ).toBeTruthy();
    expect(fixture.nativeElement.querySelector('#regulator_administrator')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('#regulator_basic_user')).toBeTruthy();
  });

  it('should show radios if editable is true', async () => {
    await bootstrap(routeEdit);
    expect(fixture.nativeElement.querySelectorAll("input[type='radio']")).toHaveLength(12);
  });

  it('change permissions on administrator click', async () => {
    await bootstrap(routeEdit);
    click(fixture.nativeElement.querySelector('#regulator_administrator'));
    fixture.detectChanges();
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_SECTOR_ASSOCIATIONS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ASSIGN_REASSIGN_TASKS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_USERS_AND_CONTACTS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_SECTOR_USERS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_SUBMISSION-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_PEER_REVIEW-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_SECTOR_ASSOCIATIONS-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ASSIGN_REASSIGN_TASKS-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_USERS_AND_CONTACTS-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (fixture.nativeElement.querySelector('#permissions\\.MANAGE_SECTOR_USERS-optionNONE') as HTMLInputElement | null)
        ?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_SUBMISSION-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_PEER_REVIEW-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
  });

  it('change permissions on base user click', async () => {
    await bootstrap(routeEdit);
    click(fixture.nativeElement.querySelector('#regulator_basic_user'));
    fixture.detectChanges();
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_SECTOR_ASSOCIATIONS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ASSIGN_REASSIGN_TASKS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_USERS_AND_CONTACTS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_SECTOR_USERS-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_SUBMISSION-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_PEER_REVIEW-optionEXECUTE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_SECTOR_ASSOCIATIONS-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ASSIGN_REASSIGN_TASKS-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(false);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.MANAGE_USERS_AND_CONTACTS-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (fixture.nativeElement.querySelector('#permissions\\.MANAGE_SECTOR_USERS-optionNONE') as HTMLInputElement | null)
        ?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_SUBMISSION-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
    expect(
      (
        fixture.nativeElement.querySelector(
          '#permissions\\.ADMIN_TERMINATION_PEER_REVIEW-optionNONE',
        ) as HTMLInputElement | null
      )?.checked ?? false,
    ).toBe(true);
  });

  it('should render viewer properly', async () => {
    await bootstrap(routeView, { add: false, edit: false });
    expect(
      (fixture.nativeElement.querySelector('#user\\.firstName') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe(viewerUserState.user.firstName);
    expect(
      (fixture.nativeElement.querySelector('#user\\.lastName') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe(viewerUserState.user.lastName);
    expect(
      (fixture.nativeElement.querySelector('#user\\.jobTitle') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe(viewerUserState.user.jobTitle);
    expect(
      (fixture.nativeElement.querySelector('#user\\.email') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe(viewerUserState.user.email);
    expect(
      (fixture.nativeElement.querySelector('#user\\.email') as { disabled?: boolean } | null)?.disabled ?? false,
    ).toBe(true);
    expect(
      (fixture.nativeElement.querySelector('#user\\.phoneNumber') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe(viewerUserState.user.phoneNumber);
    expect(
      (fixture.nativeElement.querySelector('#user\\.mobileNumber') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe(viewerUserState.user.mobileNumber);
    expect(
      fixture.nativeElement.querySelector('cca-file-input')?.textContent.includes(viewerUserState.user.signature.name),
    ).toBeTruthy();
    expect(fixture.nativeElement.querySelector('#regulator_administrator')).toBeFalsy();
    expect(fixture.nativeElement.querySelector('#regulator_basic_user')).toBeFalsy();
    expect(fixture.nativeElement.textContent).toContain('Permissions');
  });

  it('should render add regulator property', async () => {
    await bootstrap(routeAdd, { add: true, edit: true });
    expect(
      (fixture.nativeElement.querySelector('#user\\.firstName') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('');
    expect(
      (fixture.nativeElement.querySelector('#user\\.lastName') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe('');
    expect(
      (fixture.nativeElement.querySelector('#user\\.jobTitle') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe('');
    expect(
      (fixture.nativeElement.querySelector('#user\\.email') as HTMLInputElement | HTMLSelectElement | null)?.value ??
        '',
    ).toBe('');
    expect(
      (fixture.nativeElement.querySelector('#user\\.email') as { disabled?: boolean } | null)?.disabled ?? false,
    ).toBe(false);
    expect(
      (fixture.nativeElement.querySelector('#user\\.phoneNumber') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('');
    expect(
      (fixture.nativeElement.querySelector('#user\\.mobileNumber') as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('');
    expect(fixture.nativeElement.querySelector('#regulator_administrator')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('#regulator_basic_user')).toBeTruthy();
  });

  it('should submit new regulator', async () => {
    await bootstrap(routeAdd, { add: true, edit: true });
    const spy = jest.spyOn(regulatorUsersService, 'inviteRegulatorUserToCA');
    type(fixture.nativeElement.querySelector('#user\\.firstName'), 'George');
    type(fixture.nativeElement.querySelector('#user\\.lastName'), 'Mitau');
    type(fixture.nativeElement.querySelector('#user\\.jobTitle'), 'Job Title for George');
    type(fixture.nativeElement.querySelector('#user\\.email'), 'georgemitau@cca.uk');
    type(fixture.nativeElement.querySelector('#user\\.phoneNumber'), '123123123');
    click(fixture.nativeElement.querySelector('#regulator_basic_user'));
    fixture.detectChanges();
    const signature = new File(['image bytes'], 'sample.bmp');
    const uploader = fixture.nativeElement.querySelector('input[type="file"]');
    Object.defineProperty(uploader, 'files', {
      value: [signature],
      writable: false,
    });
    uploader.dispatchEvent(new Event('change', { bubbles: true }));
    fixture.detectChanges();
    const submitBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Submit'),
    ) as HTMLButtonElement;
    click(submitBtn);
    expect(spy).toHaveBeenCalled();
  });

  it('should show first name error if empty was submitted', async () => {
    await bootstrap(routeEdit);
    clear(fixture.nativeElement.querySelector('#user\\.firstName'));
    const saveBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Save'),
    ) as HTMLButtonElement;
    click(saveBtn);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('There is a problem');
    expect(fixture.nativeElement.textContent.match(/Enter user's first name/g)?.length ?? 0).toBeGreaterThan(0);
  });

  it('should show last name error if empty was submitted', async () => {
    await bootstrap(routeEdit);
    clear(fixture.nativeElement.querySelector('#user\\.lastName'));
    const saveBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Save'),
    ) as HTMLButtonElement;
    click(saveBtn);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('There is a problem');
    expect(fixture.nativeElement.textContent.match(/Enter user's last name/g)?.length ?? 0).toBeGreaterThan(0);
  });

  it('should show multiple errors if present', async () => {
    await bootstrap(routeEdit);
    clear(fixture.nativeElement.querySelector('#user\\.firstName'));
    clear(fixture.nativeElement.querySelector('#user\\.lastName'));
    const saveBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Save'),
    ) as HTMLButtonElement;
    click(saveBtn);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('There is a problem');
    expect(fixture.nativeElement.textContent.match(/Enter user's first name/g)?.length ?? 0).toBeGreaterThan(0);
    expect(fixture.nativeElement.textContent.match(/Enter user's last name/g)?.length ?? 0).toBeGreaterThan(0);
  });

  it('should show signature file error', async () => {
    await bootstrap(routeEdit);
    const deleteBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Delete'),
    ) as HTMLButtonElement;
    click(deleteBtn);
    fixture.detectChanges();
    const saveBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Save'),
    ) as HTMLButtonElement;
    click(saveBtn);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('There is a problem');
    expect(fixture.nativeElement.textContent.match(/Select a file/g)?.length ?? 0).toBeGreaterThan(0);
  });

  it('should submit a valid form for current user', async () => {
    await bootstrap(routeEdit, { add: false, edit: false });
    const spy = jest.spyOn(regulatorUsersService, 'updateCurrentRegulatorUser');
    type(fixture.nativeElement.querySelector('#user\\.firstName'), 'Johnathan');
    const saveBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Save'),
    ) as HTMLButtonElement;
    click(saveBtn);
    expect(spy).toHaveBeenCalled();
  });

  it('should submit a valid form not for current user', fakeAsync(async () => {
    await bootstrap(routeEdit2);
    const spy = jest.spyOn(regulatorUsersService, 'updateRegulatorUserByCaAndId');
    type(fixture.nativeElement.querySelector('#user\\.firstName'), 'Johnathan');
    const saveBtn = Array.from(fixture.nativeElement.querySelectorAll('button')).find((el: any) =>
      el.textContent.includes('Save'),
    ) as HTMLButtonElement;
    click(saveBtn);
    expect(spy).toHaveBeenCalled();
  }));
});
