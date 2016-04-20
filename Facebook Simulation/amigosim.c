/*
 * amigosim.c
 *
 * Simulate AmigoNet activity by reading actions from a text file.
 *
 * James Heliotis, Chris Dickens
 *
 * COMMAND FORMATS:
 * register <name> <birthdate>
 * friend <name1> <birthdate1> <name2> <birthdate2>
 * unfriend <name1> <birthdate1> <name2> <birthdate2>
 * printFriends <name> <birthdate>
 * countUsers
 *
 * Comment lines begin with "#".
 * They are echoed in the output, but otherwise ignored.
 *
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>

#include "amigonet.h"

#ifndef UNINSTRUMENTED
#include "amigomem.h"
#endif


//#define DEBUG


static const char REGISTER[] = "addUser";
static const char FRIEND[] = "friend";
static const char UNFRIEND[] = "unfriend";
static const char PRINTFRIENDS[] = "printFriends";
static const char PRINTNUMUSERS[] = "countUsers";
static const char EXIT[] = "exit";

#define MAX_LEN 100 // This big for comments


/*************************************************************
 *                                                           *
 * Calls to the AmigoNet API                                 *
 *                                                           *
 *************************************************************/

 //Function to add a new user
static void do_register( const char *name, const char *birthdate )
{
    addUser( name, birthdate );
}

//Function to print friends for a user
static void do_printFriends( const char *name, const char *birthdate )
{
	struct User_S* user = findUser(name, birthdate);
	//If the user exists, print their friends
	if(user != NULL)
	{
		printAmigos(user);
	}
	else
	{
		fprintf(stderr, "Error, user doesn't exist.\n");
	}
}

//Function to print the number of users
static void do_printNumUsers(void)
{
    printNumUsers();
}

//Function to associate two users as friends
static void do_friend( const char *name1, const char *birthdate1, 
    const char *name2, const char *birthdate2 )
{
	struct User_S* user1 = findUser(name1, birthdate1);
	struct User_S* user2 = findUser(name2, birthdate2);
	
	//Ensure both users exist, then add each other as friends
	if(user1 != NULL && user2 != NULL)
	{
		if(user1 == user2)
		{
			fprintf(stderr, "Error, cannot friend yourself.\n");
		}
		else
		{
			addAmigo(user1, user2);
		}
	}
	else
	{
		fprintf(stderr, "Error, user doesn't exist.\n");
	}
}

//Function to unfriend two users
static void do_unfriend( const char *name1, const char *birthdate1,
    const char *name2, const char *birthdate2 )
{
	struct User_S* user1 = findUser(name1, birthdate1);
	struct User_S* user2 = findUser(name2, birthdate2);
	
	//Ensure both users exist, then unfriend both users
	if(user1 && user2)
	{
		removeAmigo(user1, user2);
	}
	else
	{
		fprintf(stderr, "Error, user doesn't exist.\n");
	}
}

/*************************************************************
 *                                                           *
 * Helper Functions                                          *
 *                                                           *
 *************************************************************/

 static char* alloc_and_cat(char* str1, char* str2)
{
    char* ptr;
    int size = strlen(str1) + strlen(str2) + 2;

    ptr = (char*)malloc(size);
    if(ptr == NULL)
    {
        fprintf(stderr, "Failed to allocate memory\n");
        assert(NULL);
    }

    strncpy(ptr, str1, size);
    strncat(ptr, " ", size-strlen(str1));
    strncat(ptr, str2, size-strlen(str1)-1);
   
    return ptr;
}


/*************************************************************
 *                                                           *
 * Command input loop                                        *
 *                                                           *
 *************************************************************/

