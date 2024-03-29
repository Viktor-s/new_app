package me.justup.upme.view.dashboard;

import android.widget.ScrollView;

public class SimpleScrollingStrategy implements ScrollingStrategy {

    private ScrollView mScrollViewContainer = null;

    public SimpleScrollingStrategy(ScrollView scrollViewContainer) {
        mScrollViewContainer = scrollViewContainer;
    }

    @Override
    public boolean performScrolling(final int x, final int y, final CoolDragAndDropGridView view) {

        if (mScrollViewContainer != null) {

            int scrollY = mScrollViewContainer.getScrollY();
            int delta = scrollY - view.getTop();
            int maxDelta = Math.max(delta, 0);

            int dy = y - delta;

            int height = view.getHeight();
            int containerHeight = mScrollViewContainer.getHeight();

            int topThreesHold = containerHeight / 10;
            int bottomThreesHold = 9 * containerHeight / 10;

            if ((dy < topThreesHold) && (maxDelta > 0)) {
                mScrollViewContainer.scrollBy(0, -topThreesHold / 8);

                return true;

            } else if ((dy > bottomThreesHold) && ((delta + containerHeight) < height)) {
                mScrollViewContainer.scrollBy(0, topThreesHold / 8);

                return true;
            }
        }

        return false;
    }

}
