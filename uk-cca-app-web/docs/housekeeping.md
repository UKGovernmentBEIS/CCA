# Housekeeping

This file is a list of chores to do for the repository. Here we write up inconsistencies and stuff that we
want to refactor and did not have the time to.

Issues in this document are not to be taken as is. The team can remove/add entries at will. Whenever we decide to fix one of the issues below, we should always open a corresponding Jira ticket, preferably with the label of `Technical Tasks`.

## Dependencies

We are always trying to reduce external dependencies to keep installs fast and the project simpler.

## Duplicate Code

1. Why do we need a `cca-radio-option` component, since netz already has one?

## Code inconsistencies - Possible refactors

1. In file inputs, we can define the file type accepted like this, to make the broswer popup accept these types by default.
   For example, for EXCEL type files: `accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"`

## Code smells

1. We have directives with HTML which is incorrect. These should be components. We also have components that use the directive syntax on template usage. These also should be components.
