import { Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SelectComponent } from '@netz/govuk-components';

import { CountiesDirective } from './counties.directive';

// Mock the COUNTIES constant that the directive uses
jest.mock('@shared/services', () => ({
  COUNTIES: [
    { id: 0, name: '' },
    { id: 1, name: 'Cyprus' },
    { id: 2, name: 'Greece' },
    { id: 3, name: 'Afghanistan' },
  ],
}));

describe('CountiesDirective', () => {
  let directive: CountiesDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: '<div govuk-select ccaCounties [formControl]="county" label="County"> </div>',
    imports: [CountiesDirective, ReactiveFormsModule, SelectComponent],
  })
  class TestComponent {
    county = new FormControl();
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(CountiesDirective)).injector.get(CountiesDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should assign counties to select', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    const selectElement = fixture.debugElement.query(By.css('select'));
    const options = selectElement.nativeElement.options;

    expect(options.length).toBe(4);
    expect(options[0].value).toBe('0: ');
    expect(options[1].text).toBe('Afghanistan');
    expect(options[2].text).toBe('Cyprus');
    expect(options[3].text).toBe('Greece');
  }));
});
