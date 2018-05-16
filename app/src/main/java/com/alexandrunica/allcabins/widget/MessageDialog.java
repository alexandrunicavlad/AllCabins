package com.alexandrunica.allcabins.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class MessageDialog extends DialogFragment {
    private static final String MESSAGE_ARG = "MESSAGE_ARG";
    private static final String BUTTON_TEXT_ARG = "BUTTON_TEXT_ARG";

    protected String message;
    private String buttonText;


    public static  MessageDialog newInstance(String buttonText,String message, Context context) {
        MessageDialog messageDialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE_ARG, message);
        args.putString(BUTTON_TEXT_ARG, buttonText);

        messageDialog.setArguments(args);
        return messageDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        message = args.getString(MESSAGE_ARG);
        buttonText = args.getString(BUTTON_TEXT_ARG);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_message, null);

        Button ok = (Button) view.findViewById(R.id.message_ok);
        TextView messageText = (TextView) view.findViewById(R.id.message_text);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        messageText.setText(message);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if(buttonText!= null && !buttonText.isEmpty())
            ok.setText(buttonText);

        return dialog;
    }


}
