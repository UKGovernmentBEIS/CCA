import { Component } from '@angular/core';
import type { ComponentFixture } from '@angular/core/testing';
import { TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { ErrorMessageComponent } from '../error-message';
import { FileUploadComponent } from './file-upload.component';

describe('FileUploadComponent', () => {
  @Component({
    standalone: true,
    imports: [FileUploadComponent, ReactiveFormsModule],
    template: '<div govukFileUpload [formControl]="control"></div>',
  })
  class TestComponent {
    control = new FormControl();
  }

  let component: FileUploadComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FileUploadComponent, TestComponent, ErrorMessageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(FileUploadComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable the fileupload', () => {
    hostComponent.control.disable();
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');

    expect(input.disabled).toBeTruthy();
  });

  it('should set as touched on blur', () => {
    const hostElement = fixture.debugElement;
    const input = hostElement.query(By.css('input'));

    expect(hostComponent.control.touched).toBeFalsy();

    input.triggerEventHandler('focus', {});
    input.triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(hostComponent.control.touched).toBeTruthy();
  });

  it('should emit file change', () => {
    const input = fixture.debugElement.query(By.css('input'));
    const mockFiles = [new File(['test'], 'test.txt', { type: 'text/plain' })];
    const mockEvent = { target: { files: mockFiles } };

    expect(hostComponent.control.value).toBeNull();

    input.triggerEventHandler('change', mockEvent);
    fixture.detectChanges();

    expect(hostComponent.control.value).toEqual(mockFiles);
  });
});
