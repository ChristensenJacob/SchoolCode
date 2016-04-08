#ifndef PIPELINE_H
#define PIPELINE_H

#include <atomic>
#include <condition_variable>
#include <cstdlib>
#include <fstream>
#include <iostream>
#include <mutex>
#include <queue>
#include <thread>
#include <numeric>
#include <string>

using std::thread;
using std::mutex;
using std::cout;
using std::cin;
using std::endl;

class Pipeline
{
private:
	static size_t* outarray;
public:
	static std::queue<int> q, t;
	static std::condition_variable q_cond, t_cond;
	static mutex q_sync, t_sync, arr_sync;
	static size_t step1, step2, step3;
	static std::atomic_size_t accum;
	static std::vector<thread> t1, t2, t3;
	static std::atomic_bool quit;

	static void debug(int x);
	Pipeline();
	~Pipeline();
	void begin();
	static void thread_1(int);
	static void thread_2();
	static void thread_3(int);
};

#endif

/*
This project was a nice reprieve from the last one, and a nice easy project to do while preparing for finals.
The only part I had an issue with was getting the threads to print in order, but that was easily solved by using
an external array to keep track of which threads had finished with which values.
*/