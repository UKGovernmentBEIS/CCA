import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestActionVariationActivatedState } from './testing/mock-data';
import { UnderlyingAgreementVariationActivatedComponent } from './underlying-agreement-variation-activated.component';

describe('UnderlyingAgreementVariationActivatedComponent', () => {
  let component: UnderlyingAgreementVariationActivatedComponent;
  let fixture: ComponentFixture<UnderlyingAgreementVariationActivatedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationActivatedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionVariationActivatedState);

    fixture = TestBed.createComponent(UnderlyingAgreementVariationActivatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should match snapshot', () => {
    expect(fixture).toMatchSnapshot();
  });
});
