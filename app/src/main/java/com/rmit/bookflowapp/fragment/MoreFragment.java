package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentMessageListBinding;
import com.rmit.bookflowapp.databinding.FragmentMoreBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.repository.UserRepository;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class MoreFragment extends Fragment {
    private String TAG = "MoreFragment";
    private FragmentMoreBinding binding;
    private MainActivity activity;
    private User user;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);
        activity.setProgressVisibility(true);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        UserRepository.getInstance().getUserById(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                user = task.getResult();
                activity.setProgressVisibility(false);
                handlePurchaseButton();
            }
        });
        binding.userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("USER_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
            }
        });
        binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "follow", Toast.LENGTH_SHORT).show();
            }
        });
        binding.aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigate(R.id.aboutFragment);
            }
        });
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                activity.navController.navigate(R.id.action_moreFragment_to_authenticationFragment);
            }
        });
        return binding.getRoot();
    }
    private void handlePurchaseButton(){
        if (user.isVerified()){
            binding.purchaseBtn.setText("You've already purchased a verified account!");
            binding.purchaseBtn.setClickable(false);
            binding.purchaseBtn.setTextColor(Color.parseColor("#207a39"));
        } else {
            binding.purchaseBtn.setClickable(true);
            binding.purchaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDetails();
                }
            });
        }
    }
    private void getDetails(){
        activity.setProgressVisibility(true);
        Fuel.INSTANCE.post("https://us-central1-striking-water-408603.cloudfunctions.net/createPaymentIntent", null).responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    JSONObject result = new JSONObject(s);
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            result.getString("customer"),
                            result.getString("ephemeralKey")
                    );
                    paymentIntentClientSecret = result.getString("paymentIntent");
                    PaymentConfiguration.init(getContext(), result.getString("publishableKey"));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showStripePaymentSheet();
                        }
                    });



                } catch (JSONException e){
                    Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(@NonNull FuelError fuelError) {

            }
        });
    }

    private void showStripePaymentSheet(){
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("coDeR")
                .customer(customerConfig)
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult){
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(getContext(), ((PaymentSheetResult.Failed) paymentSheetResult).getError().toString() , Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            FirebaseFirestore.getInstance().document("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).update("verified", true);
            Toast.makeText(getContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
            binding.purchaseBtn.setText("You've already purchased a verified account!");
            binding.purchaseBtn.setClickable(false);
            binding.purchaseBtn.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        }
        activity.setProgressVisibility(false);
    }
}