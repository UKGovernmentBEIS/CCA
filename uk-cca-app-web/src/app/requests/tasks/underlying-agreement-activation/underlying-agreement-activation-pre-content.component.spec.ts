import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from './testing/mock-data';
import UnderlyingAgreementActivationPreContentComponent from './underlying-agreement-activation-pre-content.component';

describe('UnderlyingAgreementActivationPreContentComponent', () => {
  let component: UnderlyingAgreementActivationPreContentComponent;
  let fixture: ComponentFixture<UnderlyingAgreementActivationPreContentComponent>;
  let store: RequestTaskStore;
  let page: Page;
  let router: Router;

  class Page extends BasePage<UnderlyingAgreementActivationPreContentComponent> {
    get notifyButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UnderlyingAgreementActivationPreContentComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);
    store.setState({ ...store.state, isEditable: true });

    fixture = TestBed.createComponent(UnderlyingAgreementActivationPreContentComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to notify operator', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.notifyButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['underlying-agreement-activation', 'notify-operator'], expect.anything());
  });
});
