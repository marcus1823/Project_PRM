package com.example.menaccessoriesshop.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.menaccessoriesshop.data.model.User;
public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    public void setUser(User user) {
        userLiveData.setValue(user);
    }
    public LiveData<User> getUser() {
        return userLiveData;
    }
}





