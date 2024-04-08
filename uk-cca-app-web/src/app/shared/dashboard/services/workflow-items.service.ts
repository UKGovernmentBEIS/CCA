import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import {
  ItemDTOResponse,
  ItemsAssignedToMeService,
  ItemsAssignedToOthersService,
  UnassignedItemsService,
} from 'cca-api';

import { WorkflowItemsAssignmentType } from '../store';

type ItemsServiceMethod =
  | UnassignedItemsService['getUnassignedItems']
  | ItemsAssignedToOthersService['getAssignedToOthersItems']
  | ItemsAssignedToMeService['getAssignedItems'];

@Injectable()
export class WorkflowItemsService {
  constructor(
    private readonly itemsAssignedToMeService: ItemsAssignedToMeService,
    private readonly itemsAssignedToOthersService: ItemsAssignedToOthersService,
    private readonly unassignedItemsService: UnassignedItemsService,
  ) {}

  getItems(type: WorkflowItemsAssignmentType, page: number, pageSize: number): Observable<ItemDTOResponse> {
    const serviceMethod = this.getServiceMethod(type);
    return serviceMethod(page - 1, pageSize);
  }

  private getServiceMethod(type: WorkflowItemsAssignmentType): ItemsServiceMethod {
    switch (type) {
      case 'unassigned':
        return this.unassignedItemsService.getUnassignedItems.bind(this.unassignedItemsService);
      case 'assigned-to-others':
        return this.itemsAssignedToOthersService.getAssignedToOthersItems.bind(this.itemsAssignedToOthersService);
      case 'assigned-to-me':
      default:
        return this.itemsAssignedToMeService.getAssignedItems.bind(this.itemsAssignedToMeService);
    }
  }
}
