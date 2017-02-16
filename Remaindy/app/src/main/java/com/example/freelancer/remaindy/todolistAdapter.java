package com.example.freelancer.remaindy;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class todolistAdapter extends BaseAdapter {
    public List<Todoobject> todoitems;
    public Activity activity;
    public ImageView popuplist;
    public todolistAdapter(List<Todoobject> todoitems, Activity activity) {
        this.todoitems = todoitems;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return todoitems.size();
    }

    @Override
    public Object getItem(int position) {
        return todoitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).
                    inflate(R.layout.listitem, parent, false);
        }
        TextView title= (TextView)convertView.findViewById(R.id.title);
        popuplist=(ImageView)convertView.findViewById(R.id.overflowicon);
        String val=todoitems.get(position).getTitle();
        Log.e("adapter",val);
        title.setText(val);
        final View itemview=convertView;
        popuplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view,position,itemview);
            }
        });
        return convertView;
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, final int position,View convertview ) {
        // inflate menu
        PopupMenu popup = new PopupMenu(activity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.channeledit, popup.getMenu());
        popup.setOnMenuItemClickListener(new todolistAdapter.MyMenuItemClickListener(position,convertview));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;
        View itemview;
        public MyMenuItemClickListener(final int position,final View itemview) {
            this.position = position;
            this.itemview=itemview;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.edititem:
                    dilaogforedit(position,itemview);
                    return true;
                case R.id.removeitem:
                    removetodoitem(position,itemview);
                    return true;
                default:
            }
            return false;
        }
    }
    public void removetodoitem(int position,View itemview){
        Todoobject todoobject= todoitems.get(position);
        todoitems.remove(position);
        notifyDataSetChanged();
        SQLiteStorage sqLiteStorage= SQLiteStorage.getInstance(activity);
        sqLiteStorage.DeleteTodoItem(todoobject);
        notifyDataSetChanged();
        Toast.makeText(activity, "Todo Item Remove Sucessfull", Toast.LENGTH_SHORT).show();

    }
    public void dilaogforedit(final int position,final View itemview){
        String title="";
        final Dialog edittodoitem = new Dialog(activity);
        edittodoitem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        edittodoitem.setCanceledOnTouchOutside(false);
        edittodoitem.setCancelable(false);
        edittodoitem.setContentView(R.layout.createtodoitem);
        final TextView headerText = (TextView) edittodoitem
                .findViewById(R.id.dialog_title);
        final EditText text_dialog_content = (EditText) edittodoitem
                .findViewById(R.id.group_dialog_edit);
        final TextView donebutton = (TextView) edittodoitem
                .findViewById(R.id.donebutton);
        final TextView cancelbutton = (TextView) edittodoitem
                .findViewById(R.id.cancelbutton);
        text_dialog_content.setHint("Enter todo item title");
        headerText.setText("Edit Todo item");
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!text_dialog_content.getText().toString().trim().isEmpty()){
                    String title = text_dialog_content.getText().toString().trim();
                    if (!(title.equals(""))) {

                        edittodoitem(position,title,itemview);
                        edittodoitem.dismiss();
                    }


                }
                else{
                    Toast.makeText(activity, "Please enter Todo item", Toast.LENGTH_SHORT).show();
                }
            }

        });
        cancelbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                edittodoitem.cancel();
            }
        });
        edittodoitem.show();
    }
    public void edittodoitem(int position,String title,View itemview){
        Todoobject oldtodoobject= todoitems.get(position);
        TextView textView=(TextView)itemview.findViewById(R.id.title);
        textView.setText(title);
        Todoobject newtodoobject= new Todoobject();
        newtodoobject.setTitle(title);
        SQLiteStorage sqLiteStorage= SQLiteStorage.getInstance(activity);
        sqLiteStorage.Updatetitle(oldtodoobject,newtodoobject);
        this.todoitems=sqLiteStorage.getalltodoitems();
        Toast.makeText(activity, "Todo Item edit Sucessfull", Toast.LENGTH_SHORT).show();
    }
}
