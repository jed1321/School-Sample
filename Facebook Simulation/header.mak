
CFLAGS = -Wall -Wextra -pedantic -std=c99 -ggdb -Werror
LFLAGS = -ggdb
CLIBFLAGS = -L/home/course/csci243/pub/projects/amigonet2 -lamigomem

default: all checksymbols

checksymbols:
	@./checksyms.sh

