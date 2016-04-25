/**
@author Jeff DeSain, jed1321

This program provides functions for creating and dealing with racers
*/

#define _BSD_SOURCE
#include "racer.h"
#include <assert.h>
#include "display.h"
#include <pthread.h>
#include <unistd.h>

long waitTime;
pthread_mutex_t mutex;

// initRacers - Do setup work for all racers at the start of the program.
// @param milliseconds length of pause between steps in animation 
//
void initRacers( long milliseconds )
{
	waitTime = milliseconds;
	clear();
	pthread_mutex_init(&mutex, NULL);
	srand(time(NULL));
}

// makeRacer - Create a new racer.
//
// @param name the string name to show on the display for this racer
// @param position the row in which to race
// @return Racer pointer a dynamically allocated Racer object
// @pre strlen( name ) < MAX_NAME_LEN, for display reasons.
//
Racer *makeRacer( char *name, int position )
{
	Racer* r = (Racer*)malloc(sizeof(Racer));
	assert(r!= NULL);
	
	r->dist = 1;
	r->row = position;
	r->graphic = name;
	
	return r;
}

// destroyRacer - Destroy all dynamically allocated storage for a racer.
//
// @param racer the object to be de-allocated
//
void destroyRacer( Racer *racer )
{
	free(racer->graphic);
	free(racer);
}

// run Run one racer in the race.
// Initialize the display of the racer*:
//   The racer starts at the start position, column 1.
//   The racer's graphic (text name ) is displayed.
// This action happens repetitively, until its position is at FINISH_LINE:
//   Randomly calculate a waiting period, no more than
//   the value given to initRacers
//   Sleep for that length of time.
//   Change the display position of this racer by +1 column*:
//     Erase the racer's name from the display.
//     Update the racer's dist field by +1.
//     Display the racer's name at the new position.
//
// The intention is to execute this function many times simultaneously,
// each in its own thread.
//
// note: Care is taken to keep the update of the display by one racer "atomic".
//
//
// @pre racer cannot be NULL.
// @param racer a Racer, declared as void* to be comptable with pthread
//		  interface
// @return void pointer (NULL)
//
void *run( void *racer )
{
	Racer* r = (Racer*) racer;
	
	
	while(r->dist <= FINISH_LINE)
	{
		pthread_mutex_lock(&mutex);
		
		//delete old car tail
		set_cur_pos(r->row, r->dist - 2);
		put(' ');
		
		//draw new car
		set_cur_pos(r->row, r->dist);
		for(int i = 0; i < MAX_NAME_LEN; i++)
		{
			put(r->graphic[i]);
			set_cur_pos(r->row, i + r->dist);
		}
		pthread_mutex_unlock(&mutex);
		
		//update position and wait
		r->dist++;
		usleep((rand()%waitTime) * 1000);
	}

	return (void*)NULL;
}