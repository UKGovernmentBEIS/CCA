import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';
import { EditOnHoldComponent } from './edit-on-hold.component';

describe('EditOnHoldComponent', () => {
  let fixture: ComponentFixture<EditOnHoldComponent>;
  let component: EditOnHoldComponent;
  let store: BuyoutAndSurplusTabStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, EditOnHoldComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(),
        },
        BuyoutAndSurplusTabStore,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditOnHoldComponent);
    store = TestBed.inject(BuyoutAndSurplusTabStore);
    store.setState({
      surplusInfo: {
        excluded: true,
      },
    });

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should pre-populate form value with false', () => {
    store.setState({
      surplusInfo: {},
    });

    fixture.detectChanges();
    component = fixture.componentInstance;

    expect(component.form.valid).toBe(true);
    expect(component.form.value.changeOnHold).toEqual(false);
  });
});
