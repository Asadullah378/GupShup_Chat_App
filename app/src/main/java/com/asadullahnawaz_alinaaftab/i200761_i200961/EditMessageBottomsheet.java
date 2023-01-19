package com.asadullahnawaz_alinaaftab.i200761_i200961;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.asadullahnawaz_alinaaftab.i200761_i200961.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditMessageBottomsheet extends BottomSheetDialogFragment {

    Button update_msg;
    TextView new_msg;
    LinkMenuInterface callback;
    int i;
    String s;
    String r;

    public EditMessageBottomsheet(LinkMenuInterface callback, int i, String s, String r) {

        this.callback = callback;
        this.s = s;
        this.r = r;
        this.i = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.edit_message_bottomsheet,
                container, false);

        update_msg = v.findViewById(R.id.edit_msg_btn);
        new_msg = v.findViewById(R.id.edit_message);


        update_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = new_msg.getText().toString();

                if(msg.isEmpty()){
                    Toast.makeText(getContext(), "Message Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }

                else{
                    callback.setNewMessage(msg,i,s,r);
                    dismiss();
                }



            }
        });





        return v;
    }


}
