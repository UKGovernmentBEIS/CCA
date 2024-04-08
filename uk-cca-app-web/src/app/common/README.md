### CCA - Common UI modules

This is a library-type structure that provides common functionality for the UI.  
It consists of:

- The `cancel-task` module is for providing functionality for cancelling a RequestTask that has been opened. It is required in many types of RequestTasks and is provided along with its routes, but you can use the components alone if you wish.
- The `change-task-assignee` module is for providing functionality for changing a RequestTask's assignee user. It is provided along with its routes, but you can use the components alone if you wish.
- The `forms` module contains several mechanisms for assisting with RequestTask forms/wizards functionality.
  Its use is optional but recommended if you want to maintain a consistent structure in task forms and their handling.
  Read its documentation [here](./forms/README.md).
- The `request-action` module contains the setup for the RequestAction (aka timeline) page. Minimal routes file is provided - you can use components and guards at your discretion.
- The `request-task` module contains the setup for the RequestTask page. Minimal routes file is provided - you can use components and guards at your discretion.
- The `shared` module contains some commonly used components - use at your convenience
- The `store` module contains the SignalStore state management implementation.
