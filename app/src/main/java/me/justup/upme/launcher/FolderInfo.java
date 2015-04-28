package me.justup.upme.launcher;

import android.content.ContentValues;

import java.util.ArrayList;

public class FolderInfo extends ItemInfo {

    public boolean opened;

    /**
     * The apps and shortcuts
     */
    public ArrayList<ShortcutInfo> contents = new ArrayList<ShortcutInfo>();

    public ArrayList<FolderListener> listeners = new ArrayList<FolderListener>();

    public FolderInfo() {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;
    }

    public void add(ShortcutInfo item) {
        contents.add(item);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onAdd(item);
        }

        itemsChanged();
    }

    public void remove(ShortcutInfo item) {
        contents.remove(item);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onRemove(item);
        }

        itemsChanged();
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTitleChanged(title);
        }
    }

    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.TITLE, title.toString());
    }

    public void addListener(FolderListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FolderListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void itemsChanged() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onItemsChanged();
        }
    }

    @Override
    public void unbind() {
        super.unbind();
        listeners.clear();
    }

    public interface FolderListener {
        public void onAdd(ShortcutInfo item);

        public void onRemove(ShortcutInfo item);

        public void onTitleChanged(CharSequence title);

        public void onItemsChanged();
    }
}
