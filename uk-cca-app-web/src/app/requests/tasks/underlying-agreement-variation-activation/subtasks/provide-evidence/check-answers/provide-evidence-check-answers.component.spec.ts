import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import ProvideEvidenceCheckAnswersComponent from './provide-evidence-check-answers.component';

describe('ProvideEvidenceCheckAnswersComponent', () => {
  let component: ProvideEvidenceCheckAnswersComponent;
  let fixture: ComponentFixture<ProvideEvidenceCheckAnswersComponent>;
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProvideEvidenceCheckAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(ProvideEvidenceCheckAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
