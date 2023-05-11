package de.zagro.shitchat.ui.registration.direct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DirectViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DirectViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is direct fragment!");
    }

    public LiveData<String> getText() { return mText; }
}
