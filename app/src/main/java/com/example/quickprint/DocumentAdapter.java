package com.example.quickprint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private final OnDocumentActionListener listener;
    private List<Document> documentList;
    private Context context;

    public DocumentAdapter(Context context, List<Document> documentList, OnDocumentActionListener listener) {
        this.context = context;
        this.documentList = documentList;
        this.listener = listener;
    }

    public interface OnDocumentActionListener {
        void onPrintAgain();
        void onDelete(Document doc);
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document doc = documentList.get(position);
        holder.textTitle.setText(doc.getName());

        holder.buttonPrintAgain.setOnClickListener(v -> {
            if (listener != null) listener.onPrintAgain();
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(doc);
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        Button buttonPrintAgain, buttonDelete;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            buttonPrintAgain = itemView.findViewById(R.id.buttonPrintAgain);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public void removeItem(Document doc) {
        int position = documentList.indexOf(doc);
        if (position != -1) {
            documentList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
