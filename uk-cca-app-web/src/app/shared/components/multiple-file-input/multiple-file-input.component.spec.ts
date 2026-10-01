import { HttpEvent, HttpEventType, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { Subject } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { FileUuidDTO } from 'cca-api';

import { FileUploadService } from '../file-input/file-upload.service';
import { FileUploadEvent } from '../file-input/file-upload-event';
import { FileValidators } from '../file-input/file-validators';
import { MultipleFileInputComponent } from './multiple-file-input.component';

describe('MultipleFileInputComponent', () => {
  let component: MultipleFileInputComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;
  let control: FormControl;

  const activatedRoute = new ActivatedRouteStub();

  @Component({
    template: `
      <form [formGroup]="form">
        <cca-multiple-file-input
          formControlName="file"
          [baseDownloadUrl]="getDownloadUrl()"
          hint="Custom hint"
        ></cca-multiple-file-input>
      </form>
    `,
    imports: [MultipleFileInputComponent, ReactiveFormsModule],
    providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
  })
  class TestComponent {
    form = new FormGroup({ file: new FormControl(null) });
    getDownloadUrl() {
      return `/download/`;
    }
  }

  class Page extends BasePage<TestComponent> {
    get filesText() {
      return this.queryAll<HTMLDivElement>('.cca-multi-file-upload__message');
    }

    get downloadLinks() {
      return this.queryAll<HTMLAnchorElement>('.govuk-link');
    }

    get deleteButtons() {
      return this.queryAll<HTMLButtonElement>('button[name="delete"]');
    }

    set files(file: File[]) {
      this.setInputValue('input', file);
    }

    get input() {
      return this.query<HTMLInputElement>('input');
    }

    get filePickerButton() {
      return this.query<HTMLButtonElement>('.cca-multi-file-upload__dropzone button[type="button"]');
    }

    get dropzoneHint() {
      return this.query<HTMLParagraphElement>('.cca-multi-file-upload__dropzone p');
    }

    get fileSizeHint() {
      return this.query<HTMLSpanElement>('[id="file-hint-file-size"]');
    }

    get customHint() {
      return this.query<HTMLSpanElement>('[id="file-hint"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(MultipleFileInputComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    control = hostComponent.form.get('file') as FormControl;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(page.dropzoneHint.classList.contains('govuk-body')).toBeTruthy();
  });

  it('should show the file-size hint together with the custom hint', () => {
    expect(page.fileSizeHint.textContent).toContain('no more than 20MB');
    expect(page.customHint.textContent).toEqual('Custom hint');
    expect(page.input.getAttribute('aria-describedby')).toContain('file-hint-file-size');
    expect(page.input.getAttribute('aria-describedby')).toContain('file-hint');
  });

  it('should keep the hidden input out of the tab order and open the picker from the button', () => {
    expect(page.input.tabIndex).toBe(-1);

    const clickSpy = vi.spyOn(page.input, 'click');
    page.filePickerButton.click();
    fixture.detectChanges();

    expect(clickSpy).toHaveBeenCalledTimes(1);
    expect(page.filePickerButton.getAttribute('aria-labelledby')).toBe(`l.${page.input.id} ld.${page.input.id}`);
  });

  it('should return focus to the Choose files button after adding and deleting files', () => {
    page.files = [new File(['test content'], 'New file')];
    fixture.detectChanges();

    expect(document.activeElement).toBe(page.filePickerButton);

    page.deleteButtons[0].click();
    fixture.detectChanges();

    expect(document.activeElement).toBe(page.filePickerButton);
  });

  it('should display current value', () => {
    expect(page.filesText).toHaveLength(0);

    control.setValue([{ file: new File(['abc'], 'Uploaded file'), uuid: '1234' }]);
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['Uploaded file (opens in a new tab)']);
    expect(page.downloadLinks.map((link) => link.href)).toEqual([expect.stringContaining('/download/1234')]);
  });

  // The control value can also come from the API/store through `buildFormControl`. Depending on the
  // payload it may hold entries without a file reference or a single file instead of a list. None of
  // those may break the file list (`Cannot read properties of undefined (reading 'name')` was thrown
  // from `file-upload-list.component.html`).
  describe('unexpected control values', () => {
    it('should display a stored file that has no file reference', () => {
      control.setValue([{ uuid: '1234', file: undefined }] as FileUploadEvent[]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['File (opens in a new tab)']);
    });

    it('should delete a stored file that has no file reference', () => {
      control.setValue([{ uuid: '1234', file: undefined }] as FileUploadEvent[]);
      fixture.detectChanges();

      page.deleteButtons[0].click();
      fixture.detectChanges();

      expect(page.filesText).toHaveLength(0);
      expect(control.value).toHaveLength(0);
    });

    it('should display a single file that is not wrapped in a list', () => {
      control.setValue({ file: new File(['abc'], 'Uploaded file'), uuid: '1234' } as unknown as FileUploadEvent[]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['Uploaded file (opens in a new tab)']);
    });

    it('should not display anything for a null value', () => {
      control.setValue(null);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.filesText).toHaveLength(0);
    });

    it('should display a null entry that is part of the list', () => {
      const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();
      control.setAsyncValidators(TestBed.inject(FileUploadService).uploadMany(() => uploadSubject));
      control.setValue([null, { file: new File(['abc'], 'Uploaded file'), uuid: '1234' }]);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
        'File (opens in a new tab)',
        'Uploaded file (opens in a new tab)',
      ]);

      page.deleteButtons[0].click();
      fixture.detectChanges();

      expect(control.value).toEqual([{ file: expect.any(File), uuid: '1234' }]);
    });
  });

  it('should validate big files', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    expect(control.touched).toBeFalsy();

    control.setValidators(FileValidators.multipleCompose(FileValidators.maxFileSize(1)));
    control.setAsyncValidators(TestBed.inject(FileUploadService).uploadMany(() => uploadSubject));
    const file = new File(['test content'], 'Big file');
    vi.spyOn(file, 'size', 'get').mockReturnValue(1024 * 1024 * 1024);
    page.files = [file];
    fixture.detectChanges();

    expect(control.touched).toBeTruthy();
    expect(page.filesText[0].textContent).toEqual('Big file must be 1MB or smaller');
    expect(control.invalid).toBeTruthy();
    expect(control.errors).toEqual({ 'maxFileSize-0-0': 'Big file must be 1MB or smaller' });
  });

  it('should show progress when uploading new files', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    control.setAsyncValidators(TestBed.inject(FileUploadService).uploadMany(() => uploadSubject));

    control.setValue([{ file: new File(['test content'], 'Existing file'), uuid: 'abcdA' }]);
    page.files = [new File(['test content'], 'New file')];
    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 5, total: 15 });
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'Existing file (opens in a new tab)',
      'New file (opens in a new tab) 33%',
    ]);

    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 10, total: 15 });
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'Existing file (opens in a new tab)',
      'New file (opens in a new tab) 67%',
    ]);

    uploadSubject.next(new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }));
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'Existing file (opens in a new tab)',
      'New file (opens in a new tab) has been uploaded',
    ]);
    expect(page.downloadLinks.map((link) => link.href)).toEqual([
      expect.stringContaining('/download/abcdA'),
      expect.stringContaining('/download/abcd'),
    ]);
  });

  it('should delete existing files', () => {
    control.setValue([{ file: new File(['test content'], 'Existing file') }]);
    fixture.detectChanges();

    page.deleteButtons[0].click();
    fixture.detectChanges();

    expect(page.filesText).toHaveLength(0);
    expect(control.value).toHaveLength(0);
  });

  it('should delete in flight files', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();
    control.setAsyncValidators(TestBed.inject(FileUploadService).uploadMany(() => uploadSubject));

    control.setValue([{ file: new File(['test content'], 'Existing file'), uuid: 'abcA' }]);
    fixture.detectChanges();

    page.files = [new File(['test content'], 'New file')];
    fixture.detectChanges();

    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 5, total: 15 });
    fixture.detectChanges();

    page.deleteButtons[1].click();
    fixture.detectChanges();

    expect(page.filesText).toHaveLength(1);
  });

  it('should disable the control', () => {
    control.disable();
    fixture.detectChanges();

    expect(page.input.disabled).toBeTruthy();
    expect(page.filePickerButton.disabled).toBeTruthy();
    expect(page.deleteButtons.every((button) => button.disabled)).toBeTruthy();
  });
});
