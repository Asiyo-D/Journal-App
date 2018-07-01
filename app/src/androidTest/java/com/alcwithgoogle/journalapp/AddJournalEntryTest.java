package com.alcwithgoogle.journalapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.android.gms.common.internal.Preconditions.checkArgument;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by root on 6/30/18 for LoqourSys
 */

@RunWith(AndroidJUnit4.class)
public class AddJournalEntryTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void saveClickedAndJournalEntry() {
        String journalEntryTitle = "First day at the zoo";
        String journalBodyText = "I went to the zoo and saw a monkey";

        onView(withId(R.id.fab))
                .perform(click());

        onView(withId(R.id.txt_journal_title))
                .perform(typeText(journalEntryTitle), closeSoftKeyboard());
        onView(withId(R.id.txt_journal_text))
                .perform(typeText(journalBodyText), closeSoftKeyboard());

        onView(withId(R.id.btn_save))
                .perform(click());

        onView(withId(R.id.recycler))
                .perform(scrollTo(hasDescendant(withText(journalBodyText))));

        onView(withItemText(journalBodyText)).check(matches(isDisplayed()));

    }

    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA RV with text " + itemText);
            }
        };
    }
}
