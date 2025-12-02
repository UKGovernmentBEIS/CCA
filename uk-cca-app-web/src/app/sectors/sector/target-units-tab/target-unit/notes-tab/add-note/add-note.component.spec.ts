import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { AccountNotesService } from 'cca-api';

import { mockTargetUnitAccountDetails } from '../../../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { AddNoteComponent } from './add-note.component';

describe('AddNoteComponent', () => {
  let component: AddNoteComponent;
  let fixture: ComponentFixture<AddNoteComponent>;
  let accountNotesService: jest.Mocked<AccountNotesService>;
  let router: Router;
  let store: ActiveTargetUnitStore;

  beforeEach(async () => {
    const mockAccountNotesService = mockClass(AccountNotesService);
    mockAccountNotesService.createAccountNote = jest.fn().mockReturnValue(of({}));
    mockAccountNotesService.uploadAccountNoteFile = jest.fn().mockReturnValue(
      of({
        type: 4,
        body: { uuid: 'test-uuid' },
      }),
    );

    await TestBed.configureTestingModule({
      imports: [AddNoteComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        ActiveTargetUnitStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: AccountNotesService, useValue: mockAccountNotesService },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveTargetUnitStore);
    store.setState({
      targetUnitAccountDetails: mockTargetUnitAccountDetails,
      underlyingAgreementDetails: null,
    });

    accountNotesService = TestBed.inject(AccountNotesService) as jest.Mocked<AccountNotesService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(AddNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct downloadUrl', () => {
    expect(component['downloadUrl']).toBe('../file-download/');
  });

  it('should initialize form with note and files controls', () => {
    expect(component['form'].get('note')).toBeTruthy();
    expect(component['form'].get('files')).toBeTruthy();
  });

  it('should not submit if form is invalid', () => {
    component['form'].patchValue({ note: '' });
    component.onSubmit();

    expect(accountNotesService.createAccountNote).not.toHaveBeenCalled();
  });

  it('should submit valid form and navigate back', () => {
    const mockNote = 'Test note content';
    const mockFiles = [{ uuid: 'uuid-1', file: new File(['content'], 'test.pdf') }];

    component['form'].patchValue({
      note: mockNote,
      files: mockFiles as any,
    });

    component.onSubmit();

    expect(accountNotesService.createAccountNote).toHaveBeenCalledWith({
      accountId: mockTargetUnitAccountDetails.id,
      note: mockNote,
      files: ['uuid-1'],
    });

    expect(router.navigate).toHaveBeenCalledWith(['../../'], {
      relativeTo: expect.anything(),
      fragment: 'notes',
    });
  });

  it('should handle empty files array', () => {
    component['form'].patchValue({
      note: 'Test note',
      files: [],
    });

    component.onSubmit();

    expect(accountNotesService.createAccountNote).toHaveBeenCalledWith(
      expect.objectContaining({
        files: [],
      }),
    );
  });
});
