package com.example.polyOder.chatPoly.AIChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.databinding.LayoutItemChatLeftBinding;
import com.example.polyOder.model.Message;

import java.util.ArrayList;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolderMessage> {
    private ArrayList<Message> listMessage;

    private Context context;


    public MessageAdapter(ArrayList<Message> listMessage) {
        this.listMessage = listMessage;
    }

    public MessageAdapter(ArrayList<Message> listMessage,  Context context) {
        this.listMessage = listMessage;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageAdapter.ViewHolderMessage(LayoutItemChatLeftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolderMessage holder, int position) {
        Message message = listMessage.get(position);
        if (message == null) {
            return;
        } else {
            holder.initData(message);
        }
    }

    @Override
    public int getItemCount() {
        return listMessage.size();

    }

    public class ViewHolderMessage extends RecyclerView.ViewHolder {
        LinearLayout layoutChatLeft, layoutChatRight;
        TextView tvChatLeft, tvChatRight;
        public ViewHolderMessage(LayoutItemChatLeftBinding binding) {
            super(binding.getRoot());
            layoutChatLeft = binding.layoutChatLeft;
            layoutChatRight = binding.layoutChatRight;
            tvChatLeft = binding.tvChatLeft;
            tvChatRight = binding.tvChatRight;
        }

        void initData(Message message) {
            if(message.getSendBy().equals(Message.SEND_BY_ME)){
                layoutChatLeft.setVisibility(View.GONE);
                layoutChatRight.setVisibility(View.VISIBLE);
                tvChatRight.setText(message.getMessage());
            }else {
                layoutChatRight.setVisibility(View.GONE);
                layoutChatLeft.setVisibility(View.VISIBLE);
                tvChatLeft.setText(message.getMessage());
            }


        }
    }
}
