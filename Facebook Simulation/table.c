/**
@author Jeff Desain, jed1321

Description: Implementation of a hash table
*/

#include "table.h"
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include "amigomem.h"

/**
@param t The table
@param key Key to find
@return index of key in table, or -1 if not found
*/
int getIndex(Table* t, void* key);

/**
@param t The table to rehash
*/
void rehash(Table* tt);


/// Create a new hash table.
/// @param hash The key's hash function
/// @param equals The key's equal function for comparison
/// @param print A print function for the key/value, used for dump debugging
/// @exception Assert fails if can't allocate space
/// @return A newly created table
Table* create(long (*hash)(void* key),
              bool (*equals)(void* key1, void* key2),
              void (*print)(void* key1, void* key2))
{
	Table* aTable = (Table*)malloc(sizeof(Table));

	aTable-> size = 0;
	aTable-> capacity = INITIAL_CAPACITY;
	aTable-> table = (Entry**)calloc(aTable->capacity, sizeof(Entry*));
	
	if(aTable == NULL)
	{
		fprintf(stderr, "table::create failed to allocate space\n");
		assert(NULL);
	}
	

	aTable->hash = hash;
	aTable->equals = equals;
	aTable->print = print;
	
	aTable->collisions = 0;
	aTable->rehashes = 0;
	
	return aTable;
}

/// Destroy a table
/// @param t The table to destroy
void destroy(Table* t)
{
	for(unsigned int i = 0; i < t->capacity; i++)
	{
		if((t->table)[i] != NULL)
		{
			free((t->table)[i]);
		}
	}
		free(t->table);
		free(t);
}

/// Print out information about the hash table (size,
/// capacity, collisions, rehashes).  If full is
/// true, it will also print out the entire contents of the hash table,
/// using the registered print function with each non-null entry.
/// @param t The table to display
/// @param full Do a full dump of entire table contents
void dump(Table* t, bool full)
{
	printf("Size: %zu\nCapacity: %zu\nCollisions: %zu\nRehashes: %zu\n", t->size, t->capacity, t->collisions, t->rehashes);
	
	if(full)
	{
		for(unsigned int i = 0; i < t->capacity; i++)
		{
			printf("%d: ", i);
			if((t->table)[i] != NULL)
			{
				printf("(");
				t->print((t->table)[i]->key, (t->table)[i]->value); 
				printf(")");
			}
			else
				printf("%s", "null");
				
			printf("%s","\n");
		}
	}
}

/**
@param t The table
@param key Key to find
@return index of key in table, or -1 if not found
*/
int getIndex(Table* t, void* key)
{
	Entry** entries = t->table;
	int answer = -1;
	unsigned int index = (t->hash(key)) % t->capacity;
	unsigned int initialIndex = index;
	bool found = false;
	
	while(entries[index] != NULL && !found)
	{
		//Check if the keys match
		if(t->equals(entries[index]->key, key))
		{
			answer = index;
			found = true;
		}
		else
		{
			t->collisions++;
			//If not at the end of the table, go to next element
			if(index < (t->capacity) - 1)
			{
				index++;
			}
			//Otherwise wrap to the beginning
			else
			{
				index = 0;
			}
			//Ensure search isn't looping through the table multiple times
			if(index == initialIndex)
				found = true;
		}
	}
	
	return answer;
}

/// Get the value associated with a key from the table.  This function
/// uses the registered hash function to locate the key, and the
/// registered equals function to check for equality.
/// @pre The table must have the key, or else it will assert fail
/// @param t The table
/// @param key The key
/// @return The associated value of the key
void* get(Table* t, void* key)
{
	void* answer = NULL;
	int index = getIndex(t, key);
	
	if(index != -1)
	{
		answer = (t->table)[index]->value;
	}
	else
	{
		fprintf(stderr, "table::get key not found\n");
		assert(NULL);
	}
	return answer;
}

