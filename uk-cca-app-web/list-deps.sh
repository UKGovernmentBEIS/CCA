#! /bin/bash

# prints the number of project dependencies

dep_count=$(yarn list --prod | wc -l)
echo "Production dependencies: $dep_count" 

dep_count=$(yarn list | wc -l)

echo "Dev dependencies: $dep_count"