import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { ReturnLinkComponent } from '../../shared/components/return-link/return-link.component';
import { NotSuccessComponent } from './not-success.component';

describe('NotSuccessComponent', () => {
  let component: NotSuccessComponent;
  let fixture: ComponentFixture<NotSuccessComponent>;
  let page: Page;

  const activatedRoute = new ActivatedRouteStub();

  class Page extends BasePage<NotSuccessComponent> {
    get content(): HTMLHeadElement {
      return this.query('h1');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PageHeadingComponent],
      declarations: [NotSuccessComponent, ReturnLinkComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            ...activatedRoute,
            queryParams: of({ message: 'Payment cancelled by user' }),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotSuccessComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.content.textContent.trim()).toEqual('Payment cancelled by user');
  });
});