/// Check if the table has a key.  This function uses the registered hash
/// function to locate the key, and the registered equals function to
/// check for equality.
/// @param t The table
/// @param key The key
/// @return Whether the key exists in the table.
bool has(Table* t, void* key)
{
	//If we can find the key, the table has the key
	return(getIndex(t, key) != -1);
}

/// Get the collection of keys from the table.  This function allocates
/// space to store the keys, which the caller is responsible for freeing.
/// @param t The table
/// @exception Assert fails if can't allocate space
/// @return A dynamic array of keys
void** keys(Table* t)
{
	//allocate memory for keys
	void** k = (void**)malloc(sizeof(void*) * t->size);
	if(k == NULL)
	{
		fprintf(stderr, "table::keys failed to allocate memory\n");
		assert(NULL);
	}
	
	int stored = 0;
	//copy all the keys over
	for(unsigned int i = 0; i < t->capacity; i++)
	{
		if((t->table)[i] != NULL)
		{
			k[stored++] = ((t->table)[i])->key;
		}
	}
	
	return k;
}

/// Add a key value pair to the table, or update an existing key's value.
/// This function uses the registered hash function to locate the key,
/// and the registered equals function to check for equality.
/// @param t The table
/// @param key The key
/// @param value The value
/// @exception Assert fails if can't allocate space
/// @return The old value in the table, if one exists.
void* put(Table* t, void* key, void* value)
{
	void* answer = NULL;
	int index;
	
	//Check if already in the table
	if((index = getIndex(t, key)) != -1)
	{
		answer = (t->table)[index]->value;
		(t->table)[index]->value = value;
	}
	else
	{
		Entry** entries = (t->table);
		index = (t->hash(key)) % t->capacity;
		
		//find the next open index
			while(entries[index] != NULL)
			{
				if((unsigned int)index < t->capacity - 1)
					index++;
				else
					index = 0;
			}
			
			entries[index] = (Entry*)malloc(sizeof(Entry));
			
			if(entries[index] == NULL)
			{
				fprintf(stderr, "table::put failed to allocate memory\n");
				assert(NULL);
			}
			entries[index]->key = key;
			entries[index]->value = value;
			t->size++;
	}
	
	//Rehash if table is too full
	if(((float)t-> size / t->capacity) >= LOAD_THRESHOLD)
	{
		rehash(t);
	}
	
	return answer;
}

/**
@param t The table to rehash
*/
void rehash(Table* t)
{
	
	//Copy table of entries to a temporary table
	Entry** tmp = (Entry**)calloc(t->capacity, sizeof(Entry*));
	unsigned int tmpCap = t->capacity;
	for(unsigned int i = 0; i < t->capacity; i++)
	{
		tmp[i] = t->table[i];
	}
		
	
	free(t->table);
	
	
	t-> size = 0;
	t-> capacity *= RESIZE_FACTOR;
	t-> rehashes++;

	//Put the entries back in the Table
	t-> table = (Entry**)calloc(t->capacity, sizeof(Entry*));
	if(t-> table == NULL)
	{
		fprintf(stderr, "table::rehash failed to allocate memory\n");
		assert(NULL);
	}
	
	for(unsigned int i = 0; i < tmpCap; i++)
	{
		if(tmp[i] != NULL)
		{
			put(t, tmp[i]->key, tmp[i]->value);
			free(tmp[i]);
		}
	}
	
	free(tmp);
	
}



/// Get the collection of values from the table.  This function allocates
/// space to store the values, which the caller is responsible for freeing.
/// @param t The table
/// @exception Assert fails if can't allocate space
/// @return A dynamic array of values
void** values(Table* t)
{
	//allocate pointer for values
	void** v = (void**)malloc(sizeof(void*) * t->size);
	if(v == NULL)
	{
		fprintf(stderr, "table::keys failed to allocate memory");
		assert(NULL);
	}
	
	int stored = 0;
	
	//copy over values
	for(unsigned int i = 0; i < t->capacity; i++)
	{
		if((t->table)[i] != NULL)
		{
			v[stored++] = ((t->table)[i])->value;
		}
	}	
	return v;
}