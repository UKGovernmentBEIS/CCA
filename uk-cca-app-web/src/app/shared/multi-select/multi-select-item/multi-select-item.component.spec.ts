import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { MultiSelectComponent } from '@shared/multi-select/multi-select.component';

import { MultiSelectItemComponent } from './multi-select-item.component';

describe('MultipleSelectItemComponent', () => {
  @Component({
    template: `
      <div cca-multi-select [formControl]="control" label="Test label">
        <div cca-multi-select-item itemName="testName" itemValue="1" label="Test label 1"></div>
        <div cca-multi-select-item itemName="testName" itemValue="2" label="Test label 2"></div>
      </div>
    `,
    standalone: true,
    imports: [MultiSelectComponent, MultiSelectItemComponent, ReactiveFormsModule],
  })
  class TestComponent {
    control = new FormControl();
  }

  let component: MultiSelectItemComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
    (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>('button').click();
    fixture.detectChanges();
    component = fixture.debugElement.query(By.directive(MultiSelectItemComponent)).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
