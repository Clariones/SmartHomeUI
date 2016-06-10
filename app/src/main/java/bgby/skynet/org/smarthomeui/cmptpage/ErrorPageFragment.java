package bgby.skynet.org.smarthomeui.cmptpage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import bgby.skynet.org.smarthomeui.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorPageFragment extends Fragment {

    protected String errMessage;
    protected ImageButton btnMessage;

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public ErrorPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_error_page, container, false);
        btnMessage = (ImageButton) view.findViewById(R.id.errfrgmt_btn_message);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getErrMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}
