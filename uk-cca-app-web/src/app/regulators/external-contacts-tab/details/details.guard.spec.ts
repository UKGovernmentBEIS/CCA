import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';

import { lastValueFrom, of, throwError } from 'rxjs';

import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { ActivatedRouteSnapshotStub } from '@netz/common/testing';

import { CaExternalContactsService } from 'cca-api';

import { viewNotFoundExternalContactError } from '../../errors/business-error';
import { ActiveExternalContactStore } from '../active-external-contact.store';
import { ExternalContactDetailsGuard } from './details.guard';

describe('DetailsGuard', () => {
  let store: ActiveExternalContactStore;
  let caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>>;

  const response = { contact: { id: '1', name: 'Dexter', email: 'dexter@lab.com', description: 'A scientist' } };

  beforeEach(() => {
    caExternalContactsService = {
      getCaExternalContactById: jest.fn().mockReturnValue(of(response)),
    };

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [
        ActiveExternalContactStore,
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
      ],
    });

    store = TestBed.inject(ActiveExternalContactStore);
  });

  function getGuard(route: ActivatedRouteSnapshot) {
    return TestBed.runInInjectionContext(() => ExternalContactDetailsGuard(route));
  }

  it('should allow access and resolve the contact if the contact is found', async () => {
    const route = new ActivatedRouteSnapshotStub({ userId: '1 ' });
    await expect(lastValueFrom(getGuard(route))).resolves.toBeTruthy();

    expect(caExternalContactsService.getCaExternalContactById).toHaveBeenCalledWith(1);

    expect(store.state).toEqual(response);
  });

  it('should display business error page if the contact is not found', async () => {
    caExternalContactsService.getCaExternalContactById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'EXTCONTACT1000' } })),
    );

    await expect(lastValueFrom(getGuard(new ActivatedRouteSnapshotStub({ userId: '1' })))).rejects.toBeTruthy();
    await expectBusinessErrorToBe(viewNotFoundExternalContactError);
  });
});