int main() {
    create_amigonet();
    char line[ MAX_LEN ];
    char *result, *cmd, *arg1, *arg2;
    char *name1, *name2, *birthdate1, *birthdate2;
    printf("> ");
    fflush(stdout);
    while ( ( result = fgets( line, MAX_LEN, stdin ) ) ) {
        if ( line[ 0 ] == '#' ) {
            printf( "%s", line );
        }
        else {
            #ifdef DEBUG
            printf("%s", line);
            #endif
            cmd = strtok( line, " \n" );
            if( cmd == NULL)
            {
                // do nothing
            }
            else if ( strcmp( cmd, REGISTER ) == 0 )
            {
                arg1 = strtok( NULL, " \n" );
                arg2 = strtok( NULL, " \n" );
                birthdate1 = strtok( NULL, " \n" );
                if( arg1 && arg2 && birthdate1 && (birthdate1[2] == '/') && (birthdate1[5] == '/'))
                {
                    name1 = alloc_and_cat( arg1, arg2 );
                    do_register( name1, birthdate1 );
                    free(name1);
                }
                else
                {
                    fprintf(stderr, "Error, invalid arguments for addUser.\n");
                }
            }
            else if ( strcmp( cmd, PRINTFRIENDS) == 0 )
            {
				arg1 = strtok( NULL, " \n" );
                arg2 = strtok( NULL, " \n" );
                birthdate1 = strtok( NULL, " \n" );
                if( arg1 && arg2 && birthdate1 && (birthdate1[2] == '/') && (birthdate1[5] == '/'))
                {
                    name1 = alloc_and_cat( arg1, arg2 );
                    do_printFriends( name1, birthdate1 );
                    free(name1);
                }
                else
                {
                    fprintf(stderr, "Error, invalid arguments for printFriends.\n");
                }
				
            }
            else if ( strcmp( cmd, PRINTNUMUSERS) == 0 )
            {
                do_printNumUsers();
            }
            else if ( strcmp( cmd, EXIT) == 0 )
            {
                break;
            }
            else if ( strcmp( cmd, FRIEND ) == 0 ) 
            {
				//get person 1
				arg1 = strtok( NULL, " \n" );
                arg2 = strtok( NULL, " \n" );
                birthdate1 = strtok( NULL, " \n" );
                if( arg1 && arg2 && birthdate1 && (birthdate1[2] == '/') && (birthdate1[5] == '/'))
                {
                    name1 = alloc_and_cat( arg1, arg2 );
					
					//get person 2
					arg1 = strtok( NULL, " \n" );
					arg2 = strtok( NULL, " \n" );
					birthdate2 = strtok( NULL, " \n" );
					if( arg1 && arg2 && birthdate1 && (birthdate1[2] == '/') && (birthdate1[5] == '/'))
					{
						name2 = alloc_and_cat( arg1, arg2 );
						
						//add to friends
						do_friend(name1, birthdate1, name2, birthdate2);
						free(name1);
						free(name2);
					}
					else
					{
						fprintf(stderr, "Error, invalid arguments for friend.\n");
					}
                }
                else
                {
                    fprintf(stderr, "Error, invalid arguments for friend.\n");
                }	
            }
            else if ( strcmp( cmd, UNFRIEND ) == 0 )
            {
				//get person 1
				arg1 = strtok( NULL, " \n" );
                arg2 = strtok( NULL, " \n" );
                birthdate1 = strtok( NULL, " \n" );
                if( arg1 && arg2 && birthdate1 && (birthdate1[2] == '/') && (birthdate1[5] == '/'))
                {
                    name1 = alloc_and_cat( arg1, arg2 );
					
					//get person 2
					arg1 = strtok( NULL, " \n" );
					arg2 = strtok( NULL, " \n" );
					birthdate2 = strtok( NULL, " \n" );
					if( arg1 && arg2 && birthdate1 && (birthdate1[2] == '/') && (birthdate1[5] == '/'))
					{
						name2 = alloc_and_cat( arg1, arg2 );
						
						//fremove from friends
						do_unfriend(name1, birthdate1, name2, birthdate2);
						free(name1);
						free(name2);
					}
					else
					{
						fprintf(stderr, "Error, invalid arguments for unfriend.\n");
					}
                }
                else
                {
                    fprintf(stderr, "Error, invalid arguments for unfriend.\n");
                }	
            }
            else {
                fprintf( stderr, "Error, illegal command \"%s\"\n", line );
            }
        }
        printf("> ");
        fflush(stdout);
    }
    destroy_amigonet();
	
	return 1;
}

