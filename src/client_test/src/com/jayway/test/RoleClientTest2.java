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


public class RoleClientTest2 extends InstrumentationTestCase {
	private Solo solo;
	private Activity act;
	
	public RoleClientTest2() {
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
		//solo.assertCurrentActivity("Expected CharStatus activity", "CharStatus");
		assertTrue(solo.searchText("Karl"));
		assertTrue(solo.searchText("Warrior"));
		assertTrue(solo.searchText("/100"));
		solo.clickOnButton(" Back ");
		//solo.assertCurrentActivity("Expected RoleClient activity", "RoleClient");
	}
	
	public void testListPlayers() throws Exception {
		solo.clickOnButton("List Players");
		solo.clickOnButton("Back");
		assertTrue(solo.searchText("Log Out"));
		//solo.assertCurrentActivity("Expected ListPlayers activity", "ListPlayers");
		
		/* FIXME: Known problem here. For some reason, ListPlayers doesn't load
		 * the list correctly during testing. */
		//solo.clickInList(0);
		//solo.assertCurrentActivity("Expected CharStatus activity", "CharStatus");
	}
	
	/*
	public void testWorldMap() throws Exception {
		solo.clickOnButton("World Map");
		solo.assertCurrentActivity("Expected WorldMap activity", "WorldMap");
	}
	*/
	
	public void testLogout() throws Exception {
		assertTrue(solo.searchText("Log Out"));
		solo.clickOnButton("Log Out");
		//solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
	}
	
	// Check if there's a Login button.
	public void testLoginButton() throws Exception {
		//solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
		assertTrue(solo.searchText("Log Out"));
		solo.clickOnButton("Log Out");
		assertTrue(solo.searchText("Use Account"));
		solo.clickOnButton("Use Account");
		//solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		
		// Enter in form information
		// Watching this stuff get auto-typed in the emulator is super-cool :D
		solo.enterText(0, "Karl");
		solo.enterText(1, "password");
		
		//Should also be able to do solo.clickOnButton("Save"), but didn't work for some reason.
		//This is find button with index 0 (first button, aka "Save").
		solo.clickOnButton(0);
		solo.sleep(10000);
		assertTrue(solo.searchText("Log Out"));
	}
	/*
	//test logging into a wrong account
	public void testUseWrongAccount() throws Exception {
		solo.clickOnButton("Log Out");
		solo.clickOnButton("Use Account");
		//solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		
		//enter wrong password
		solo.enterText(0, "Karl");
		solo.enterText(1, "pp");
		solo.clickOnButton(0);
		//solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		solo.sleep(10000);
		assertTrue(solo.searchText("Log in failed."));
		
		//enter nonexisting account
		solo.enterText(0, "iefnvk");
		solo.enterText(1, "lol");
		solo.clickOnButton(0);
		//solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		solo.sleep(10000);
		assertTrue(solo.searchText("Log in failed."));
	}
	*/
	// Check if there's a create account button.
	public void testCreateAccountButton() throws Exception {
		//solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
		solo.clickOnButton("Log Out");
		assertTrue(solo.searchText("Create Account"));
	}
	
	//test navigating create account
	public void testCreateAccount() throws Exception {
		solo.clickOnButton("Log Out");
		solo.clickOnButton("Create Account");
		//solo.assertCurrentActivity("Expected CreateAccount activity", "CreateAccount");
		
		//test select character button
		assertTrue(solo.searchText("Select Character"));
		solo.clickOnButton("Select Character");
		//solo.assertCurrentActivity("Expected CharacterSelection activity", "CharacterSelection");
		assertTrue(solo.searchText("Select your class"));
		
		//test warrior button
		assertTrue(solo.searchText("Warrior"));
		solo.clickOnButton("Warrior");
		assertTrue(solo.searchText("A fierce brawler"));
		
		//test paladin button
		assertTrue(solo.searchText("Paladin"));
		solo.clickOnButton("Paladin");
		assertTrue(solo.searchText("A holy warrior"));
		
		//test assassin button
		assertTrue(solo.searchText("Assassin"));
		solo.clickOnButton("Assassin");
		assertTrue(solo.searchText("Silent, but deadly."));
		
		solo.clickOnButton("Confirm");
		//solo.assertCurrentActivity("Expected CreateAccount activity", "CreateAccount");
		solo.sleep(10000);
		assertTrue(solo.searchText("Log Out"));
	}
	
	//test creating a wrong account
	public void testCreateWrongAccount() throws Exception {
		solo.clickOnButton("Log Out");
		solo.clickOnButton("Create Account");
		//solo.assertCurrentActivity("Expected CreateAccount activity", "CreateAccount");
			
		solo.enterText(0, "k");
		
		solo.enterText(1, "pp");
		solo.clickOnButton("  Save  ");
		
		solo.clickOnButton("Select Character");
		solo.sleep(5000);
		solo.clickOnButton("Warrior");
		solo.clickOnButton("Confirm");
		
		solo.clickOnButton("  Save  ");
		solo.sleep(10000);
		assertTrue(solo.searchText("Name is too short"));
		
		solo.enterText(0, "arl");
		solo.clickOnButton("  Save  ");
		solo.sleep(10000);
		assertTrue(solo.searchText("Name has already been taken"));
		
		solo.enterText(0, "ifkenhoe");
		solo.clickOnButton("  Save  ");
		solo.sleep(10000);
		assertTrue(solo.searchText("Password is too short"));
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
