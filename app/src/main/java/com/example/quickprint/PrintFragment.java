package com.example.quickprint;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrintFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<Document> documentList;

    private FirebaseFirestore mFirestore;
    private CollectionReference documents;

    private static final String TAG = PrintFragment.class.getName();

    public PrintFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Print.
     */
    // TODO: Rename and change types and number of parameters
    public static PrintFragment newInstance(String param1, String param2) {
        PrintFragment fragment = new PrintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_print, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getEmail() == null) {
            RelativeLayout relativeLayout = view.findViewById(R.id.relLayout);

            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                View child = relativeLayout.getChildAt(i);
                child.setVisibility(View.GONE);
            }

            view.findViewById(R.id.textViewQuickPrintLogin).setVisibility(View.VISIBLE);
            view.findViewById(R.id.Forbidden).setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.printNew).setOnClickListener(v -> print(v));
        view.findViewById(R.id.preview).setOnClickListener(v -> preview(v));

        recyclerView = view.findViewById(R.id.recyclerView);
        documentList = new ArrayList<>();

        getDocumentsData();

        adapter = new DocumentAdapter(requireContext(), documentList, (DocumentAdapter.OnDocumentActionListener) getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void print(View view) {
        Intent intent = new Intent(requireContext(), PrintActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public  void preview(View view) {
        Intent intent = new Intent(requireContext(), ChooseAndPreviewActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public void getDocumentsData() {
        mFirestore = FirebaseFirestore.getInstance();
        documents = mFirestore.collection("Documents");

        Query getDocuments = documents.whereEqualTo("owner", user.getEmail());

        getDocuments.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    documentList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        Document doc = new Document(name);
                        doc.setId(document.getId());
                        documentList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public DocumentAdapter getAdapter() {
        return adapter;
    }
}