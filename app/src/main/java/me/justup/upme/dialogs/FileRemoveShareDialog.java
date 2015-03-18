package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class FileRemoveShareDialog extends DialogFragment {
    private static final String TAG = makeLogTag(FileRemoveShareDialog.class);

    public static final String FILE_REMOVE_SHARE_DIALOG = "file_remove_share_dialog";

    private LayoutInflater mLayoutInflater;
    // private LinearLayout mUserShareLayout;


    public static FileRemoveShareDialog newInstance() {
        return new FileRemoveShareDialog();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_remove_file_share, null);

        // mUserShareLayout = (LinearLayout) dialogView.findViewById(R.id.user_share_items_layout);

        // ApiWrapper.query(new GetMailContactQuery(), new GetContactList());

        builder.setView(dialogView).setTitle(R.string.you_contacts)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // to FileExplorerService for query
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
