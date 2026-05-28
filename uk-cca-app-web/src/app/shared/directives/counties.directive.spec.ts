import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SelectComponent } from '@netz/govuk-components';

import { CountyService } from '../services/county.service';
import { CountiesDirective } from './counties.directive';

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
    const mockCountyService = {
      counties: signal([
        { id: 0, name: '' },
        { id: 1, name: 'Cyprus' },
        { id: 2, name: 'Greece' },
        { id: 3, name: 'Afghanistan' },
      ]),
    };

    fixture = TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [ControlContainer, { provide: CountyService, useValue: mockCountyService }],
    }).createComponent(TestComponent);

    directive = fixture.debugElement.query(By.directive(CountiesDirective)).injector.get(CountiesDirective);
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should assign counties to select', () => {
    const selectElement = fixture.debugElement.query(By.css('select'));
    const options = selectElement.nativeElement.options;

    expect(options.length).toBe(4);
    expect(options[0].value).toBe('0: ');
    expect(options[1].text).toBe('Afghanistan');
    expect(options[2].text).toBe('Cyprus');
    expect(options[3].text).toBe('Greece');
  });
});
