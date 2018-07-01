package com.alcwithgoogle.journalapp;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by root on 6/30/18 for LoqourSys
 */
@RunWith(AndroidJUnit4.class)
public class NewJournalScreenTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void newJournalEntry() {
        onView(withId(R.id.fab))
                .perform(click());
        onView(withId(R.id.til_title))
                .check(matches(isDisplayed()));

    }
}
