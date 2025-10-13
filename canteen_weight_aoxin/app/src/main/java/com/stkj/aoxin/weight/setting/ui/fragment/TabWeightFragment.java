package com.stkj.aoxin.weight.setting.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.utils.PriceUtils;
import com.stkj.aoxin.weight.home.ui.activity.LoginLandActivity;
import com.stkj.aoxin.weight.home.ui.activity.MainActivity;
import com.stkj.aoxin.weight.home.ui.activity.WeightCalibrationActivity;
import com.stkj.aoxin.weight.home.ui.widget.ScaleDashboardView;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.model.BindFragmentSwitchEvent;
import com.stkj.aoxin.weight.pay.model.InitWeightEvent;
import com.stkj.aoxin.weight.weight.Weight;
import com.stkj.aoxin.weight.weight.WeightCallback;
import com.stkj.aoxin.weight.weight.WeightUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * 称重页面
 */
public class TabWeightFragment extends BaseRecyclerFragment {
    
    private static final String TAG = "TabWeightFragment";
    
    // UI Components
    private TextView tvWeightDisplay;
    private TextView tvTareWeight;
    private TextView tvGrossWeight;
    private TextView tvUnitPrice;
    private TextView tvTotalPrice;

    private View view_wending;

    private View view_jingzhong;


    private View view_zero;
    private ScaleDashboardView scaleDashboard;

    private long netWeightTime = 0L;

    // Keypad buttons
    private Button[] keypadButtons = new Button[12];

    // Action buttons
    private Button btnTareAction;
    private Button btnZeroAction;

    private Button btnWeightCalibration;

    // Weight and price data
    private double currentWeight = 0.000;
    private double tareWeight = 0.000;
    private double grossWeight = 0.000;
    private double unitPrice = 0.00;
    private double totalPrice = 0.00;

    // Input state
    private StringBuilder priceInput = new StringBuilder();
    private boolean isInputtingPrice = false;

    // Scale simulation
    private Handler weightHandler = new Handler();
    private Random random = new Random();
    private boolean isStable = false;
    private boolean isSimulatingWeight = false;

    // Format patterns
    private DecimalFormat weightFormat = new DecimalFormat("0.000");
    private DecimalFormat priceFormat = new DecimalFormat("0.00");

    @Override
    protected int getLayoutResId() {
        return com.stkj.aoxin.weight.R.layout.fragment_tab_weight;
    }

    @Override
    protected void initViews(View rootView) {
        initializeViews();
        setupEventListeners();
        //startWeightSimulation();
        initWeightght();
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
//        if (isFirstOnResume) {
//            initData();
//        }
    }


    private void initializeViews() {
        // Weight displays
        tvWeightDisplay = (TextView) findViewById(R.id.tv_weight_display);
        tvTareWeight = (TextView) findViewById(R.id.tv_tare_weight);
        tvGrossWeight = (TextView) findViewById(R.id.tv_gross_weight);
        tvUnitPrice = (TextView) findViewById(R.id.tv_unit_price);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        scaleDashboard = (ScaleDashboardView) findViewById(R.id.scale_dashboard);

        view_wending = (View) findViewById(R.id.view_wending);
        view_jingzhong = (View) findViewById(R.id.view_jingzhong);
        view_zero = (View) findViewById(R.id.view_zero);

        // Keypad buttons
        keypadButtons[0] = (Button) findViewById(R.id.btn_1);
        keypadButtons[1] = (Button) findViewById(R.id.btn_2);
        keypadButtons[2] = (Button) findViewById(R.id.btn_3);
        keypadButtons[3] = (Button) findViewById(R.id.btn_4);
        keypadButtons[4] = (Button) findViewById(R.id.btn_5);
        keypadButtons[5] = (Button) findViewById(R.id.btn_6);
        keypadButtons[6] = (Button) findViewById(R.id.btn_7);
        keypadButtons[7] = (Button) findViewById(R.id.btn_8);
        keypadButtons[8] = (Button) findViewById(R.id.btn_9);
        keypadButtons[9] = (Button) findViewById(R.id.btn_0);
        keypadButtons[10] = (Button) findViewById(R.id.btn_dot);
        keypadButtons[11] = (Button) findViewById(R.id.btn_clear);

        // Action buttons
        btnTareAction = (Button) findViewById(R.id.btn_tare_action);
        btnZeroAction = (Button) findViewById(R.id.btn_zero_action);
        btnWeightCalibration = (Button) findViewById(R.id.btn_weight_calibration);

        // Set initial display values
        updateWeightDisplays();
        updatePriceDisplays();

        // Set initial needle position to zero
        scaleDashboard.setWeight(0f);

        // Start in net weight mode
        isStable = false;
    }

