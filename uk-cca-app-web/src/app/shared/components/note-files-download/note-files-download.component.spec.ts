import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { ActivatedRouteStub, mockClass, testSchedulerFactory } from '@netz/common/testing';

import { AccountNotesService, FileDocumentsService } from 'cca-api';

import { NoteFilesDownloadComponent } from './note-files-download.component';

describe('NoteFilesDownloadComponent', () => {
  let component: NoteFilesDownloadComponent;
  let fixture: ComponentFixture<NoteFilesDownloadComponent>;
  let accountNotesService: jest.Mocked<AccountNotesService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });
    accountNotesService = mockClass(AccountNotesService);
    accountNotesService.generateGetAccountFileNoteToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );

    const activatedRoute = new ActivatedRouteStub({ uuid: 'xyz', targetUnitId: 11 });

    await TestBed.configureTestingModule({
      imports: [NoteFilesDownloadComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: AccountNotesService, useValue: accountNotesService },
        { provide: FileDocumentsService, useValue: { configuration: { basePath: '' } } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NoteFilesDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/v1.0/file-notes/abce');
  });

  it('should refresh the download link', async () => {
    accountNotesService.generateGetAccountFileNoteToken.mockClear().mockImplementation(() => {
      let subscribes = 0;

      return defer(() => {
        subscribes += 1;

        return subscribes === 1
          ? of({ token: 'abcf', tokenExpirationMinutes: 1 })
          : subscribes === 2
            ? of({ token: 'abcd', tokenExpirationMinutes: 2 })
            : of({ token: 'abce', tokenExpirationMinutes: 1 });
      });
    });

    testSchedulerFactory().run(({ expectObservable }) =>
      expectObservable(component.url$.pipe(take(3))).toBe('a 59s 999ms b 119s 999ms (c|)', {
        a: '/v1.0/file-notes/abcf',
        b: '/v1.0/file-notes/abcd',
        c: '/v1.0/file-notes/abce',
      }),
    );
  });
});
