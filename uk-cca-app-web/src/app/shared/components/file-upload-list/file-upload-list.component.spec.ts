import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { FileUploadEvent } from '../file-input/file-upload-event';
import { FileUploadListComponent } from './file-upload-list.component';

describe('FileUploadListComponent', () => {
  let component: FileUploadListComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  const activatedRoute = new ActivatedRouteStub();

  @Component({
    template: `
      <cca-file-upload-list
        [listTitle]="listTitle()"
        [files]="files()"
        (fileDelete)="onDelete($event)"
        [isDisabled]="isDisabled()"
      />
    `,
    imports: [FileUploadListComponent],
    providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
  })
  class TestComponent {
    listTitle = signal<string>('');
    files = signal<FileUploadEvent[]>([]);
    isDisabled = signal<boolean>(false);

    readonly onDeleteSpy = vi.fn<(index: number) => void>();

    onDelete(index: number) {
      this.onDeleteSpy(index);
    }
  }

  class Page extends BasePage<TestComponent> {
    get listTitle() {
      return this.query<HTMLDivElement>('.govuk-heading-m');
    }

    get hidden() {
      return this.query<HTMLDivElement>('.cca-hidden');
    }

    get rows() {
      return this.queryAll<HTMLDivElement>('.cca-multi-file-upload__row');
    }

    get files() {
      return this.queryAll<HTMLDataElement>('.cca-multi-file-upload__message');
    }

    get deleteButtons() {
      return this.queryAll<HTMLButtonElement>('button[name="delete"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(FileUploadListComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list title', () => {
    const listTitle = 'This is a list title';
    hostComponent.listTitle.set(listTitle);
    fixture.detectChanges();

    expect(page.listTitle.textContent).toBe(listTitle);
  });

  it('should hide the element when there are no items', () => {
    expect(page.hidden).toBeTruthy();
  });

  it('should list the files and their status', () => {
    hostComponent.files.set([
      { file: { name: 'Uploaded file' }, uuid: '1234', progress: null },
      { file: new File([], 'Test file'), uuid: '1254', errors: null, progress: 1 },
      { file: new File([], 'Test file 2'), uuid: null, errors: null, progress: 0.3 },
      { file: new File([], 'Test file 3'), uuid: null, errors: { upload: 'Could not upload' }, progress: null },
    ]);

    fixture.detectChanges();

    expect(page.hidden).toBeFalsy();
    expect(page.rows).toHaveLength(4);
    expect(page.files.map((row) => row.textContent.trim())).toEqual([
      'Uploaded file (opens in a new tab)',
      'Test file (opens in a new tab) has been uploaded',
      'Test file 2 (opens in a new tab) 30%',
      'Could not upload',
    ]);
    expect(page.deleteButtons).toHaveLength(4);
  });

  it('should emit whenever a file is deleted', () => {
    hostComponent.onDeleteSpy.mockClear();
    hostComponent.files.set([
      { file: { name: 'Uploaded file' }, uuid: '1234', progress: null },
      { file: new File([], 'Test file 3'), uuid: null, errors: { upload: 'Could not upload' }, progress: null },
    ]);

    fixture.detectChanges();

    page.deleteButtons[0].click();
    fixture.detectChanges();

    expect(hostComponent.onDeleteSpy).toHaveBeenCalledWith(0);
  });

  it('should disable the delete button', () => {
    expect(page.deleteButtons.some((button) => button.disabled)).toBeFalsy();

    hostComponent.isDisabled.set(true);
    fixture.detectChanges();

    expect(page.deleteButtons.every((button) => button.disabled)).toBeTruthy();
  });

  // `FileUploadEvent.file` is required and always set by `FileInputComponent`/
  // `MultipleFileInputComponent`, but the list also renders values that come straight from form
  // controls and API stores. A single malformed entry used to break the whole page with
  // `TypeError: Cannot read properties of undefined (reading 'name')`
  // (thrown from the `progress`/`success` templates of `file-upload-list.component.html`).
  describe('malformed entries', () => {
    const namelessFile = (extra: Partial<FileUploadEvent>): FileUploadEvent =>
      ({ uuid: '1234', ...extra }) as FileUploadEvent;

    it.each([
      ['no file', () => namelessFile({ progress: 0.5 })],
      ['an undefined file', () => namelessFile({ file: undefined, progress: 0.5 })],
      ['a null file', () => namelessFile({ file: null, progress: 0.5 })],
      ['a null file and progress of 1', () => namelessFile({ file: null, progress: 1 })],
      ['an undefined file and progress of 1', () => namelessFile({ file: undefined, progress: 1 })],
      ['a null file and errors', () => namelessFile({ file: null, errors: { upload: 'Could not upload' } })],
    ])('should render a single entry with %s', (_label, entry) => {
      hostComponent.files.set([entry()]);

      expect(() => fixture.detectChanges()).not.toThrow();

      // The single entry is rendered by the single-file branch of the template (no
      // `.cca-multi-file-upload__row` wrapper), so assert on the message and the delete button.
      expect(page.files).toHaveLength(1);
      expect(page.deleteButtons).toHaveLength(1);
      expect(page.files[0].textContent).not.toContain('undefined');
    });

    it('should render several entries when one of them has no file', () => {
      hostComponent.files.set([
        { file: { name: 'Uploaded file' }, uuid: '1234', progress: null },
        namelessFile({ progress: 0.5 }),
      ]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.rows).toHaveLength(2);
      expect(page.files.map((row) => row.textContent.trim())).toEqual([
        'Uploaded file (opens in a new tab)',
        'File (opens in a new tab) 50%',
      ]);
    });

    it('should render several entries that all lack a file', () => {
      hostComponent.files.set([namelessFile({ progress: 0.5 }), namelessFile({ progress: 0.25 })]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.rows).toHaveLength(2);
      expect(page.files.map((row) => row.textContent.trim())).toEqual([
        'File (opens in a new tab) 50%',
        'File (opens in a new tab) 25%',
      ]);
    });

    it('should fall back to a placeholder name when the stored file name is missing', () => {
      // `RequestTaskFileService.buildFileEvent` builds `{ name: storedFileName(...) }`, which is an empty
      // string whenever the attachments map has no entry for the uuid.
      hostComponent.files.set([{ file: { name: undefined }, uuid: '1234' }]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.files.map((row) => row.textContent.trim())).toEqual(['File (opens in a new tab)']);
      expect(page.deleteButtons[0].textContent).toContain('File');
    });

    it('should not render anything when the files input is not an array', () => {
      hostComponent.files.set({ length: 1, 0: namelessFile({ progress: 0.5 }) } as unknown as FileUploadEvent[]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.hidden).toBeTruthy();
      expect(page.rows).toHaveLength(0);
    });

    it('should render the entries of a sparse list', () => {
      const sparse: FileUploadEvent[] = new Array(2);
      sparse[1] = { file: { name: 'Uploaded file' }, uuid: '1234', progress: null };
      hostComponent.files.set(sparse);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.rows).toHaveLength(2);
      expect(page.files.map((row) => row.textContent.trim())).toEqual([
        'File (opens in a new tab)',
        'Uploaded file (opens in a new tab)',
      ]);
    });

    it('should still emit the index of a malformed entry when it is deleted', () => {
      hostComponent.onDeleteSpy.mockClear();
      hostComponent.files.set([{ file: { name: 'Uploaded file' }, uuid: '1234' }, namelessFile({ progress: 0.5 })]);

      fixture.detectChanges();
      page.deleteButtons[1].click();
      fixture.detectChanges();

      expect(hostComponent.onDeleteSpy).toHaveBeenCalledWith(1);
    });
  });
});
