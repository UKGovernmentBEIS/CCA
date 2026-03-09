import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { UnderlyingAgreementRegulatorLedVariationPrecontentComponent } from './underlying-agreement-variation-review-precontent.component';

describe('UnderlyingAgreementRegulatorLedVariationPrecontentComponent', () => {
  let component: UnderlyingAgreementRegulatorLedVariationPrecontentComponent;
  let fixture: ComponentFixture<UnderlyingAgreementRegulatorLedVariationPrecontentComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementRegulatorLedVariationPrecontentComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' } });
    store.setPayload({ payloadType: '', sectionsCompleted: { OPERATOR_ASSENT_DECISION_SUBTASK: 'COMPLETED' } });
    store.setState({ ...store.state, isEditable: true });

    fixture = TestBed.createComponent(UnderlyingAgreementRegulatorLedVariationPrecontentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });
});
