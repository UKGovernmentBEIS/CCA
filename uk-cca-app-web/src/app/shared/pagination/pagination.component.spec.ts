import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PaginationComponent } from './pagination.component';

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  let route: ActivatedRoute;

  @Component({
    template: `
      <cca-pagination
        [count]="count"
        [pageSize]="pageSize"
        (currentPageChange)="this.currentPage = $event"
      ></cca-pagination>
    `,
  })
  class TestComponent {
    count;
    pageSize;
    currentPage;
  }

  @Component({ template: '<router-outlet></router-outlet>' })
  class RouterComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent, RouterTestingModule.withRoutes([{ path: '', component: TestComponent }])],
      declarations: [TestComponent, RouterComponent],
      schemas: [NO_ERRORS_SCHEMA],
    });
  });

  beforeEach(() => {
    TestBed.createComponent(RouterComponent);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(PaginationComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    element = fixture.nativeElement;
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate total pages', () => {
    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__item')).length).toEqual(0);

    hostComponent.count = 36;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__item')).length).toEqual(4);
    expect(component.pageNumbers).toEqual([1, 2, 3, 4]);

    hostComponent.count = 53;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__item')).length).toEqual(6);
    expect(component.pageNumbers).toEqual([1, 2, 3, 4, 5, 6]);
  });

  it('should emit currentPage', async () => {
    const links = element.querySelectorAll<HTMLLIElement>('.govuk-pagination__item--current');
    expect(hostComponent.currentPage).toEqual(1);
    expect(links.length).toEqual(0);

    hostComponent.count = 36;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    const page3 = fixture.debugElement.queryAll(By.css('.govuk-pagination__link'))[1].nativeElement;
    expect(page3.textContent.trim()).toEqual('2');

    page3.click();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(route.snapshot.queryParamMap.get('page')).toEqual('2');
    expect(hostComponent.currentPage).toEqual(2);
  });

  it('should show dots for a large amount of pages', () => {
    hostComponent.count = 126;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__item--ellipses')).length).toEqual(1);
    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__item')).length).toEqual(4);
  });

  it('should not show previous on first page or next on last page', () => {
    hostComponent.count = 30;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__prev'))).toHaveLength(0);
    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__next'))).toHaveLength(1);

    // Click on `Next` link
    fixture.debugElement.queryAll(By.css('.govuk-pagination__link'))[3].nativeElement.click();
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__prev'))).toHaveLength(1);
    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__next'))).toHaveLength(1);

    fixture.debugElement.queryAll(By.css('.govuk-pagination__link'))[3].nativeElement.click();
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__prev'))).toHaveLength(1);
    expect(fixture.debugElement.queryAll(By.css('.govuk-pagination__next'))).toHaveLength(0);
  });

  it('should not display a page if no results exist', () => {
    hostComponent.count = 0;
    hostComponent.pageSize = 0;
    fixture.detectChanges();

    const links = element.querySelectorAll('.govuk-pagination__item');

    expect(links.length).toEqual(0);
  });
});
