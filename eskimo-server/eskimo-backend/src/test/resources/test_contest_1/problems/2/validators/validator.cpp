#include "testlib.h"

int main() {
    registerValidation();
	int a = inf.readInt(0, 1000, "A");
	inf.readSpace();
	int b = inf.readInt(0, 1000, "B");
	inf.readEoln();
	inf.readEof();
}