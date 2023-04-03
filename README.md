# Thread-pool
The thread pool contains two queues, each of which is served by 2 workers  streams Each execution queue has a limited 
size of 10 tasks. Tasks are added to the execution queue through one interface (the user does not have explicit access
to execution queues). Tasks are added immediately at the end less than a full execution queue, or are discarded if they
are not are placed in the execution queue. The challenge takes a random time between 4 and 10 seconds