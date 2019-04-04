package com.upv.dadm.valenparking.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.upv.dadm.valenparking.R;

public class myDialogFragment extends AppCompatDialogFragment {

    public myDialogFragment() {};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(android.R.drawable.stat_sys_warning);
        String click =  getArguments().getString("click");
        Log.v("hola", click);
        if(click.equals("one")){
            builder.setTitle(getString(R.string.favourite_dialog_delete_title));
        }else{
            builder.setTitle(getString(R.string.favourite_dialog_delete_all_title));
        }
        builder.setMessage(msg);

        if(click.equals("one")) {
            builder.setPositiveButton(getString(R.string.favourite_dialog_option_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);

                }
            });
        }else{
            builder.setPositiveButton(getString(R.string.favourite_dialog_option_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), 1, null);

                }
            });
        }
        builder.setNegativeButton(getString(R.string.favourite_dialog_option_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), 2, null);
            }
        });
        builder.create().show();
        return super.onCreateDialog(savedInstanceState);
    }

    public static myDialogFragment getInstance(String message){
        myDialogFragment myDialogFragment = new myDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }
}