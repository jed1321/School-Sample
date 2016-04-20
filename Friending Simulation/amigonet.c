/**
@author Jeff DeSain, jed1321

This program uses a hash table to simulate facebook friend requests
*/


#include "amigomem.h"
#include "amigonet.h"
#include "table.h"
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include "hash.h"

#define _BSD_SOURCE
#define _GNU_SOURCE

Table* UserTable;

typedef struct User_S 
{
    char* name;
    char* birthday;
	char* id;
	short friendCount;
	short friendCap;
	struct User_S** friends;
} User;


//Function to print strings
void strStrPrint(void* key, void* value) {
    printf("%s : %s", (char*)key, (char*)value);
}

/**
 * Initialize the system.
 * (This is where some memory is initially allocated.)
 */
void create_amigonet()
{
	UserTable = create(strHash, strEquals, strStrPrint);
}

/**
 * Shut down the system.
 * (This is where a bunch of memory is freed.)
 */
void destroy_amigonet()
{
	User** users = (User**)values(UserTable);
	
	//For each user, free all of the dymanically allocated fields.
	for(unsigned int i = 0; i < UserTable->size; i++)
	{
		free(users[i]->name);
		free(users[i]->birthday);
		free(users[i]->id);
		free(users[i]->friends);
		free(users[i]);
	}
	
	//free the array of values
	free(users);
	
	destroy(UserTable);
}

/**
 * Add a new user with initially no friends.
 * The parameters 'name' and 'birthday' are used to initialize the new user entry.
 * Note: they must be copied to prevent the caller from changing
 * the them later.
 * If the name already exists, then this function does nothing, except for
 * print an error message.
 */
void addUser( const char *name, const char *birthdate )
{

	char* id;
	int year = 0;
	char aYear[5];
	
	//ensure valid input
	if(strlen(name) < 3 || strlen(name) > MAX_NAME_LENGTH || strlen(birthdate) != 10)
	{
		fprintf(stderr, "Error, invalid arguments for addUser.\n");
		return;
	}
	else 
	{
		strncpy(aYear, birthdate + 6, (size_t)4);
		aYear[4] = '\0';
		year = atoi(aYear);
		
		if(year < 1700 || year > 2200)
		{
			fprintf(stderr, "Error, invalid arguments for addUser.\n");
			return;
		}
		
	}
	
	//Temporary holders for name and date
	char* aName = (char*)malloc(strlen(name) + 1);
	strcpy(aName, name);
	
	char* aDate = (char*)malloc(strlen(birthdate) + 1);
	strcpy(aDate, birthdate);
	
	
	id = (char*)calloc(strlen(aName) + strlen(aDate) + 1, sizeof(char));
	

	
	if(aName == NULL || aDate == NULL || id == NULL)
	{
		fprintf(stderr, "amigonet::addUser failed to allocate memory");
		assert(NULL);
	}
	
	//id = name + date
	strcat(id, aName);
	strcat(id, aDate);
	
	//make array for friends and pointer to user
	User** friends = (User**)malloc(sizeof(User*) * 3);
	User* user = (User*)malloc(sizeof(User));
	
	//allocate memory for user fields
	user->name = (char*)malloc(strlen(aName) + 1);
	user->birthday = (char*)malloc(strlen(aDate) +1);
	user->id = (char*)malloc(strlen(id) + 1);
	
	if(user->name == NULL || user->birthday == NULL || user->id == NULL)
	{
		fprintf(stderr, "amigonet:addUser failed to allocate memory\n");
		assert(NULL);
	}
	
	//copy over strings
	strcpy(user->birthday, aDate);
	strcpy(user->name, aName);
	strcpy(user->id, id);
	
	//free the temp variables
	free(aName);
	free(aDate);
	free(id);
	
	//initialize other variables
	user->friendCount = 0;
	user->friendCap = 3;
	user->friends = friends;
	
	if(!has(UserTable, user->id))
	{
		put(UserTable, (void*)(user->id), (void*)user);
	}
	else
	{	
		//if the user exists, free all the variables we just allocated
		free(user->birthday);
		free(user->name);
		free(user->id);
		free(user->friends);
		free(user);
		fprintf(stderr, "Error, user already exists.\n");
	}
}


/**
 * Print out the specified user's friends in the order that they were
 * "friended"
 */
void printAmigos( struct User_S *user )
{
	//Ensure user exists
	if(user == NULL)
	{
		fprintf(stderr, "Error, user doesn't exist.\n");
	}
	else
	{
		//Print out all friends
		User** f = user->friends;
	
		if(user->friendCount == 1)
		{
			printf("%d friend\n", user->friendCount);
		}
		else
		{
			printf("%d friends\n", user->friendCount);
		}
	
		for(int i = 0; i < user-> friendCount; i++)
		{
			printf("%s, %s\n", (f[i])->name, (f[i])->birthday);
		}
	}
}

/**
 * Print the number of users.
 */
void printNumUsers(void)
{
	printf("Registered Users: %zu\n", UserTable->size);
}

/**
 * Locate a user structure using the user's name and birthdate as a key.
 * User structures are needed for the addAmigo, removeAmigo,
 * and printFriends functions.
 * If the user does not exist, NULL is returned.
 */
