/*
 * NOTE: Testing with Robotium
 * This is a Java package that basically simulates user input.
 * In other words, this is black-box testing, and in addition
 * depends on the server actually working.
 * 
 * It is also super-slow, because it simulates user input. But I think
 * this is easier at this point than trying to figure out how to
 * mock Activities. It is fairly straight-forward to write tests,
 * see my sample. The main problem is server responses.
 */

package com.jayway.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.InstrumentationTestCase;

import com.jayway.android.robotium.solo.Solo;
import ucb.cs169.project7.*;


public class RoleClientTest extends InstrumentationTestCase {
	private Solo solo;
	private Activity act;
	
	public RoleClientTest() {
          super();
	}
	
	public void setUp() throws Exception {
		// Create a dud activity, in order to set paramters in the application
		act = launchActivity("ucb.cs169.project7", LoginScreen.class, new Bundle());
		RoleClientApplication app = ((RoleClientApplication) act.getApplication());
		app.setPlayerId(1);
		app.setPlayerName("Karl");
		app.setmaxhealth(100);
		
		// Launch the actual activity you want (RoleClient) from the dud activity
		Intent myIntent = new Intent(act, RoleClient.class);
		act.startActivityForResult(myIntent, 0);
				
		solo = new Solo(getInstrumentation(), act);
	}
	
	// ==========================
	// INSERT TESTS STARTING HERE
	
	public void testActivity() throws Exception {
		//solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
		solo.assertCurrentActivity("Expected RoleClient activity", "RoleClient");
	}
	
	public void testStatus() throws Exception {
		solo.clickOnButton("My Status");
		solo.assertCurrentActivity("Expected CharStatus activity", "CharStatus");
		assertTrue(solo.searchText("Karl"));
		assertTrue(solo.searchText("Warrior"));
		assertTrue(solo.searchText("/100"));
		solo.clickOnButton(" Back ");
		solo.assertCurrentActivity("Expected RoleClient activity", "RoleClient");
	}
	
	public void testListPlayers() throws Exception {
		solo.clickOnButton("List Players");
		solo.assertCurrentActivity("Expected ListPlayers activity", "ListPlayers");
		
		/* FIXME: Known problem here. For some reason, ListPlayers doesn't load
		 * the list correctly during testing. */
		solo.clickInList(0);
		solo.assertCurrentActivity("Expected CharStatus activity", "CharStatus");
	}
	
	public void testWorldMap() throws Exception {
		solo.clickOnButton("World Map");
		solo.assertCurrentActivity("Expected WorldMap activity", "WorldMap");
	}
	
	public void testLogout() throws Exception {
		solo.clickOnButton("Log Out");
		solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
	}
	
	// END OF TESTS
	// ============
	
	@Override
	public void tearDown() throws Exception {

		try {
			solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		act.finish();
		super.tearDown();

	}
}
