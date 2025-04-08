import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SelectComponent } from '@netz/govuk-components';
import { CountryService } from '@shared/services';

import { CountryServiceStub } from 'src/testing/country.service.stub';

import { CountriesDirective } from './countries.directive';

describe('CountriesDirective', () => {
  let directive: CountriesDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: '<div govuk-select ccaCountries [formControl]="country" label="Country"> </div>',
    standalone: true,
    imports: [CountriesDirective, ReactiveFormsModule, SelectComponent],
  })
  class TestComponent {
    country = new FormControl();
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [{ provide: CountryService, useClass: CountryServiceStub }],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(CountriesDirective)).injector.get(CountriesDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should assign countries to select', () => {
    const selectElement = fixture.debugElement.query(By.css('select'));
    expect((selectElement.nativeElement as HTMLSelectElement).options[1].label).toEqual('Afghanistan');
  });
});
