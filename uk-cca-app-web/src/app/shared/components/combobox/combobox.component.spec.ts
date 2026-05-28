import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { GovukSelectOption } from '@netz/govuk-components';

import { ComboboxComponent } from './combobox.component';

@Component({
  template: `
    <form [formGroup]="form">
      <cca-combobox formControlName="item" [options]="options()" />
    </form>
  `,
  imports: [ReactiveFormsModule, ComboboxComponent],
})
class TestHostComponent {
  form = new FormGroup({
    item: new FormControl<string | null>(null),
  });

  options = signal<GovukSelectOption<string | null>[]>([
    { value: 'FAC-001', text: 'Alpha facility' },
    { value: 'FAC-002', text: 'Beta facility' },
    { value: 'FAC-003', text: 'Gamma facility' },
  ]);
}

describe('ComboboxComponent', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let hostComponent: TestHostComponent;

  const getInput = () => fixture.nativeElement.querySelector('input[role="combobox"]') as HTMLInputElement;
  const getOptions = () =>
    Array.from(fixture.nativeElement.querySelectorAll('.cca-combobox-option')) as HTMLLIElement[];
  const getActiveOption = () =>
    fixture.nativeElement.querySelector('.cca-combobox-option--active') as HTMLLIElement | null;

  const focusInput = async () => {
    getInput().dispatchEvent(new FocusEvent('focus'));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  const inputValue = async (value: string) => {
    const input = getInput();
    input.value = value;
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  const keydown = async (key: string) => {
    getInput().dispatchEvent(new KeyboardEvent('keydown', { key, bubbles: true }));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    hostComponent = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('renders the combobox input', () => {
    expect(getInput()).toBeTruthy();
  });

  it('opens dropdown on focus and shows all options', async () => {
    await focusInput();

    expect(getOptions().length).toBe(3);
  });

  it('filters options when typing', async () => {
    await focusInput();
    await inputValue('beta');

    const optionTexts = getOptions().map((option) => option.textContent?.trim());
    expect(optionTexts).toEqual(['Beta facility']);
  });

  it('selects option on click and propagates value to form control', async () => {
    await focusInput();
    await inputValue('beta');

    const option = getOptions()[0];
    option.dispatchEvent(new Event('pointerdown', { bubbles: true }));
    fixture.detectChanges();

    expect(hostComponent.form.controls.item.value).toBe('FAC-002');
    expect(getInput().value).toBe('Beta facility');
  });

  it('closes dropdown after selection', async () => {
    await focusInput();

    getOptions()[0].dispatchEvent(new Event('pointerdown', { bubbles: true }));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(getOptions().length).toBe(0);
  });

  it('displays selected option text on writeValue', async () => {
    hostComponent.form.controls.item.setValue('FAC-003');
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(getInput().value).toBe('Gamma facility');
  });

  it('supports keyboard navigation and selection', async () => {
    await focusInput();
    await keydown('ArrowDown');

    const activeAfterDown = getActiveOption();
    expect(activeAfterDown).toBeTruthy();

    await keydown('ArrowUp');

    const activeAfterUp = getActiveOption();
    expect(activeAfterUp).toBeTruthy();

    const selectedText = activeAfterUp.textContent?.trim();
    await keydown('Enter');

    expect(getInput().value).toBe(selectedText);
    expect(getOptions().length).toBe(0);

    await focusInput();
    await keydown('Escape');

    expect(getOptions().length).toBe(0);
  });

  it('reverts typed text on blur without selection', async () => {
    hostComponent.form.controls.item.setValue('FAC-001');
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    await focusInput();
    await inputValue('Gam');

    getInput().dispatchEvent(new FocusEvent('blur'));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(getInput().value).toBe('Alpha facility');
    expect(hostComponent.form.controls.item.value).toBe('FAC-001');
  });

  it('handles disabled state', async () => {
    hostComponent.form.controls.item.disable();
    fixture.detectChanges();

    expect(getInput().disabled).toBe(true);

    await focusInput();

    expect(getOptions().length).toBe(0);
  });

  it('sets aria-autocomplete to both', () => {
    expect(getInput().getAttribute('aria-autocomplete')).toBe('both');
  });

  it('shows inline autocomplete for prefix match', async () => {
    await focusInput();
    await inputValue('Alp');

    const input = getInput();
    expect(input.value).toBe('Alpha facility');
    expect(input.selectionStart).toBe(3);
    expect(input.selectionEnd).toBe(14);
  });

  it('does not show inline autocomplete when no prefix matches', async () => {
    await focusInput();
    await inputValue('xyz');

    const input = getInput();
    expect(input.value).toBe('xyz');
  });

  it('accepts inline autocomplete on Tab', async () => {
    await focusInput();
    await inputValue('Bet');
    await keydown('Tab');

    expect(hostComponent.form.controls.item.value).toBe('FAC-002');
  });

  it('accepts inline autocomplete on ArrowRight', async () => {
    await focusInput();
    await inputValue('Gam');
    await keydown('ArrowRight');

    expect(getInput().value).toBe('Gamma facility');
    expect(hostComponent.form.controls.item.value).toBe('FAC-003');
  });

  it('accepts inline autocomplete on Enter when no active list item', async () => {
    await focusInput();
    await inputValue('Alp');
    await keydown('Enter');

    expect(getInput().value).toBe('Alpha facility');
    expect(hostComponent.form.controls.item.value).toBe('FAC-001');
  });

  it('does not re-suggest after Backspace', async () => {
    await focusInput();
    await inputValue('Alp');

    await keydown('Backspace');
    await inputValue('Al');

    const input = getInput();
    expect(input.value).toBe('Al');
    expect(input.selectionStart).toBe(input.selectionEnd);
  });

  it('clears inline suggestion when navigating with ArrowDown', async () => {
    await focusInput();
    await inputValue('Alp');

    expect(getInput().value).toBe('Alpha facility');

    await keydown('ArrowDown');

    expect(getInput().value).toBe('Alp');
  });

  it('shows all matching options in dropdown while inline autocomplete is active', async () => {
    hostComponent.options.set([
      { value: 'FAC-001', text: 'ADS_1-FAC000123' },
      { value: 'FAC-002', text: 'ADS_1-FAC000124' },
      { value: 'FAC-003', text: 'ADS_1-FAC000125' },
      { value: 'FAC-004', text: 'Other facility' },
    ]);
    fixture.detectChanges();

    await focusInput();
    await inputValue('ADS');

    const input = getInput();
    expect(input.value).toBe('ADS_1-FAC000123');
    expect(input.selectionStart).toBe(3);

    const optionTexts = getOptions().map((o) => o.textContent?.trim());
    expect(optionTexts).toEqual(['ADS_1-FAC000123', 'ADS_1-FAC000124', 'ADS_1-FAC000125']);
  });
});
