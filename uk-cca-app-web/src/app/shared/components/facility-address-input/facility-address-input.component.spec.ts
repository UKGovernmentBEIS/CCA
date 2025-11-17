import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { FacilityAddressInputComponent } from './facility-address-input.component';
import { createFacilityAddressForm } from './facility-address-input-controls';

describe('FacilityAddressInputComponent', () => {
  let component: FacilityAddressInputComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <form [formGroup]="form">
        <fieldset govukFieldset>
          <legend govukLegend>What is your address?</legend>
          <div formGroupName="address">
            <cca-facility-address-input />
          </div>
        </fieldset>
      </form>
    `,
    imports: [FacilityAddressInputComponent, ReactiveFormsModule],
  })
  class TestComponent {
    form = new FormGroup({
      address: createFacilityAddressForm(null),
    });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityAddressInputComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(FacilityAddressInputComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
