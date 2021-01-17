package com.netsurf.quarterpillars.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.netsurf.quarterpillars.R;
import com.netsurf.quarterpillars.api.QuarterpillarsApi;
import com.netsurf.quarterpillars.databinding.ActivityRegistrationBinding;
import com.netsurf.quarterpillars.response.Response;
import com.netsurf.quarterpillars.utils.Utils;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    private CompositeDisposable compositeDisposable;
    private QuarterpillarsApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar();

        compositeDisposable = new CompositeDisposable();
        mService = Utils.getApi();

        binding.edtName.addTextChangedListener(new MyTextWatcher(binding.edtName));
        binding.edtMobileNumber.addTextChangedListener(new MyTextWatcher(binding.edtMobileNumber));
        binding.edtEmail.addTextChangedListener(new MyTextWatcher(binding.edtEmail));
        binding.edtPassword.addTextChangedListener(new MyTextWatcher(binding.edtPassword));

        binding.btnSignUp.setOnClickListener(view1 -> signUp());

    }

    private void signUp() {
        if (!validateName()) {
            return;
        }
        if (!validateMobileNumber()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        try {
            compositeDisposable.add(mService.registration(
                    binding.edtName.getText().toString(),
                    binding.edtMobileNumber.getText().toString(),
                    binding.edtEmail.getText().toString(),
                    binding.edtPassword.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Response>() {
                        @Override
                        public void onSuccess(Response response) {
                            Snackbar.make(binding.getRoot(),response.getMessage(),Snackbar.LENGTH_LONG)
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(binding.getRoot(),"API_ERROR : "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtName:
                    validateName();
                    break;
                case R.id.edtMobileNumber:
                    validateMobileNumber();
                    break;
                case R.id.edtEmail:
                    validateEmail();
                    break;
                case R.id.edtPassword:
                    validatePassword();
                    break;
            }
        }
    }

    private boolean validateName() {
        if (binding.edtName.getText().toString().trim().isEmpty()) {
            binding.txtName.setError(getString(R.string.enter_name));
            requestFocus(binding.edtName);
            return false;
        } else {
            binding.txtName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobileNumber() {
        if (binding.edtMobileNumber.getText().toString().trim().isEmpty()
            || !Patterns.PHONE.matcher(Objects.requireNonNull(binding.edtMobileNumber.getText()).toString().trim()).matches()
            || binding.edtMobileNumber.getText().toString().trim().length() < 10 ) {
            binding.txtMobNumber.setError(getString(R.string.enter_mobile));
            requestFocus(binding.edtMobileNumber);
            return false;
        } else {
            binding.txtMobNumber.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (binding.edtPassword.getText().toString().trim().isEmpty()) {
            binding.txtPassword.setError(getString(R.string.valid_password));
            requestFocus(binding.edtPassword);
            return false;
        } else {
            binding.txtPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        String email = binding.edtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            binding.txtEmail.setError(getString(R.string.valid_email));
            requestFocus(binding.edtEmail);
            return false;
        } else {
            binding.txtEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}