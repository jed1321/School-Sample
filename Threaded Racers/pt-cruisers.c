/**
@author Jeff DeSain, jed1321

This program races a set of racecars, each on an individual thread
*/

#include "racer.h"
#include <pthread.h>
#include <unistd.h>
#include <semaphore.h>
#include <string.h>
#include <assert.h>
#include "display.h"


#define USAGE "Usage: pt-cruisers [ random-seed ] names..."
#define USAGE_LEN "Usage: racer names must not exceed length 9."


int main(int argc, char** argv)
{	
	int time;
	int index;

	//ensure valid arguments
	if(argc == 1)
	{
		fprintf(stderr, "%s\n", USAGE);
		exit(EXIT_FAILURE);
	}
		
	time = atoi(argv[1]);
	
	if(time != 0 && argc == 2)
	{
		fprintf(stderr, "%s\n", USAGE);
		exit(EXIT_FAILURE);
	}
	
	if(time < 0)
	{
		fprintf(stderr, "Must have a positive time\n");
		exit(EXIT_FAILURE);
	}
	
	//check if a time was entered
	if(time == 0)
	{
		initRacers(DEFAULT_WAIT);
		index = 1;
	}
	else
	{
		initRacers(time);
		index = 2;
	}
	

	for(int i = index; i < argc; i++)
	{
		if(strlen(argv[i]) > MAX_NAME_LEN)
		{
			fprintf(stderr, "%s\n", USAGE_LEN);
			exit(EXIT_FAILURE);
		}
	}
	//end arg validation
	
	
	
	//Allocate space for racers
	unsigned int numCars = argc - index;
	
	Racer** racers = (Racer**)malloc(numCars * sizeof(Racer*));
	assert(racers != NULL);
	int length;
	char* name;
	int count = 0;
	
	for(unsigned int i = 0; i < numCars; i++)
	{
		//construct the name
		count = 0;
		name = (char*)malloc(MAX_NAME_LEN);
		length = strlen(argv[i + index]);
		for(int j = 0; j < (MAX_NAME_LEN - length)/2; j++)
		{
			name[j] = '_';
			count++;
		}
		
		strcpy(name + count, argv[i + index]);
		
		for(int k = (count + length); k < MAX_NAME_LEN; k++)
		{
			name[k] = '_';
		}
		
		//make a racer
		racers[i] = makeRacer(name, i + 1);
	}
	
	//spin off threads
	pthread_t* threads = (pthread_t*)malloc(sizeof(pthread_t) * numCars);
	assert(threads != NULL);
	
	
	for(unsigned int i = 0; i < numCars; i++)
	{
		pthread_create(&threads[i], NULL, run, (void*)racers[i]);
	}
	
	for(unsigned int i = 0; i < numCars; i++)
	{
		pthread_join(threads[i], NULL);
	}
	
	//make the shell echo after the cars
	set_cur_pos(numCars + 1, 1);
	
	//free everything
	for(unsigned int i = 0; i < numCars; i++)
	{
		destroyRacer(racers[i]);
	}
	free(racers);
	free(threads);
	
	return EXIT_SUCCESS;
}