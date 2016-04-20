#!/bin/bash

# Output detailed parsing info for debugging
#set -x

if eval "readelf -s *.o | awk '{print \$8}' | grep -q -P \"^malloc\$\""; then
   echo ""
   echo "Error, malloc is called directly, include amigomem.h in all .c files"
   exit 1
fi

if eval "readelf -s *.o | awk '{print \$8}' | grep -q -P \"^realloc\$\""; then
   echo ""
   echo "Error, realloc is called directly, include amigomem.h in all .c files"
   exit 1
fi

if eval "readelf -s *.o | awk '{print \$8}' | grep -q -P \"^calloc\$\""; then
   echo ""
   echo "Error, calloc is called directly, include amigomem.h in all .c files"
   exit 1
fi

if eval "readelf -s *.o | awk '{print \$8}' | grep -q -P \"^strdup\$\""; then
   echo ""
   echo "Error, strdup is called, do not use strdup in this project."
   exit 1
fi

if eval "readelf -s *.o | awk '{print \$8}' | grep -q -P \"^getline\$\""; then
   echo ""
   echo "Error, getline is called, do not use getline in this project."
   exit 1
fi

exit 0

