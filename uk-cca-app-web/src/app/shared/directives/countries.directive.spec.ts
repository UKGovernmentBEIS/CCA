import { Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ControlContainer, FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SelectComponent } from '@netz/govuk-components';

import { CountriesDirective } from './countries.directive';

// Mock the COUNTRIES constant that the directive uses
jest.mock('@shared/services', () => ({
  COUNTRIES: [
    {
      code: 'GB-ENG',
      name: 'England',
      officialName: 'England',
    },
    {
      code: 'GB-SCT',
      name: 'Scotland',
      officialName: 'Scotland',
    },
    {
      code: 'GB-WLS',
      name: 'Wales',
      officialName: 'Wales',
    },
    {
      code: 'PT',
      name: 'Portugal',
      officialName: 'The Portuguese Republic',
    },
    {
      code: 'AF',
      name: 'Afghanistan',
      officialName: 'Islamic Republic of Afghanistan',
    },
  ],
  UK_COUNTRY_CODES: ['GB-ENG', 'GB-NIR', 'GB-SCT', 'GB-WLS'],
}));

describe('CountriesDirective', () => {
  let directive: CountriesDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: '<div govuk-select ccaCountries [formControl]="country" label="Country"> </div>',
    imports: [CountriesDirective, ReactiveFormsModule, SelectComponent],
  })
  class TestComponent {
    country = new FormControl();
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [ControlContainer],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(CountriesDirective)).injector.get(CountriesDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should assign countries to select with correct ordering', fakeAsync(() => {
    fixture.detectChanges();
    tick(); // Wait for async operations to complete

    const selectElement = fixture.debugElement.query(By.css('select'));
    const options = selectElement.nativeElement.options;

    // Check UK countries come first, followed by empty option, then other countries alphabetically
    expect(options[0].text).toBe('England');
    expect(options[1].text).toBe('Scotland');
    expect(options[2].text).toBe('Wales');
    expect(options[3].text).toBe('--'); // Empty selection after Wales
    expect(options[4].text).toBe('Afghanistan');
    expect(options[5].text).toBe('Portugal');

    // Verify values are country codes
    expect(options[0].value).toBe('0: GB-ENG');
    expect(options[4].value).toBe('4: AF');
  }));
});
