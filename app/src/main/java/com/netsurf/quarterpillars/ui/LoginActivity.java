package com.netsurf.quarterpillars.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.netsurf.quarterpillars.R;
import com.netsurf.quarterpillars.api.QuarterpillarsApi;
import com.netsurf.quarterpillars.databinding.ActivityLoginBinding;
import com.netsurf.quarterpillars.response.Response;
import com.netsurf.quarterpillars.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CompositeDisposable compositeDisposable;
    private QuarterpillarsApi mService;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1001;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        compositeDisposable = new CompositeDisposable();
        mService = Utils.getApi();

        mAuth = FirebaseAuth.getInstance();

        createRequest();

        binding.edtEmail.addTextChangedListener(new MyTextWatcher(binding.edtEmail));
        binding.edtPassword.addTextChangedListener(new MyTextWatcher(binding.edtPassword));

        binding.btnLogin.setOnClickListener(view1 -> loginWithEmail());

        binding.btnSignUp.setOnClickListener(view12 -> startActivity(new Intent(this,RegistrationActivity.class)));

        binding.btnGoogleLayout.setOnClickListener(view13 -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginWithEmail() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }

        try {
            compositeDisposable.add(mService.login(
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
                case R.id.edtEmail:
                    validateEmail();
                    break;
                case R.id.edtPassword:
                    validatePassword();
                    break;
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Throwable e) {
                Log.e("SIGN_IN", new StringBuilder().append("Error : ").append(e.getMessage()).toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Snackbar.make(binding.getRoot(), new StringBuilder().append("Welcome ")
                                    .append(user.getDisplayName()).toString(),Snackbar.LENGTH_LONG)
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        } else {
                            Snackbar.make(binding.getRoot(),"Sorry authentication failed, try later!",Snackbar.LENGTH_LONG)
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}