    private void setupEventListeners() {
        // Keypad listeners - automatically start price input when numbers are pressed
        for (int i = 0; i < 10; i++) {
            final int digit = (i == 9) ? 0 : i + 1;
            keypadButtons[i].setOnClickListener(v -> onNumberPressed(String.valueOf(digit)));
        }

        // Dot button
        keypadButtons[10].setOnClickListener(v -> onNumberPressed("."));

        // Clear button
        keypadButtons[11].setOnClickListener(v -> onClearPressed());

        // Enter key listener - finish price input when Enter/Confirm is pressed
        for (Button keypadButton : keypadButtons) {
            keypadButton.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    if (isInputtingPrice) {
                        finishPriceInput();
                        return true;
                    }
                }
                return false;
            });
        }

        // Action button listeners
        btnTareAction.setOnClickListener(v -> performTare());
        btnZeroAction.setOnClickListener(v -> performZero());
        btnWeightCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WeightCalibrationActivity.class));
            }
        });
        // Long press to start price input manually (alternative way)
        tvUnitPrice.setOnLongClickListener(v -> {
            if (!isInputtingPrice) {
//                startPriceInput();
            }
            return true;
        });

        // Click on unit price to start/finish input
        tvUnitPrice.setOnClickListener(v -> {

        });

        // Add test click listener for weight display to test needle positioning
        tvWeightDisplay.setOnClickListener(v -> {
            testNeedlePositions();
        });
    }


    private void startWeightSimulation() {
        if (isSimulatingWeight) return;

        isSimulatingWeight = true;
        weightHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isStable && isSimulatingWeight) {
                    // Simulate weight fluctuation for 0-150kg scale
                    // Generate weights in a more reasonable range for testing alignment
                    double baseWeight = 5.0 + (random.nextDouble() * 50.0); // 5 to 55kg for easier verification
                    double fluctuation = (random.nextDouble() - 0.5) * 2.0; // ±1.0 kg

                    currentWeight = Math.max(0, Math.min(150.0, baseWeight + fluctuation));

                    updateWeightDisplays();
                    updateNeedlePosition();
                    calculateTotalPrice();

                    weightHandler.postDelayed(this, 1000); // Update every 1000ms for easier observation
                }
            }
        }, 200);



    }

    private void initWeightght() {
        WeightUtils.start("/dev/ttyS0", new WeightCallback() {
            @Override
            public void callback(final Weight weight, final String cache) {
                 getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String pattern = "重量:%.3f    皮重:%.3f    稳定:%s    皮重:%s    零位:%s";
//                        if (weight != null) {
//                            String text = String.format(
//                                    pattern,
//                                    weight.netWeight,
//                                    weight.tareWeight,
//                                    weight.stabled ? "是" : "否",
//                                    weight.netFlag ? "是" : "否",
//                                    weight.zeroFlag ? "是" : "否"
//                            );
//                            binding.tvWeight.setText(text);
//                        }


                        if (weight != null && !PriceUtils.formatPrice(weight.netWeight).equals(tvGrossWeight.getText().toString().trim())) {

                            if (System.currentTimeMillis() - netWeightTime < 50) {
                                return  ;
                            }

                            tareWeight = weight.tareWeight;

                            if(weight.tareWeight > 0){
                                tvTareWeight.setText(weight.tareWeight + " kg");
                            } else {
                                tvTareWeight.setText("0.000 kg");
                            }

                            if (weight.zeroFlag){
                                view_zero.setBackgroundResource(R.drawable.status_circle_green);
                            }else {
                                view_zero.setBackgroundResource(R.drawable.status_circle_gray);
                            }

                            if (weight.stabled){
                                view_wending.setBackgroundResource(R.drawable.status_circle_green);
                            }else {
                                view_wending.setBackgroundResource(R.drawable.status_circle_gray);
                            }

                            if(weight.netWeight > 0){

                                netWeightTime = System.currentTimeMillis();




                                Log.d(TAG, "limeWeight  weight.netWeight: "  + weight.netWeight);
                                Log.d(TAG, "limeWeight  weight.tareWeight: "  + weight.tareWeight);
                                Log.d(TAG, "limeWeight  weight.zeroFlag: "  + weight.zeroFlag);

                                currentWeight = weight.netWeight;


                                updateWeightDisplays();
                                updateNeedlePosition();
                                calculateTotalPrice();



                            } else {
                                currentWeight = 0.00;
                                updateWeightDisplays();
                                updateNeedlePosition();
                                calculateTotalPrice();
                            }


                        }else {
//                            btnTakeProductPhoto.setImageResource(R.mipmap.take_photo_default);
//                            tvGrossWeight.setText("0.00");

                        }
                    }
                });

            }
        });
    }

    private void stopWeightSimulation() {
        isSimulatingWeight = false;
        weightHandler.removeCallbacksAndMessages(null);
    }

    private void updateWeightDisplays() {
        // Calculate total weight = net weight + tare weight
        if (currentWeight > 0) {
            grossWeight = currentWeight + tareWeight;
        }else {
            grossWeight = 0.000;
        }

        tvWeightDisplay.setText(weightFormat.format(currentWeight) + " kg");

        tvGrossWeight.setText(weightFormat.format(grossWeight) + " kg");

        // Recalculate total price when weight changes
        calculateTotalPrice();
    }

    private void updatePriceDisplays() {
        // Always display unit price as formatted value, never change the stored unitPrice value
        tvTotalPrice.setText(priceFormat.format(unitPrice));
        tvTotalPrice.setText(priceFormat.format(Double.parseDouble(tvUnitPrice.getText().toString().trim()) *  currentWeight));
    }

    private void updateNeedlePosition() {
        // Update the custom dashboard view with current weight
        scaleDashboard.setWeight((float) currentWeight);
    }

    private void zeroScale() {
        currentWeight = 0.000;
        tareWeight = 0.000;
        grossWeight = 0.000;
        updateWeightDisplays(); // This will also recalculate total price
        updateNeedlePosition();
    }

    private void performTare() {
        // Set tare weight to current net weight (this will make net weight show as 0)
        try {
            WeightUtils.tare();
            ToastUtils.toastMsgSuccess("去皮成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void performZero() {
        try {
            WeightUtils.zero();
            ToastUtils.toastMsgSuccess("置零成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onNumberPressed(String number) {
        if (!isInputtingPrice) {
            // Start price input automatically when any number is pressed
//            startPriceInput();
        }

        if (number.equals(".")) {
            // Handle decimal point input
            String currentInput = priceInput.toString();
            if (currentInput.contains(".")) {
                return; // Only allow one decimal point
            }
            if (currentInput.isEmpty()) {
                priceInput.append("0."); // Add leading zero if decimal is first
            } else {
                priceInput.append(".");
            }
        } else {
            // Handle number input
            String currentInput = priceInput.toString();

            // Check if we already have a decimal point
            if (currentInput.contains(".")) {
                String[] parts = currentInput.split("\\.");
                if (parts.length > 1 && parts[1].length() >= 2) {
                    return; // Already have 2 decimal places, don't allow more
                }
            }

            // Limit total input length to prevent extremely large numbers
            if (priceInput.length() < 10) {
                priceInput.append(number);
            }
        }

        updatePriceInput();
    }

    private void onClearPressed() {
        if (priceInput.length() > 0) {
            priceInput.deleteCharAt(priceInput.length() - 1);
            updatePriceInput();
            calculateTotalPrice();
        }  else {
            // Manual clear - only clear when not in input mode and user explicitly wants to clear
            unitPrice = 0.00;
            tvUnitPrice.setText("0.00");
            updatePriceDisplays();
            calculateTotalPrice();
        }
    }


    private void updatePriceInput() {
        try {
            String inputText = priceInput.toString();
            if (!inputText.isEmpty()) {
                // Validate the input format
                if (isValidPriceInput(inputText)) {
                    // Display the current input exactly as entered during input mode
                    if (inputText.endsWith(".")) {
                        tvUnitPrice.setText(inputText); // Keep the decimal point for user feedback
                    } else {
                        // Show the input as-is during input, don't format yet
                        tvUnitPrice.setText(inputText);
                    }
                } else {
                    // Invalid input, don't update display - keep showing current unitPrice
                    return;
                }
            } else {
                // Empty input - show current unitPrice formatted, don't show 0.00
                tvUnitPrice.setText(priceFormat.format(unitPrice));
            }
        } catch (NumberFormatException e) {
            // Handle invalid input by showing current unitPrice, never 0.00
            tvUnitPrice.setText(priceFormat.format(unitPrice));
        }
    }

    private void finishPriceInput() {
        isInputtingPrice = false;

        try {
            String inputText = priceInput.toString();
            if (!inputText.isEmpty() && isValidPriceInput(inputText)) {
                // Remove trailing decimal point if exists
                if (inputText.endsWith(".")) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                }
                if (!inputText.isEmpty()) {
                    unitPrice = Double.parseDouble(inputText);
                }
                // Don't change unitPrice if input is empty - keep existing value
            }
            // Don't set unitPrice to 0.00 for invalid input - keep existing value
        } catch (NumberFormatException e) {
            // Keep existing unitPrice if parsing fails - don't reset to 0
        }

        // Reset appearance - remove any background colors
        // tvUnitPrice.setBackgroundColor(0x00000000); // Transparent - REMOVED
        // tvUnitPrice.setTextColor(0xFF000000); // Black text - REMOVED

        // Always display the fixed unit price with proper formatting
        updatePriceDisplays();
        calculateTotalPrice();

        if (unitPrice > 0) {
            Toast.makeText(getActivity(), "单价已设置: " + priceFormat.format(unitPrice) + "/kg", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "输入取消", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateTotalPrice() {
        // Only calculate total price if net weight is >= 0
        if (currentWeight >= 0) {
            totalPrice = currentWeight * unitPrice;
        } else {
            totalPrice = 0.00;
        }
        updatePriceDisplays();
    }

    /**
     * Validates if the price input is in correct format
     * Allows integers and floats with maximum 2 decimal places
     * @param input The input string to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPriceInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // Check for valid decimal format
        if (input.equals(".") || input.startsWith(".") && input.length() == 1) {
            return false; // Just a decimal point is not valid
        }

        // Allow trailing decimal point for user input feedback
        if (input.endsWith(".")) {
            String withoutDot = input.substring(0, input.length() - 1);
            if (withoutDot.isEmpty()) {
                return false;
            }
            try {
                Double.parseDouble(withoutDot);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // Check decimal places
        if (input.contains(".")) {
            String[] parts = input.split("\\.");
            if (parts.length > 2) {
                return false; // Multiple decimal points
            }
            if (parts.length == 2 && parts[1].length() > 2) {
                return false; // More than 2 decimal places
            }
        }

        try {
            double value = Double.parseDouble(input);
            return value >= 0 && value < 100000; // Reasonable price range
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void testNeedlePositions() {
        // Test key positions: 0kg, 75kg (middle), 150kg (max)
//        Handler testHandler = new Handler();
//        double[] testWeights = {0.0, 37.5, 75.0, 112.5, 150.0};
//
//        for (int i = 0; i < testWeights.length; i++) {
//            final double testWeight = testWeights[i];
//            testHandler.postDelayed(() -> {
//                currentWeight = testWeight;
//                updateWeightDisplays();
//                updateNeedlePosition();
//                Toast.makeText(getActivity(), "测试重量: " + weightFormat.format(testWeight) + "kg", Toast.LENGTH_SHORT).show();
//            }, i * 2000); // 2 second intervals
//        }



    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitWeightEvent(InitWeightEvent eventBus) {
        WeightUtils.stop();
        initWeightght();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWeightSimulation();
    }
}
