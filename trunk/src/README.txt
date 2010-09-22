There are 3 folders here: client, client_test, and server.
client is the Android application. client_test is the testing application for
the client. server is the Rails server currently deployed on EC2.

In the client, you will now need to either create an account, or log into an
existing one. The existing accounts are as follows:
  Karl
  Saung
  Glen
The password for any of these accounts is 'password'.
It is recommended to not use the Karl account, as it is used by the testing
framework.

The server is now on EC2 and should just work, given that EC2 is working.


CLIENT INSTALLATION
===================
The application is built against the Google APIs + Android, Version 4 (1.6).
Any AVDs/hardware used to run this phone will need to be at least backwards
compatible with 1.6 to run.

Follow these instructions to make the map view work: Copy the debug.keystore
in project7/trunk/docs to .android in your home directory. After the keystore
has been copied, you will need to create a new emulator device in AVD if
running on an emulator. If a previous version of the app is installed on a
hardware device with a keystore other than the one in the repository, then the
app will have to be deleted on the phone to be installed with the new keys.
The changing of the debug keystore is necessary to use the google maps view.

Some of the automated test cases depend on waiting for a server response. If 
there is no server response then those test cases will fail. Also, those test 
cases "sleep" momentarily to wait for the server response; if the server 
response takes longer than usual then the test case may proceed before the 
response and fail.

For Battles: Two players must have accepted a battle to run the battle. If one
player starts attacking another player, currently the battle will wait until 
the other player accepts. To do so, have another Android app out logged in to 
the second player to accept the battle, or log out and log onto the other 
character.


ACCELEROMETER USAGE
===================
The Android Phone's accelerometer is used to process 3 simple gestures,
slapping down(start vertically in front of you with screen facing you and
swing down so that it ends flat in front of you), slapping up(start flat in
front of you with screen facing up and swing up so that it ends vertical), and
stabbing(start flat in front of you with screen facing up and then push
forward while twisting your hand counterclockwise). The user can press down 
the button, perform a gesture, and let go of the button. The attack will 
succeed if it is done in the correct manner and quickly. The moves are 
processed in a simple manner: the x, y, and z accelerations are stored when 
the user presses down the button and when the button is released, and the move
is calculated from these 2 stored states. A more accurate way to process moves
is to trace a curve through the points of the gesture at small time intervals,
but this would be heavy on the hardware and the professor suggestedto keep the
simple 2-state way because the user gets the idea of the gestures. The button
Accelerometer Tester is for testing purposes only.