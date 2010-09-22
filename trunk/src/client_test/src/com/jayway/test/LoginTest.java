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

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import ucb.cs169.project7.*;


public class LoginTest extends ActivityInstrumentationTestCase2<LoginScreen> {
	private Solo solo;
	
	public LoginTest() {
          super("ucb.cs169.project7", LoginScreen.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	// Check if there's a Login button.
	public void testLoginButton() throws Exception {
		solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
		assertTrue(solo.searchText("Use Account"));
	}
		
	// Test logging into a correct account.
	public void testUseCorrectAccount() throws Exception {
		solo.clickOnButton("Use Account");
		solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		
		// Enter in form information
		// Watching this stuff get auto-typed in the emulator is super-cool :D
		solo.enterText(0, "Karl");
		solo.enterText(1, "password");
		
		//Should also be able to do solo.clickOnButton("Save"), but didn't work for some reason.
		//This is find button with index 0 (first button, aka "Save").
		solo.clickOnButton(0);
		
		// Wait for server to respond. I wish there was a better way than this.
		// As a side effect, this test is non-deterministic.
		//solo.sleep(10000);
		
		//solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		// TODO: How do I check for a Toast?
		/*
		// Look at my status, since I should be logged in now.
		solo.clickOnButton("My Status");
		solo.assertCurrentActivity("Expected CharStatus activity", "CharStatus");
		assertTrue(solo.searchText("Karl"));
		*/
	}
	
	//test logging into a wrong account
	public void testUseWrongAccount() throws Exception {
		solo.clickOnButton("Use Account");
		solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		
		//enter wrong password
		solo.enterText(0, "Karl");
		solo.enterText(1, "pp");
		solo.clickOnButton(0);
		solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		solo.sleep(10000);
		assertTrue(solo.searchText("Log in failed."));
		
		//enter nonexisting account
		solo.enterText(0, "iefnvk");
		solo.enterText(1, "lol");
		solo.clickOnButton(0);
		solo.assertCurrentActivity("Expected UseAccount activity", "UseAccount");
		solo.sleep(10000);
		assertTrue(solo.searchText("Log in failed."));
		
	}
	
	// Check if there's a create account button.
	public void testCreateAccountButton() throws Exception {
		solo.assertCurrentActivity("Expected LoginScreen activity", "LoginScreen");
		assertTrue(solo.searchText("Create Account"));
	}
	
	//test navigating create account
	public void testCreateAccount() throws Exception {
		solo.clickOnButton("Create Account");
		solo.assertCurrentActivity("Expected CreateAccount activity", "CreateAccount");
		
		//test select character button
		assertTrue(solo.searchText("Select Character"));
		solo.clickOnButton("Select Character");
		solo.assertCurrentActivity("Expected CharacterSelection activity", "CharacterSelection");
		assertTrue(solo.searchText("Please select a class."));
		
		//test warrior button
		assertTrue(solo.searchText("Warrior"));
		solo.clickOnButton("Warrior");
		assertTrue(solo.searchText("A fierce fighter"));
		
		//test paladin button
		assertTrue(solo.searchText("Paladin"));
		solo.clickOnButton("Paladin");
		assertTrue(solo.searchText("A holy warrior"));
		
		//test assassin button
		assertTrue(solo.searchText("Assassin"));
		solo.clickOnButton("Assassin");
		assertTrue(solo.searchText("Silent, but deadly."));
		
		solo.clickOnButton("Confirm");
		solo.assertCurrentActivity("Expected CreateAccount activity", "CreateAccount");
	}
	
	//test creating a wrong account
	public void testCreateWrongAccount() throws Exception {
		solo.clickOnButton("Create Account");
		solo.assertCurrentActivity("Expected CreateAccount activity", "CreateAccount");
		
		solo.clickOnButton("  Save  ");
		solo.sleep(100);
		assertTrue(solo.searchText("Please enter a login name."));
		
		solo.enterText(0, "k");
		solo.clickOnButton("  Save  ");
		solo.sleep(100);
		assertTrue(solo.searchText("Please enter a password."));
		
		solo.enterText(1, "pp");
		solo.clickOnButton("  Save  ");
		solo.sleep(100);
		assertTrue(solo.searchText("Please select a character class."));
		
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
	
	// XXX: Leaving this here for reference. Not actually part of our code.
	public void pendingTextIsSaved() throws Exception {
		solo.clickOnText("Other");
		solo.clickOnButton("Edit");
		assertTrue(solo.searchText("Edit Window"));
		solo.enterText(1, "Some text for testing purposes");
		solo.clickOnButton("Save");
		assertTrue(solo.searchText("Changes have been made successfully"));
		solo.clickOnButton("Ok");
		assertTrue(solo.searchText("Some text for testing purposes"));
	}

	@Override
	public void tearDown() throws Exception {

		try {
			solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();

	}
}
