LOCATIONS OF TESTS
==================

We do not actually have any tests in this folder. Tests are in the following
locations:

Client: src/client_test
Server: src/server/spec


ABOUT RUNNING TESTS
===================

CLIENT
------
The tests use Android's InstrumentationTestRunner, as well as the Robotium
testing framework. Robotium runs tests by pretending to be an actual user
using the application. You can actually see the actions being taken by looking
at the emulator while the tests are running.

This is a separate project from the client itself (just the way Android
testing works).

There are a couple issues to be aware of regarding this testing setup:

- In our experience, the InstrumentationTestRunner doesn't always attach to
the emulator correctly. If the application doesn't load on the emulator within
a reasonable timeframe, try again.

- The tests rely on the server running properly (the server is not mocked).
Meaning that tests which should otherwise pass will fail if the server is down
or is currently in a state where it either cannot return requests in a timely
manner, or is otherwise in a dysfunctional state.


SERVER
------
These tests are mainly Unit tests, with a few integration tests.

The tests can be run via the command "rake spec" from the src/server root
folder. Both ruby and rubygems must be installed. In addition, the following
gems are required:

rails
rspec
rspec-rails
sqlite3-ruby

As well as the sqlite3 libraries in the ruby bin folder. Alternatively, 
a different DB can be used if it is specified properly in
src/server/config/database.yml (for the test database).

Since the other gems are frozen, the only gem that should be needed is rails
(version 2.3.5). However, the SQLite3 binaries are still needed. There are
numerous guides available on the web for how to accomplish this.
