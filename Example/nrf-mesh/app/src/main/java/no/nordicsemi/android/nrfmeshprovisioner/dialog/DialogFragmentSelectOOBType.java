/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmeshprovisioner.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.meshprovisioner.provisionerstates.ProvisioningCapabilities;
import no.nordicsemi.android.meshprovisioner.utils.AuthenticationOOBMethods;
import no.nordicsemi.android.meshprovisioner.utils.InputOOBAction;
import no.nordicsemi.android.meshprovisioner.utils.OutputOOBAction;
import no.nordicsemi.android.meshprovisioner.utils.StaticOOBType;
import no.nordicsemi.android.nrfmeshprovisioner.R;
import no.nordicsemi.android.nrfmeshprovisioner.adapter.AuthenticationOOBMethodsAdapter;
import no.nordicsemi.android.nrfmeshprovisioner.utils.HexKeyListener;

public class DialogFragmentSelectOOBType extends DialogFragment {

    private static final String CAPABILITIES = "CAPABILITIES";
    //UI Bindings
    @BindView(R.id.oob_types)
    Spinner oobTypesSpinner;
    @BindView(R.id.static_oob_input_layout)
    TextInputLayout staticOobInputLayout;
    @BindView(R.id.static_oob_input)
    TextInputEditText staticOobTextInput;
    @BindView(R.id.output_oob_container)
    LinearLayout containerOutputOOB;
    @BindView(R.id.input_oob_container)
    LinearLayout containerInputOOB;
    @BindView(R.id.radio_group_output_oob)
    RadioGroup rgOutputOob;
    @BindView(R.id.radio_group_input_oob)
    RadioGroup rgInputOob;

    private ProvisioningCapabilities capabilities;
    private List<AuthenticationOOBMethods> availableOOBTypes = new ArrayList<>();
    private AuthenticationOOBMethodsAdapter authenticationOobMethodsAdapter;

    public interface DialogFragmentSelectOOBTypeListener {

        void onNoOOBSelected();

        void onStaticOOBSelected(final StaticOOBType staticOOBType);

        void onOutputOOBActionSelected(final OutputOOBAction outputOOBType);

        void onInputOOBActionSelected(final InputOOBAction inputOOBType);

        void onOOBSelectionCanceled();
    }

    public static DialogFragmentSelectOOBType newInstance(final ProvisioningCapabilities capabilities) {
        final DialogFragmentSelectOOBType fragment = new DialogFragmentSelectOOBType();
        final Bundle args = new Bundle();
        args.putParcelable(CAPABILITIES, capabilities);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            capabilities = getArguments().getParcelable(CAPABILITIES);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_select_oob_type, null);

        //Bind ui
        ButterKnife.bind(this, rootView);
        populateOOBTypes();
        authenticationOobMethodsAdapter = new AuthenticationOOBMethodsAdapter(requireContext(), availableOOBTypes);
        oobTypesSpinner.setAdapter(authenticationOobMethodsAdapter);

        oobTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updateOOBUI(position);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        final KeyListener hexKeyListener = new HexKeyListener();
        staticOobTextInput.setKeyListener(hexKeyListener);
        staticOobTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    staticOobInputLayout.setError(getString(R.string.error_empty_group_address));
                } else {
                    staticOobInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });

        rgOutputOob.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_blink:
                    break;
                case R.id.radio_beep:
                    break;
                case R.id.radio_vibrate:
                    break;
                case R.id.radio_output_numeric:
                    break;
                case R.id.radio_output_alpha_numeric:
                    break;
            }
        });


        rgInputOob.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_push:
                    break;
                case R.id.radio_twist:
                    break;
                case R.id.radio_input_numeric:
                    break;
                case R.id.radio_input_alpha_numeric:
                    break;
            }
        });

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext()).
                setView(rootView).
                setIcon(R.drawable.ic_oob_lock_outline).
                setTitle(R.string.title_select_oob).
                setPositiveButton(R.string.ok, (dialog, which) -> {
                    final AuthenticationOOBMethods type = (AuthenticationOOBMethods) oobTypesSpinner.getSelectedItem();
                    switch (type) {
                        case NO_OOB_AUTHENTICATION:
                            ((DialogFragmentSelectOOBTypeListener) requireContext()).onNoOOBSelected();
                            break;
                        case STATIC_OOB_AUTHENTICATION:
                            ((DialogFragmentSelectOOBTypeListener) requireContext()).onStaticOOBSelected(StaticOOBType.STATIC_OOB_AVAILABLE);
                            break;
                        case OUTPUT_OOB_AUTHENTICATION:
                            ((DialogFragmentSelectOOBTypeListener) requireContext()).onOutputOOBActionSelected(getSelectedOutputOOBType());
                            break;
                        case INPUT_OOB_AUTHENTICATION:
                            ((DialogFragmentSelectOOBTypeListener) requireContext()).onInputOOBActionSelected(getSelectedInputOOBType());
                            break;
                    }
                }).
                setNegativeButton(R.string.cancel, (dialog, which) -> ((DialogFragmentSelectOOBTypeListener) requireContext()).onOOBSelectionCanceled());


        return alertDialogBuilder.show();
    }

    private void populateOOBTypes() {
        availableOOBTypes.clear();
        availableOOBTypes.add(AuthenticationOOBMethods.NO_OOB_AUTHENTICATION);

        if (capabilities.isStaticOOBInformationAvailable()) {
            availableOOBTypes.add(AuthenticationOOBMethods.STATIC_OOB_AUTHENTICATION);
        }

        if (!capabilities.getSupportedOutputOOBActions().isEmpty()) {
            availableOOBTypes.add(AuthenticationOOBMethods.OUTPUT_OOB_AUTHENTICATION);
        }

        if (!capabilities.getSupportedInputOOBActions().isEmpty()) {
            availableOOBTypes.add(AuthenticationOOBMethods.INPUT_OOB_AUTHENTICATION);
        }
    }

    private void updateOOBUI(final int position) {
        final AuthenticationOOBMethods oobType = authenticationOobMethodsAdapter.getItem(position);
        switch (oobType) {
            case NO_OOB_AUTHENTICATION:
                staticOobInputLayout.setVisibility(View.GONE);
                containerOutputOOB.setVisibility(View.GONE);
                containerInputOOB.setVisibility(View.GONE);
                break;
            case STATIC_OOB_AUTHENTICATION:
                staticOobInputLayout.setVisibility(View.VISIBLE);
                containerOutputOOB.setVisibility(View.GONE);
                containerInputOOB.setVisibility(View.GONE);
                break;
            case OUTPUT_OOB_AUTHENTICATION:
                staticOobInputLayout.setVisibility(View.GONE);
                containerOutputOOB.setVisibility(View.VISIBLE);
                containerInputOOB.setVisibility(View.GONE);
                break;
            case INPUT_OOB_AUTHENTICATION:
                staticOobInputLayout.setVisibility(View.GONE);
                containerOutputOOB.setVisibility(View.GONE);
                containerInputOOB.setVisibility(View.VISIBLE);
                break;
        }
    }

    private OutputOOBAction getSelectedOutputOOBType() {
        final int id = rgOutputOob.getCheckedRadioButtonId();
        switch (id) {
            case R.id.radio_blink:
                return OutputOOBAction.BLINK;
            case R.id.radio_beep:
                return OutputOOBAction.BEEP;
            case R.id.radio_vibrate:
                return OutputOOBAction.VIBRATE;
            case R.id.radio_output_numeric:
                return OutputOOBAction.OUTPUT_NUMERIC;
            case R.id.radio_output_alpha_numeric:
                return OutputOOBAction.OUTPUT_ALPHA_NUMERIC;
        }
        return null;
    }

    private InputOOBAction getSelectedInputOOBType() {
        final int id = rgInputOob.getCheckedRadioButtonId();
        switch (id) {
            case R.id.radio_push:
                return InputOOBAction.PUSH;
            case R.id.radio_twist:
                return InputOOBAction.TWIST;
            case R.id.radio_input_numeric:
                return InputOOBAction.INPUT_NUMERIC;
            case R.id.radio_input_alpha_numeric:
                return InputOOBAction.INPUT_ALPHA_NUMERIC;
        }
        return null;
    }
}
