package me.justup.upme.view.dashboard;

public interface ScrollingStrategy {
    boolean performScrolling(final int x, final int y, final CoolDragAndDropGridView view);
}
