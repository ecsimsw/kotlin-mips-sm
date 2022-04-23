int main()
{
	int fib_n = 10;
	int result;

	result = fib(fib_n);
}

int fib(int n)
{
	int result;
	if (n <= 2) return 1;
	else {
		result = fib(n-1) + fib(n-2);
		return result;
	}
}
