package com.zisantolunay.happybirthday;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    class addPhotoViewHolder extends RecyclerView.ViewHolder{

        Button buttonAddPhoto;
        ImageView buttonHelp;

        public addPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonAddPhoto = itemView.findViewById(R.id.button_add_photo);
            buttonHelp = itemView.findViewById(R.id.helpButton_addPhoto);
        }
    }

    class editButtonViewHolder extends RecyclerView.ViewHolder{

        TextView title, description;
        EditText editText;
        ImageView imageView, helpButton;


        public editButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.pager_item_title);
            description = itemView.findViewById(R.id.pager_item_description);
            editText = itemView.findViewById(R.id.editText_editButon);
            imageView = itemView.findViewById(R.id.pager_item_image);
            helpButton = itemView.findViewById(R.id.button_editButton);
        }
    }

    class addMessageViewHolder extends RecyclerView.ViewHolder{
        ImageView helpButton;
        Button addButton;
        Spinner spinner;
        EditText editText;

        public addMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            helpButton = itemView.findViewById(R.id.helpButton_addMessage);
            addButton = itemView.findViewById(R.id.addButton_addMessage);
            spinner = itemView.findViewById(R.id.spinner);
            editText = itemView.findViewById(R.id.editText_addMessage);
        }
    }

    private List<PagerItem> list;
    private static String TAG = "TAG_ViewPagerAdapter";
    private SelectPhotoListener listener;

    public ViewPagerAdapter(List<PagerItem> list, SelectPhotoListener listener){
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == PagerItem.ADDMESSAGE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_item_add_message, parent,false);
            view.setTag("ADDMESSAGETAG");
            return new addMessageViewHolder(view);
        }else if(viewType == PagerItem.ADDPHOTO){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_item_add_photo, parent,false);
            view.setTag("ADDPHOTOTAG");
            return new addPhotoViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_item_edit_button, parent,false);
            view.setTag("EDITBUTTONTAG");
            return new editButtonViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final Options options = Options.getInstance();
        if(holder instanceof addMessageViewHolder){

            final ArrayList<String> messages = new ArrayList<>();
            if(!options.messages.isEmpty()){
                messages.clear();
                messages.addAll(options.messages);
            }
            final SpinnerAdapter adapter = new SpinnerAdapter(holder.itemView.getContext(), messages);
            ((addMessageViewHolder)holder).spinner.setAdapter(adapter);

           ((addMessageViewHolder)holder).addButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String message = ((addMessageViewHolder)holder).editText.getText().toString();
                   ((addMessageViewHolder)holder).editText.setText("");
                   if(message.isEmpty()){
                       Toast.makeText(holder.itemView.getContext(), R.string.please_enter_message,Toast.LENGTH_SHORT).show();
                   }else{
                       options.addMessage(message);
                       messages.add(message);
                       adapter.notifyDataSetChanged();
                       ((addMessageViewHolder)holder).spinner.setSelection(adapter.getCount()-1);
                       Toast.makeText(holder.itemView.getContext(), R.string.message_added,Toast.LENGTH_SHORT).show();
                   }
               }
           });

            ((addMessageViewHolder)holder).helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHelper(holder.itemView.getContext(),PagerItem.ADDMESSAGE);
                }
            });

        }else if(holder instanceof addPhotoViewHolder){

            ((addPhotoViewHolder)holder).buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPhotoSelected();
                }
            });
            ((addPhotoViewHolder)holder).buttonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHelper(holder.itemView.getContext(),PagerItem.ADDPHOTO);
                }
            });
        }else if(holder instanceof editButtonViewHolder){
            final PagerItem item = list.get(position);

            ((editButtonViewHolder)holder).title.setText(item.getTitle());
            ((editButtonViewHolder)holder).description.setText(item.getDescription());
            ((editButtonViewHolder)holder).imageView.setImageResource(item.getImage());

            ((editButtonViewHolder)holder).editText.setHint(item.getHint());


            ((editButtonViewHolder)holder).helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHelper(holder.itemView.getContext(),item.getSecondType());
                }
            });
        }

    }

    private void openHelper(Context context, int type){
        Intent helpActivity = new Intent(context, HelperActivity.class);
        helpActivity.putExtra("type",type);
        context.startActivity(helpActivity);
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getType() == PagerItem.EDITBUTTON){
            return list.get(position).getSecondType();
        }else{
            return list.get(position).getType();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
