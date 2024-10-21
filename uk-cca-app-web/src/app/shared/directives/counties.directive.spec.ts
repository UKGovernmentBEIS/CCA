import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { of } from 'rxjs';

import { CountyService } from '@core/services/county.service';
import { MockType } from '@netz/common/testing';
import { SelectComponent } from '@netz/govuk-components';

import { CountiesDirective } from './counties.directive';

const mockCountyService: MockType<CountyService> = {
  getUkCounties: jest.fn().mockReturnValue(
    of([
      {
        id: 1,
        name: 'Cyprus',
      },
      {
        id: 2,
        name: 'Greece',
      },
      {
        id: 3,
        name: 'Afghanistan',
      },
    ]),
  ),
};

describe('CountiesDirective', () => {
  let directive: CountiesDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: '<div govuk-select ccaCounties [formControl]="county" label="County"> </div>',
    standalone: true,
    imports: [CountiesDirective, ReactiveFormsModule, SelectComponent],
  })
  class TestComponent {
    county = new FormControl();
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [{ provide: CountyService, useValue: mockCountyService }],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(CountiesDirective)).injector.get(CountiesDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should assign counties to select', () => {
    const selectElement = fixture.debugElement.query(By.css('select'));
    expect((selectElement.nativeElement as HTMLSelectElement).options[1].label).toEqual('Cyprus');
  });
});
