#include "Pipeline.h"

std::queue<int> Pipeline::q, Pipeline::t;
std::condition_variable Pipeline::q_cond, Pipeline::t_cond;
mutex Pipeline::q_sync, Pipeline::t_sync, Pipeline::arr_sync;
size_t Pipeline::step1, Pipeline::step2, Pipeline::step3 = 10;
std::atomic_size_t Pipeline::accum{ 0 };
std::vector<thread> Pipeline::t1, Pipeline::t2, Pipeline::t3;
std::atomic_bool Pipeline::quit{ false };
size_t* Pipeline::outarray = new size_t[step3];

Pipeline::Pipeline()
{
	
}

Pipeline::~Pipeline()
{

}


void Pipeline::begin()
{
	cout << "Press ENTER to begin generating numbers";
	cin.get();
	for (size_t i = 0; i < step1; ++i)
		t1.push_back(thread(Pipeline::thread_1, i));
	cout << "Press ENTER to stop generating numbers";
	cin.get();
	Pipeline::quit = true;
	for (auto &t1x : t1)
		t1x.join();

	for (size_t i = 0; i < step2; ++i)
		t2.push_back(thread(Pipeline::thread_2));

	for (auto &t2x : t2)
		t2x.join();

	q = t;

	for (size_t i = 0; i < step3; ++i)
		t3.push_back(thread(Pipeline::thread_3, i));

	for (auto &t3x : t3)
		t3x.join();

	for (size_t i = 0; i < step3; ++i)
		cout << "Group " << i << " has " << outarray[i] << " numbers" << endl;
	
}

void Pipeline::thread_1(int i)
{
	std::srand(time(nullptr)+i);
	while (!quit.load())
	{
		int n = rand();

		std::unique_lock<mutex> slck(q_sync);
		q.push(n);
		slck.unlock();
		q_cond.notify_one();
	}
}

void Pipeline::thread_2()
{
	while (true)
	{
		std::unique_lock<mutex> qlck(q_sync);
		if (q.empty())
		{
			qlck.unlock();
			q_cond.notify_one();
			break;
		}
		int n = q.front();
		q.pop();
		qlck.unlock();
		q_cond.notify_one();
		if (n % 3 != 0)
		{
			std::unique_lock<mutex> tlck(t_sync);
			t.push(n);
			tlck.unlock();
			t_cond.notify_one();
		}
	}
}

void Pipeline::thread_3(int x)
{
	bool doWork = false;
	std::queue<int> out;

	while (true)
	{
		std::unique_lock<mutex> qlck(q_sync);
		if (q.empty())
		{
			qlck.unlock();
			q_cond.notify_one();
			break;
		}
		int n = q.front();
		if (n % step3 == x)
		{
			q.pop();
			doWork = true;
		}
		qlck.unlock();
		q_cond.notify_one();

		if (doWork)
		{
			out.push(n);
		}

		doWork = false;
	}

	std::unique_lock<mutex> arrlck(arr_sync);
	outarray[x] = out.size(); // This used to be where the 'group x has #' would print, but I moved it to the end of begin() for organization
	arrlck.unlock();

	std::string path = "group" + std::to_string(x) + ".txt";
	std::ofstream f(path);
	while (!out.empty())
	{
		f << out.front() << endl;
		out.pop();
	}
	f.close();
}