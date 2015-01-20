package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import me.justup.upme.R;
import me.justup.upme.utils.AnimateButtonClose;

public class MailMessagesFragment extends Fragment {

    private static final String ARG_MAIL_MESSAGES = "mail_messages";
    private Button mMailMessageCloseButton;
    private Button mStaplebutton;
    private RelativeLayout mAddFileContainer;

    public static MailMessagesFragment newInstance() {
        MailMessagesFragment fragment = new MailMessagesFragment();
        Bundle args = new Bundle();
        //args.put(ARG_MAIL_MESSAGES,);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_messages, container, false);
        mAddFileContainer = (RelativeLayout) view.findViewById(R.id.mail_messages_add_file_container);
        mMailMessageCloseButton = (Button) view.findViewById(R.id.mail_messages_close_button);
        mMailMessageCloseButton.setVisibility(View.INVISIBLE);
        mMailMessageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(MailMessagesFragment.this).commit();
            }
        });
        mStaplebutton = (Button) view.findViewById(R.id.mail_messages_staple_button);
        mStaplebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddFileContainer.getVisibility() == View.GONE) {
                    mAddFileContainer.setVisibility(View.VISIBLE);
                } else {
                    mAddFileContainer.setVisibility(View.GONE);
                }
            }
        });

        AnimateButtonClose.animateButtonClose(mMailMessageCloseButton);
        return view;
    }

}
