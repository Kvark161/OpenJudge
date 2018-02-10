#include "testlib.h"

int main() {
    registerValidation();
	int a = inf.readInt(0, 100, "A");
	inf.readSpace();
	int b = inf.readInt(0, 100, "B");
	inf.readEoln();
	inf.readEof();
}