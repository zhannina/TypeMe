package com.example.zsarsenbayev.typeme.typingActivity;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by zsarsenbayev on 11/14/17.
 */

public abstract class EditTextListener implements TextWatcher {

    private String _before;
    private String _old;
    private String _new;
    private String _after;

    private boolean _ignore = false;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        _before = charSequence.subSequence(0, start).toString();
        _old = charSequence.subSequence(start, start+count).toString();
        _after = charSequence.subSequence(start+count, charSequence.length()).toString();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        _new = charSequence.subSequence(start, start+count).toString();
    }

    @Override
    public void afterTextChanged(Editable sequence) {
        if (_ignore) {
            return;
        }
        onTextChanged(_before, _old, _new, _after);
    }

    protected abstract void onTextChanged(String before, String old, String aNew, String after);

    /**
     * Call this method when you start to update the text view, so it stops listening to it and then prevent an infinite loop.
     * @see #endUpdates()
     */
    protected void startUpdates(){
        _ignore = true;
    }

    /**
     * Call this method when you finished to update the text view in order to restart to listen to it.
     * @see #startUpdates()
     */
    protected void endUpdates(){
        _ignore = false;
    }
}
