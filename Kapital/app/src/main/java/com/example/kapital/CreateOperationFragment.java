package com.example.kapital;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateOperationFragment extends Fragment {
    private AppCompatButton createButton;
    private EditText sumET;
    private RadioGroup category, operationType;
    private RadioButton food, fun, other, expense, income;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_operation, container, false);

        sumET = view.findViewById(R.id.sum);
        category = view.findViewById(R.id.category);
        food = view.findViewById(R.id.food);
        fun = view.findViewById(R.id.fun);
        other = view.findViewById(R.id.other);
        createButton = view.findViewById(R.id.create_operation_button);

        operationType = view.findViewById(R.id.operation_type);
        expense = view.findViewById(R.id.expense);
        income = view.findViewById(R.id.income);

        mAuth = FirebaseAuth.getInstance();

        // Установка слушателя для изменения видимости категории
        operationType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.income) {
                    category.setVisibility(View.GONE);
                } else {
                    category.setVisibility(View.VISIBLE);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOperation();
            }
        });

        return view;
    }

    private void createOperation() {
        double sum = Double.parseDouble(sumET.getText().toString());
        String category = null;

        if (expense.isChecked()) {
            if (food.isChecked()) {
                category = "Еда";
            } else if (fun.isChecked()) {
                category = "Развлечения";
            } else if (other.isChecked()) {
                category = "Другое";
            }
        }

        String type = expense.isChecked() ? "expense" : "income";
        String userId = mAuth.getCurrentUser().getUid();
        Date date = new Date();

        Map<String, Object> operation = new HashMap<>();
        operation.put("sum", sum);
        operation.put("category", category);
        operation.put("userId", userId);
        operation.put("date", date);
        operation.put("type", type);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("operations")
                .add(operation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(requireContext(), "Операция добавлена!", Toast.LENGTH_SHORT).show();
                        sumET.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error", e);
                    }
                });
    }
}
