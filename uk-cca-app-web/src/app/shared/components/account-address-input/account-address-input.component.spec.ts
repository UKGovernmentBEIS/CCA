import { provideHttpClient } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { CountryService } from '../../services/country.service';
import { AccountAddressInputComponent } from './account-address-input.component';
import { createAccountAddressForm } from './account-address-input-controls';

describe('AccountAddressInputComponent', () => {
  let component: AccountAddressInputComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <form [formGroup]="form">
        <fieldset govukFieldset>
          <legend govukLegend>What is your address?</legend>
          <div formGroupName="address">
            <cca-account-address-input />
          </div>
        </fieldset>
      </form>
    `,
    imports: [AccountAddressInputComponent, ReactiveFormsModule],
  })
  class TestComponent {
    form = new FormGroup({
      address: createAccountAddressForm(null),
    });
  }

  beforeEach(async () => {
    const mockCountryService = {
      countries: signal([
        {
          code: 'PT',
          name: 'Portugal',
          officialName: 'The Portuguese Republic',
        },
        {
          code: 'PW',
          name: 'Palau',
          officialName: 'The Republic of Palau',
        },
      ]),
      ukCountries: signal([{ code: 'GB', name: 'United Kingdom', officialName: 'United Kingdom' }]),
    };

    await TestBed.configureTestingModule({
      imports: [AccountAddressInputComponent, ReactiveFormsModule],
      providers: [provideHttpClient(), { provide: CountryService, useValue: mockCountryService }],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(AccountAddressInputComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
