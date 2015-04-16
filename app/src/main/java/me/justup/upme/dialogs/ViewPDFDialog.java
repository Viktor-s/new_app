package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

import me.justup.upme.R;


public class ViewPDFDialog extends DialogFragment implements OnPageChangeListener, OnLoadCompleteListener {
    public static final String VIEW_PDF_DIALOG = "view_pdf_dialog";
    private static final String VIEW_PDF_FILE_NAME = "view_pdf_file_name";
    private static final String VIEW_PDF_FILE_PATH = "view_pdf_file_path";

    private TextView mPageLoaded;
    private TextView mAllPages;
    private TextView mNumberPage;


    public static ViewPDFDialog newInstance(final String fileName, final String filePath) {
        Bundle args = new Bundle();
        args.putString(VIEW_PDF_FILE_NAME, fileName);
        args.putString(VIEW_PDF_FILE_PATH, filePath);

        ViewPDFDialog fragment = new ViewPDFDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String fileName = (String) getArguments().getSerializable(VIEW_PDF_FILE_NAME);
        String filePath = (String) getArguments().getSerializable(VIEW_PDF_FILE_PATH);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_pdf, null);

        final PDFView mPdfPanel = (PDFView) dialogView.findViewById(R.id.dialog_pdf_panel);

        mNumberPage = (TextView) dialogView.findViewById(R.id.pdf_number_page_textView);
        mPageLoaded = (TextView) dialogView.findViewById(R.id.dialog_pdf_file_loaded);
        mAllPages = (TextView) dialogView.findViewById(R.id.dialog_pdf_file_pages);

        mPdfPanel.fromFile(new File(filePath))
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onLoad(this)
                .onPageChange(this)
                .load();

        builder.setView(dialogView).setTitle(fileName).setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mPdfPanel.recycle();
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void loadComplete(int i) {
        mPageLoaded.setVisibility(View.VISIBLE);
        mAllPages.setText(i + " стр.");
    }

    @Override
    public void onPageChanged(int i, int i2) {
        mNumberPage.setText(String.valueOf(i));
    }

}
