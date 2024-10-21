import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestActionActivatedState } from './testing/mock-data';
import { UnderlyingAgreementActivatedComponent } from './underlying-agreement-activated.component';

describe('UnderlyingAgreementActivatedComponent', () => {
  let component: UnderlyingAgreementActivatedComponent;
  let fixture: ComponentFixture<UnderlyingAgreementActivatedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementActivatedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionActivatedState);

    fixture = TestBed.createComponent(UnderlyingAgreementActivatedComponent);
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
