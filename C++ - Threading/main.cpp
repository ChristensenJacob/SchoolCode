#include "Pipeline.h"

#include <iostream>

int main(int argc, char* argv[])
{
	if (argc < 3)
	{
		std::cout << "2 integer arguments expected: number of threads for <step 1>, <step 2>";
		return 0;
	}

	Pipeline::step1 = atoi(argv[1]);
	Pipeline::step2 = atoi(argv[2]);
	Pipeline p;

	p.begin();

	return 0;
}