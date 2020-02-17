package edu.csc4360.thescotchdatabase;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

public class CheckBoxTriStates extends android.support.v7.widget.AppCompatCheckBox {
    static private final int UNKNOWN = -1;
    static private final int UNCHECKED = 0;
    static private final int CHECKED = 1;
    private int state;
    //private CheckBoxTriStates.CheckBoxTriStatesListener listener;

    public CheckBoxTriStates(Context context) {
        super(context);
        init();
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        state = UNKNOWN;
        updateBtn();

        setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            // checkbox status is changed from uncheck to checked.
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                switch (state)
                {
                    case UNKNOWN:
                        state = UNCHECKED;
                        break;
                    case UNCHECKED:
                        state = CHECKED;
                        break;
                    case CHECKED:
                        state = UNKNOWN;
                        break;
                }
               // listener.onCheckBoxTriStatesSent(state);
                updateBtn();
            }
        });

    }

    private void updateBtn()
    {
        int btnDrawable = R.drawable.ic_star_half_yellow_24dp;
        switch (state)
        {
            case UNKNOWN:
                btnDrawable = R.drawable.ic_star_half_yellow_24dp;
                break;
            case UNCHECKED:
                btnDrawable = R.drawable.ic_star_border_black_24dp;
                break;
            case CHECKED:
                btnDrawable = R.drawable.ic_star_yellow_24dp;
                break;
        }
        setButtonDrawable(btnDrawable);

    }
    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
        updateBtn();
    }

/*    public interface CheckBoxTriStatesListener {
        void onCheckBoxTriStatesSent(int state);
    }*/
}
