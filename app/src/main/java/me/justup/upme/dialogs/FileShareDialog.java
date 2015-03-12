package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import me.justup.upme.R;


public class FileShareDialog extends DialogFragment {
    public static final String FILE_SHARE_DIALOG = "file_share_dialog";

    private LinearLayout mUserShareLayout;


    public static FileShareDialog newInstance() {
        return new FileShareDialog();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_file_share, null);

        Button mShareForAll = (Button) dialogView.findViewById(R.id.share_for_all_button);
        mUserShareLayout = (LinearLayout) dialogView.findViewById(R.id.user_share_items_layout);

        builder.setView(dialogView).setTitle(R.string.you_contacts)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
