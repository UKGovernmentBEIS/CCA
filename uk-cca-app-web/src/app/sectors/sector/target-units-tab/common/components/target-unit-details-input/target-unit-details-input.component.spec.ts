import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { addSicCodeFormControl } from '../../utils';
import { TargetUnitDetailsInputComponent } from './target-unit-details-input.component';

@Component({
  template: `
    <form [formGroup]="form">
      <cca-target-unit-details-input />
    </form>
  `,
  imports: [ReactiveFormsModule, TargetUnitDetailsInputComponent],
})
class HostComponent {
  readonly form = new FormGroup({
    operatorType: new FormControl(null),
    name: new FormControl(null),
    sicCodes: new FormArray([addSicCodeFormControl('1'), addSicCodeFormControl('2'), addSicCodeFormControl('3')]),
    subsectorAssociationId: new FormControl(null),
  });
}

describe('TargetUnitDetailsInputComponent', () => {
  let fixture: ComponentFixture<HostComponent>;

  const sicCodeInputs = (): HTMLInputElement[] => Array.from(fixture.nativeElement.querySelectorAll('.row-flex input'));
  const removeButtons = (): HTMLButtonElement[] =>
    Array.from(fixture.nativeElement.querySelectorAll('.row-flex button'));

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [HostComponent] }).compileComponents();

    fixture = TestBed.createComponent(HostComponent);
    await fixture.whenStable();
  });

  it('should move focus to the next SIC code after removing a code', async () => {
    removeButtons()[1].click();
    await fixture.whenStable();

    expect(sicCodeInputs()).toHaveLength(2);
    expect(document.activeElement).toBe(sicCodeInputs()[1]);
  });

  it('should move focus to the add button after removing the last code', async () => {
    removeButtons()[2].click();
    await fixture.whenStable();

    expect((document.activeElement as HTMLElement).textContent.trim()).toBe('Add a code');
  });
});
