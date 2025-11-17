import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { Cca3MigrationAccountActivationComponent } from './cca3-migration-account-activation.component';
import { mockRequestTaskItemDTO } from './testing/mock-data';

describe('Cca3MigrationAccountActivationComponent', () => {
  let component: Cca3MigrationAccountActivationComponent;
  let fixture: ComponentFixture<Cca3MigrationAccountActivationComponent>;
  let store: RequestTaskStore;
  let page: Page;
  let router: Router;

  class Page extends BasePage<Cca3MigrationAccountActivationComponent> {
    get notifyButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cca3MigrationAccountActivationComponent],
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

    fixture = TestBed.createComponent(Cca3MigrationAccountActivationComponent);
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
    expect(navigateSpy).toHaveBeenCalledWith(
      ['cca3-migration-account-activation', 'notify-operator'],
      expect.anything(),
    );
  });
});
