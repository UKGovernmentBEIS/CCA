import { HttpErrorResponse, HttpEvent, HttpEventType, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { first, mergeMap, Subject, throwError } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { FileUuidDTO } from 'cca-api';

import { FileInputComponent } from './file-input.component';
import { FileUploadService } from './file-upload.service';
import { FileUploadEvent } from './file-upload-event';
import { FileValidators } from './file-validators';

describe('FileInputComponent', () => {
  let component: FileInputComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;
  let control: FormControl;

  const activatedRoute = new ActivatedRouteStub();

  @Component({
    template: `
      <form [formGroup]="form">
        <cca-file-input formControlName="file" [downloadUrl]="getDownloadUrl" />
      </form>
    `,
    imports: [FileInputComponent, ReactiveFormsModule],
    providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
  })
  class TestComponent {
    form = new FormGroup({ file: new FormControl({ file: new File(['abc'], 'uploaded-file.txt'), uuid: '1234' }) });
    getDownloadUrl = vi.fn((uuid: string) => `/download/${uuid}`);
  }

  class Page extends BasePage<TestComponent> {
    get fileText() {
      return this.query<HTMLDivElement>('.cca-multi-file-upload__message');
    }

    get downloadLink() {
      return this.query<HTMLAnchorElement>('.govuk-link');
    }

    get delete() {
      return this.query<HTMLButtonElement>('button[name="delete"]');
    }

    get input() {
      return this.query<HTMLInputElement>('input');
    }

    get filePickerButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }

    get file() {
      return this.input.files.item(0);
    }

    set file(file: File) {
      this.setInputValue('input', file ?? []);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(FileInputComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    control = hostComponent.form.get('file') as FormControl;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should keep the hidden input out of the tab order and open the picker from the button', () => {
    expect(page.input.tabIndex).toBe(-1);

    const clickSpy = vi.spyOn(page.input, 'click');
    page.filePickerButton.click();
    fixture.detectChanges();

    expect(clickSpy).toHaveBeenCalledTimes(1);
    expect(page.filePickerButton.getAttribute('aria-labelledby')).toBe(`l.${page.input.id} ld.${page.input.id}`);
  });

  it('should return focus to the Choose file button after adding and deleting files', () => {
    page.file = new File(['test content'], 'New file');
    fixture.detectChanges();

    expect(document.activeElement).toBe(page.filePickerButton);

    page.delete.click();
    fixture.detectChanges();

    expect(document.activeElement).toBe(page.filePickerButton);
  });

  it('should associate the error message with the input when the form is submitted', () => {
    control.setValidators(Validators.required);
    control.setValue(null);
    fixture.detectChanges();

    expect(page.input.getAttribute('aria-describedby')).not.toContain('-error');

    fixture.debugElement.query(By.css('form')).triggerEventHandler('submit', {});
    fixture.detectChanges();

    expect(page.input.getAttribute('aria-describedby')).toContain('-error');
  });

  it('should display current value', () => {
    expect(page.fileText.textContent.trim()).toEqual('uploaded-file.txt (opens in a new tab)');
  });

  // The control value can also come from the API/store through `buildFormControl`. A single-file field
  // is built from a scalar uuid, so a list or an empty value is treated as no file at all, while an
  // entry without a file reference still has to render. None of those may break the file list
  // (`Cannot read properties of undefined (reading 'name')` was thrown from
  // `file-upload-list.component.html`).
  describe('unexpected control values', () => {
    it('should not display a file when the control holds a list of files', () => {
      control.setValue([
        { file: new File(['test content'], 'First file'), uuid: 'uuid-1' },
        { file: new File(['test content'], 'Second file'), uuid: 'uuid-2' },
      ]);

      expect(() => fixture.detectChanges()).not.toThrow();

      // Showing the first file would offer a delete button for a value the payload builders read as
      // `value.permitFile?.uuid`, which is undefined for a list: the file could not be saved.
      expect(page.fileText).toBeFalsy();

      control.setValue([]);
      fixture.detectChanges();

      expect(page.fileText).toBeFalsy();
    });

    it('should display an entry that has a uuid but no file reference, so it stays visible and deletable', () => {
      control.setValue({ file: undefined, uuid: 'uuid-1' } as FileUploadEvent);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(page.fileText.textContent.trim()).toEqual('File (opens in a new tab)');
      expect(page.downloadLink.getAttribute('href')).toBe('/download/uuid-1');

      page.delete.click();
      fixture.detectChanges();

      expect(control.value).toBeNull();
    });
  });

  it('should replace the uploaded file', () => {
    page.file = new File(['test content'], 'New file');
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('New file (opens in a new tab)');
    expect(page.downloadLink.textContent.trim()).toEqual('New file (opens in a new tab)');
  });

  it('should validate a file against max size', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    expect(control.touched).toBeFalsy();

    control.setValidators(FileValidators.maxFileSize(1));
    control.setAsyncValidators(TestBed.inject(FileUploadService).upload(() => uploadSubject));
    const file = new File(['test content'], 'Big file');
    vi.spyOn(file, 'size', 'get').mockReturnValue(1024 * 1024 * 1024);
    page.file = file;
    fixture.detectChanges();

    expect(control.touched).toBeTruthy();
    expect(page.fileText.textContent).toEqual('Big file must be 1MB or smaller');
    expect(control.invalid).toBeTruthy();
    expect(control.errors).toEqual({ 'maxFileSize-0': 'Big file must be 1MB or smaller' });
  });

  it('should display upload progress', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    control.setAsyncValidators(TestBed.inject(FileUploadService).upload(() => uploadSubject));

    control.setValue({ file: new File(['test content'], 'Existing file'), uuid: 'abcdA' });
    page.file = new File(['test content'], 'New file');
    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 5, total: 15 });
    fixture.detectChanges();
    expect(page.fileText.textContent.trim()).toEqual('New file (opens in a new tab) 33%');

    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 10, total: 15 });
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('New file (opens in a new tab) 67%');

    uploadSubject.next(new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }));
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('New file (opens in a new tab) has been uploaded');
    expect(page.downloadLink.textContent.trim()).toEqual('New file (opens in a new tab)');
  });

  it('should display upload errors', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    control.setAsyncValidators(
      TestBed.inject(FileUploadService).upload(() =>
        uploadSubject.pipe(
          first(),
          mergeMap(() =>
            throwError(
              () =>
                new HttpErrorResponse({
                  status: HttpStatusCode.BadRequest,
                  error: { message: 'should not be txt' },
                }),
            ),
          ),
        ),
      ),
    );

    page.file = new File(['test content'], 'New file');
    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 10, total: 15 });
    fixture.detectChanges();

    expect(control.invalid).toBeTruthy();
    expect(control.errors).toEqual({ upload: 'New file should not be txt' });
    expect(page.fileText.textContent.trim()).toEqual('New file should not be txt');
  });

  it('should delete a file', () => {
    page.delete.click();
    fixture.detectChanges();

    expect(page.fileText).toBeFalsy();
    expect(hostComponent.form.value.file).toBeNull();
    expect(page.file).toBeNull();

    page.file = new File(['content'], 'file.txt');
    fixture.detectChanges();

    page.delete.click();
    fixture.detectChanges();

    expect(page.fileText).toBeFalsy();
    expect(hostComponent.form.value.file).toBeNull();
    expect(page.file).toBeNull();
  });

  // The dimensions of an image used to be probed before the file was written to the control, so a
  // bitmap that the browser cannot decode (or that never reports back) left the field empty and the
  // file unuploaded. The file must be shown and uploaded regardless of the probe.
  it('should display a picked image without waiting for its dimensions', () => {
    page.file = new File(['test content'], 'bitmap.bmp', { type: 'image/bmp' });
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('bitmap.bmp (opens in a new tab)');
  });

  // The probe runs after the file is already in the control: it must not delay the display, and it
  // hands the dimensions over only while the field still holds the probed file.
  it('should probe the dimensions of a picked image without waiting for them', async () => {
    // jsdom does not implement the object url api, so the probe cannot reach a resolver without it.
    const createObjectURL = vi.fn(() => 'blob:poster');
    const revokeObjectURL = vi.fn();
    Object.assign(window.URL, { createObjectURL, revokeObjectURL });
    const resolveDimensions = vi.fn(() => Promise.resolve({ width: 240, height: 140 }));
    component['getImageFileDimensionsResolver'] = resolveDimensions;

    const image = new File(['test content'], 'poster.png', { type: 'image/png' });
    page.file = image;
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('poster.png (opens in a new tab)');

    await fixture.whenStable();
    fixture.detectChanges();

    expect(createObjectURL).toHaveBeenCalledWith(image);
    expect(resolveDimensions).toHaveBeenCalledWith('blob:poster');
    expect(control.value).toEqual({ file: image, uuid: null, dimensions: { width: 240, height: 140 } });

    // The object url is released once the probe has settled.
    await new Promise((resolve) => setTimeout(resolve));
    expect(revokeObjectURL).toHaveBeenCalledWith('blob:poster');
  });

  it('should add the probed dimensions while the field still holds the picked file', () => {
    const image = new File(['test content'], 'poster.png', { type: 'image/png' });
    page.file = image;
    fixture.detectChanges();

    component['setImageDimensions'](image, { width: 240, height: 140 });
    fixture.detectChanges();

    expect(control.value).toEqual({ file: image, uuid: null, dimensions: { width: 240, height: 140 } });
  });

  it('should ignore probed dimensions of a file that is no longer picked', () => {
    const image = new File(['test content'], 'poster.png', { type: 'image/png' });
    page.file = image;
    fixture.detectChanges();

    const replacement = new File(['test content'], 'replacement.png', { type: 'image/png' });
    page.file = replacement;
    fixture.detectChanges();

    component['setImageDimensions'](image, { width: 240, height: 140 });
    fixture.detectChanges();

    expect(control.value).toEqual({ file: replacement, uuid: null, dimensions: null });
  });

  it('should retain the old file when the user cancels adding a new one', () => {
    const file = new File(['test content'], 'new-file.txt');
    page.file = file;
    fixture.detectChanges();

    page.file = null;
    fixture.detectChanges();

    expect(control.value).toEqual({ file, uuid: null, dimensions: null });
  });
});
