package com.asadullahnawaz_alinaaftab.i200761_i200961;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.asadullahnawaz_alinaaftab.i200761_i200961.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddAttachmentBottomsheet extends BottomSheetDialogFragment {

    CardView photo;
    CardView gallery;
    AddAttachmentInterface callback;
    public AddAttachmentBottomsheet(AddAttachmentInterface callback) {

        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.add_attachment_bottomsheet,
                container, false);

        photo = v.findViewById(R.id.photo);
        gallery = v.findViewById(R.id.gallery);


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.selectAttachment("photo");
                dismiss();

            }
        });



        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.selectAttachment("gallery");

                dismiss();
            }
        });



        return v;
    }


}
