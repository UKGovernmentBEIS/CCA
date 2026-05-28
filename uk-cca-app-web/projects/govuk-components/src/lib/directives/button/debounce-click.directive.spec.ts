import { Component, ElementRef, viewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { DebounceClickDirective } from './debounce-click.directive';

describe('DebounceClickDirective', () => {
  let directive: DebounceClickDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [DebounceClickDirective],
    template: ` <button #button govukDebounceClick (debounceClick)="onClick()">Simple button</button> `,
  })
  class TestComponent {
    readonly button = viewChild<ElementRef>('button');

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    onClick(): void {}
  }

  beforeEach(() => {
    vi.useFakeTimers();
    fixture = TestBed.configureTestingModule({
      imports: [TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(DebounceClickDirective)).injector.get(DebounceClickDirective);
  });

  afterEach(() => {
    vi.clearAllTimers();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should click on single click', () => {
    vi.spyOn(fixture.componentInstance, 'onClick');
    const button: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector('button');
    button.click();
    vi.advanceTimersByTime(500);
    expect(fixture.componentInstance.onClick).toHaveBeenCalled();
  });

  it('should click once on double click', () => {
    vi.spyOn(fixture.componentInstance, 'onClick');
    const button: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector('button');
    button.click();
    button.click();
    vi.advanceTimersByTime(500);
    expect(fixture.componentInstance.onClick).toHaveBeenCalledTimes(1);
  });
});
