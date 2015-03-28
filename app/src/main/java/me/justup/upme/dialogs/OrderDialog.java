package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import me.justup.upme.R;


public class OrderDialog extends DialogFragment {
    public static final String ORDER_DIALOG = "order_dialog";
    private static final String ORDER_DIALOG_HTML_FORM = "order_dialog_html_form";


    public static OrderDialog newInstance(final String htmlString) {
        Bundle args = new Bundle();
        args.putString(ORDER_DIALOG_HTML_FORM, htmlString);

        OrderDialog fragment = new OrderDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String htmlString = (String) getArguments().getSerializable(ORDER_DIALOG_HTML_FORM);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_order, null);

        setCancelable(false);

        TextView mOrderForm = (TextView) dialogView.findViewById(R.id.order_string_form_textView);
        mOrderForm.setText(Html.fromHtml(htmlString));

        builder.setView(dialogView).setTitle("Ордер").setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}
