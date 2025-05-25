package com.example.feelsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proglish2.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private final int userColor;
    private final int botColor;

    public MessageAdapter(List<Message> messages, int userColor, int botColor) {
        this.messages = messages;
        this.userColor = userColor;
        this.botColor = botColor;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSentByUser() ? 0 : 1;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.isSentByUser()) {
            showUserMessage(holder, message);
        } else {
            showBotMessage(holder, message);
        }

        message.markAsRead();
    }

    private void showUserMessage(MessageViewHolder holder, Message message) {
        holder.rightChatView.setVisibility(View.VISIBLE);
        holder.leftChatView.setVisibility(View.GONE);
        holder.rightChatTextView.setText(message.getContent());
        holder.rightTimeTextView.setText(message.getFormattedTime());
        holder.rightChatView.setBackgroundColor(userColor);
    }

    private void showBotMessage(MessageViewHolder holder, Message message) {
        holder.leftChatView.setVisibility(View.VISIBLE);
        holder.rightChatView.setVisibility(View.GONE);
        holder.leftChatTextView.setText(message.getContent());
        holder.leftTimeTextView.setText(message.getFormattedTime());
        holder.leftChatView.setBackgroundColor(botColor);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftChatTextView, rightChatTextView;
        TextView leftTimeTextView, rightTimeTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftChatTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextView = itemView.findViewById(R.id.right_chat_text_view);
            leftTimeTextView = itemView.findViewById(R.id.left_time_text_view);
            rightTimeTextView = itemView.findViewById(R.id.right_time_text_view);
        }
    }
}