struct User_S *findUser( const char *name, const char *birthdate )
{
	User* answer = NULL;
	char* id = (char*)calloc(strlen(name) + strlen(birthdate) + 1, sizeof(char));
	
	strcat(id, name);
	strcat(id, birthdate);
	
	//check the table for the key corresponding to name and birthdate
	if(has(UserTable, id))
	{
		answer = (User*)get(UserTable, id);
	}

	free(id);
	
	
	
	return answer;
}


/**
 * Add a friend (the "amigo") to the user. This should be a two-way
 * addition. If the two users are already friends., this function
 * does nothing except print an error message. 
 */
void addAmigo( struct User_S *user, struct User_S *amigo )
{
	User** f1 = user->friends;
	User** f2 = amigo->friends;
	bool exists = false;
	
	//check for existing friendship
	for(int i = 0; i < user->friendCount; i++)
	{
		if(strEquals((*f1[i]).id, amigo->id))
		{
			exists = true;
		}
	}
	
	if(exists)
	{
		fprintf(stderr, "Error, %s and %s are already friends.\n", user->name, amigo->name);
	}
	else
	{
		//ensure enough room for friends
		if(user->friendCount < user->friendCap)
		{
			f1[user->friendCount] = amigo;
			user->friendCount++;
		}
		else
		{
			//Allocate more room for friends

			User** tmp = (User**)malloc(sizeof(User*) * user->friendCap);	
			
			if(tmp == NULL)
			{
				fprintf(stderr, "amigonet:addAmigo failed to allocate memory");
				assert(NULL);
			}
			
			for(int i = 0; i < user->friendCount; i++)
			{
				tmp[i] = f1[i];
			}
			
			user->friendCap += 2;
			free(user->friends);
			user->friends = (User**)malloc(sizeof(User*) * user->friendCap);
			
			for(int i = 0; i < user->friendCount; i++)
			{
				user->friends[i] = tmp[i];
			}
			
			free(tmp);
			
			f1 = user->friends;
			
			
			//add the new friend after new memory is allocated
			f1[user->friendCount] = amigo;
			user->friendCount++;
		}
		
		
		//ensure enough room for amigo friends
		if(amigo->friendCount < amigo->friendCap)
		{
			f2[amigo->friendCount] = user;
			amigo->friendCount++;
		}
		else
		{
			//Allocate more room for friends
			User** tmp = (User**)malloc(sizeof(User*) * amigo->friendCap);	
			
			if(tmp == NULL)
			{
				fprintf(stderr, "amigonet:addAmigo failed to allocate memory");
				assert(NULL);
			}
			
			for(int i = 0; i < amigo->friendCount; i++)
			{
				tmp[i] = f2[i];
			}
			
			amigo->friendCap += 2;
			free(amigo->friends);
			amigo->friends = (User**)malloc(sizeof(User*) * amigo->friendCap);
			
			for(int i = 0; i < amigo->friendCount; i++)
			{
				amigo->friends[i] = tmp[i];
			}
			
			free(tmp);
			
			f2 = amigo->friends;
			
			//add the new friend after new memory is allocated
			f2[amigo->friendCount] = user;
			amigo->friendCount++;
		}
		
	}
}

/**
 * "Un-friend" two users. This is, again, a two-way operation.
 * If the two users were not friends, this function does nothing,
 * except print an error message.
 */
void removeAmigo( struct User_S *user, struct User_S *ex_amigo )
{
	if(user == NULL || ex_amigo == NULL)
	{
		fprintf(stderr, "Error, user doesn't exist.\n");
	}

	
	User** f1 = user->friends;
	User** f2 = ex_amigo->friends;
	bool exists = false;
	
	//check for existing friendship
	for(int i = 0; i < user->friendCount; i++)
	{
		if(strEquals((f1[i])->id, ex_amigo->id))
		{
			exists = true;
		}
	}
	
	if(!exists)
	{
		fprintf(stderr, "Error, %s and %s are not friends.\n", user->name, ex_amigo->name);
	}
	else
	{
		//remove from user
		int index = 0;
		
		while(!strEquals((f1[index])->id, ex_amigo->id))
		{
			index++;
		}
		
		for(int i = index; i < user->friendCount; i++)
			f1[i] = f1[i+1];
		
		user->friendCount--;
		
		//shrink friends if too big
		/*
		if((user->friendCap - user->friendCount) > 3)
		{
			user->friendCap -=3;
			user->friends = (User**)realloc(user->friends, user->friendCap);
		}*/
		
		//remove from amigo
		index = 0;
		
		while(!strEquals((f2[index])->id, user->id))
		{
			index++;
		}
		
		for(int i = index; i < ex_amigo->friendCount; i++)
			f2[i] = f2[i+1];
		
		ex_amigo->friendCount--;
		
		//shrink friends if too big
		/*
		if((ex_amigo->friendCap - ex_amigo->friendCount) > 3)
		{
			ex_amigo->friendCap -=3;
			ex_amigo->friends = (User**)realloc(ex_amigo->friends, ex_amigo->friendCap);
		}*/
	}
}

